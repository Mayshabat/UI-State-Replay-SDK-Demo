package com.example.replaysdk.replay

import android.app.Application
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object Replay {

    private var recording = false
    private var startedAt: Long = 0L
    private val events = mutableListOf<Event>()
    internal var currentScreen: String = "Unknown"

    // ✅ NEW: replay state + highlight
    private var replaying: Boolean = false
    internal var highlightTag: String? = null
        private set

    // ✅ NEW: navigator set by host app once
    private var navigator: ReplayNavigator? = null

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    fun init(baseUrl: String) {
        ApiClient.init(baseUrl)
        Log.d("REPLAY_SDK", "Initialized with base URL: $baseUrl")
    }

    /** Optional: auto track screens for multi-Activity apps */
    fun enableActivityScreenTracking(app: Application) {
        ScreenTracker.install(app)
    }

    // ✅ NEW
    fun attachNavigator(navigator: ReplayNavigator) {
        this.navigator = navigator
        Log.d("REPLAY_SDK", "Navigator attached")
    }

    fun isRecording(): Boolean = recording
    fun isReplaying(): Boolean = replaying
    fun currentHighlightTag(): String? = highlightTag

    fun start() {
        recording = true
        startedAt = System.currentTimeMillis()
        events.clear()
        Log.d("REPLAY_SDK", "Recording started")
    }

    private fun log(type: String, target: String? = null) {
        if (!recording) return
        val event = Event(
            type = type,
            screen = currentScreen,
            target = target,
            timestamp = System.currentTimeMillis()
        )
        events.add(event)
        Log.d("REPLAY_SDK", "Event logged: $type on $currentScreen (target: $target)")
    }

    fun trackScreen(screen: String) {
        currentScreen = screen
        log(type = "SCREEN")
    }

    fun trackClick(tag: String) {
        Log.d("REPLAY_SDK", "trackClick called for: $tag")
        log(type = "CLICK", target = tag)
    }

    fun stop(): Session {
        recording = false
        Log.d("REPLAY_SDK", "Recording stopped. Total events: ${events.size}")
        return Session(
            startedAt = startedAt,
            endedAt = System.currentTimeMillis(),
            events = events.toList()
        )
    }

    suspend fun stopAndUpload(): String {
        val session = stop()
        return upload(session)
    }

    suspend fun upload(session: Session): String {
        return try {
            val body = ApiClient.jsonBody(toJson(session))
            val response = ApiClient.service().postSession(body)
            val sessionId = extractSessionId(response) ?: response
            Log.d("REPLAY_SDK", "Upload successful. Session ID: $sessionId")
            sessionId
        } catch (e: Exception) {
            Log.e("REPLAY_SDK", "Upload failed", e)
            throw e
        }
    }

    suspend fun fetch(sessionId: String): Session {
        return try {
            val raw = ApiClient.service().getSession(sessionId)
            Log.d("REPLAY_SDK", "Fetch raw response: $raw")
            fromJson(raw)
        } catch (e: Exception) {
            Log.e("REPLAY_SDK", "Fetch failed for sessionId: $sessionId", e)
            throw e
        }
    }

    fun eventsOf(session: Session): List<Event> =
        session.events.sortedBy { it.timestamp }

    fun toJson(session: Session): String =
        json.encodeToString(session)

    fun fromJson(raw: String): Session {
        return try {
            val cleanedRaw = raw.trim().removeSurrounding("\"").replace("\\\"", "\"")
            json.decodeFromString<Session>(cleanedRaw)
        } catch (e: Exception) {
            Log.e("REPLAY_SDK", "JSON Decoding failed. Raw data: $raw", e)
            json.decodeFromString(raw)
        }
    }

    private fun extractSessionId(raw: String): String? {
        return runCatching {
            val element = json.parseToJsonElement(raw)
            element.jsonObject["sessionId"]?.jsonPrimitive?.content
        }.getOrNull()
    }

    /**
     * ✅ NEW: Library-driven replay (no app logic besides attaching navigator)
     */
    suspend fun replay(session: Session) {
        val nav = navigator ?: error("ReplayNavigator not attached. Call Replay.attachNavigator(...) in the host app.")

        val ordered = eventsOf(session)

        replaying = true
        highlightTag = null

        try {
            // small buffer
            delay(300)

            for (e in ordered) {
                when (e.type) {
                    "SCREEN" -> {
                        // screen name comes from e.screen
                        nav.goTo(e.screen)
                        delay(700)
                    }

                    "CLICK" -> {
                        val tag = e.target
                        highlightTag = tag
                        delay(600)

                        // for clicks: either action by tag OR ignore if not needed
                        if (tag != null) nav.performAction(tag)

                        delay(250)
                        highlightTag = null
                    }
                }
            }
        } finally {
            highlightTag = null
            replaying = false
        }
    }
}
