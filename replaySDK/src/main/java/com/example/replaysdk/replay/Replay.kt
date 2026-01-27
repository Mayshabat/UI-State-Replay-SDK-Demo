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

    private const val TAG = "REPLAY_SDK"

    // --- state ---
    private val lock = Any()

    private var recording = false
    private var startedAt: Long = 0L
    private val events = mutableListOf<Event>()
    internal var currentScreen: String = "Unknown"

    private var replaying: Boolean = false
    internal var highlightTag: String? = null
        private set

    private var navigator: ReplayNavigator? = null

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    // ✅ replay-only feed
    private val replayClickFeed = mutableListOf<String>()

    fun init(baseUrl: String) {
        ApiClient.init(baseUrl)
        Log.d(TAG, "Initialized with base URL: $baseUrl")
    }

    fun enableActivityScreenTracking(app: Application) {
        ScreenTracker.install(app)
    }

    fun attachNavigator(navigator: ReplayNavigator) {
        this.navigator = navigator
        Log.d(TAG, "Navigator attached")
    }

    fun isRecording(): Boolean = recording
    fun isReplaying(): Boolean = replaying
    fun currentHighlightTag(): String? = highlightTag

    /**
     * Start recording a new session.
     * If replay is currently running - do nothing (prevent weird mixed states).
     */
    fun start() {
        if (replaying) {
            Log.w(TAG, "start() ignored: currently replaying")
            return
        }
        if (recording) {
            Log.w(TAG, "start() ignored: already recording")
            return
        }

        recording = true
        startedAt = System.currentTimeMillis()
        synchronized(lock) { events.clear() }
        Log.d(TAG, "Recording started")
    }


    private fun log(type: String, target: String? = null) {
        if (!recording) return
        val event = Event(
            type = type,
            screen = currentScreen,
            target = target,
            timestamp = System.currentTimeMillis()
        )
        synchronized(lock) { events.add(event) }
        Log.d(TAG, "Event logged: $type on $currentScreen (target: $target)")
    }

    fun trackScreen(screen: String) {
        if (screen == currentScreen) return
        currentScreen = screen
        if (recording) log(type = EventType.SCREEN)
    }


    fun trackClick(tag: String) {
        log(type = EventType.CLICK, target = tag)
    }

    fun stop(): Session {
        val now = System.currentTimeMillis()

        // if stop called without start, create a consistent empty session
        if (!recording && startedAt == 0L) {
            val snapshot = synchronized(lock) { events.toList() }
            return Session(
                startedAt = now,
                endedAt = now,
                events = snapshot
            )
        }

        recording = false
        val snapshot = synchronized(lock) { events.toList() }

        return Session(
            startedAt = if (startedAt == 0L) now else startedAt,
            endedAt = now,
            events = snapshot
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
            Log.d(TAG, "Upload successful. Session ID: $sessionId")
            sessionId
        } catch (e: Exception) {
            Log.e(TAG, "Upload failed", e)
            throw e
        }
    }

    suspend fun fetch(sessionId: String): Session {
        return try {
            val raw = ApiClient.service().getSession(sessionId)
            Log.d(TAG, "Fetch raw response: $raw")
            fromJson(raw)
        } catch (e: Exception) {
            Log.e(TAG, "Fetch failed for sessionId: $sessionId", e)
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
            Log.e(TAG, "JSON Decoding failed. Raw data: $raw", e)
            json.decodeFromString(raw)
        }
    }

    private fun extractSessionId(raw: String): String? {
        return runCatching {
            val element = json.parseToJsonElement(raw)
            element.jsonObject["sessionId"]?.jsonPrimitive?.content
        }.getOrNull()
    }

    fun getReplayClickFeedText(max: Int = 12): String {
        val items = synchronized(lock) { replayClickFeed.takeLast(max) }
        return if (items.isEmpty()) "No replay clicks yet"
        else items.joinToString("\n") { "• $it" }
    }


    suspend fun replay(session: Session) {
        val nav = navigator
            ?: error("ReplayNavigator not attached. Call Replay.attachNavigator(...) in the host app.")

        if (recording) {
            Log.w(TAG, "replay() called while recording. Stopping recording first.")
            stop()
        }

        val ordered = eventsOf(session)

        replaying = true
        highlightTag = null
        synchronized(lock) { replayClickFeed.clear() }


        try {
            delay(300)

            for (e in ordered) {
                when (e.type) {
                    EventType.SCREEN -> {
                        nav.goTo(e.screen)
                        delay(700)
                    }

                    EventType.CLICK -> {
                        val tag = e.target
                        highlightTag = tag

                        if (tag != null) synchronized(lock) { replayClickFeed.add("${e.screen} -> $tag") }


                        delay(600)
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

    // Public debug helpers (safe snapshots)
    fun getRecordedEvents(): List<Event> =
        synchronized(lock) { events.toList() }

    fun getLastSessionPreview(): Session? {
        val snapshot = synchronized(lock) { events.toList() }
        if (snapshot.isEmpty()) return null
        return Session(
            startedAt = startedAt,
            endedAt = System.currentTimeMillis(),
            events = snapshot
        )
    }

    fun getClickTags(): List<String> =
        synchronized(lock) {
            events.filter { it.type == EventType.CLICK }.mapNotNull { it.target }
        }

    fun getClicksAsText(max: Int = 10): String {
        val clicks = getClickTags().takeLast(max)
        return if (clicks.isEmpty()) "No clicks yet"
        else clicks.joinToString(separator = "\n") { "• $it" }
    }
}
