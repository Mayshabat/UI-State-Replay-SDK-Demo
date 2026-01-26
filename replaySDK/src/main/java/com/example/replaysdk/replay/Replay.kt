package com.example.replaysdk.replay

import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class UploadResult(
    val sessionId: String,
    val session: Session
)

object Replay {

    private var initialized = false

    private var recording = false
    private var startedAt: Long = 0L
    private val events = mutableListOf<Event>()

    private val jsonParser = Json {
        ignoreUnknownKeys = true
    }

    // -------------------------
    // Init
    // -------------------------
    fun init(baseUrl: String) {
        ApiClient.init(baseUrl)
        initialized = true
    }

    fun isRecording(): Boolean = recording

    private fun requireInit() {
        check(initialized) { "Replay.init(baseUrl) must be called before using upload/fetch/replayById." }
    }

    // -------------------------
    // Recording
    // -------------------------
    fun start() {
        recording = true
        startedAt = System.currentTimeMillis()
        events.clear()
    }

    /**
     * Kept for compatibility (internal usage). Apps should prefer trackClick().
     */
    fun log(type: String, screen: String) {
        if (!recording) return
        events.add(
            Event(
                type = type,
                screen = screen,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    /**
     * Main developer API: record a click/tag.
     */
    fun trackClick(tag: String) {
        log(type = "CLICK", screen = tag)
    }

    fun stop(): Session {
        recording = false
        return Session(
            startedAt = startedAt,
            endedAt = System.currentTimeMillis(),
            events = events.toList()
        )
    }

    /**
     * Convenience: stop recording + upload, returns sessionId (clean).
     */
    suspend fun stopAndUpload(): String {
        val session = stop()
        return upload(session)
    }

    /**
     * Best for demos/apps: stop + upload and also return the session object.
     */
    suspend fun stopUploadAndGetSession(): UploadResult {
        val session = stop()
        val id = upload(session)
        return UploadResult(sessionId = id, session = session)
    }

    // -------------------------
    // Network
    // -------------------------
    suspend fun upload(session: Session): String {
        requireInit()
        val json = toJson(session)
        val body = ApiClient.jsonBody(json)
        val response = ApiClient.service().postSession(body)

        // Expecting: {"sessionId":"..."}
        return extractSessionId(response) ?: response
    }

    suspend fun fetch(sessionId: String): Session {
        requireInit()
        val json = ApiClient.service().getSession(sessionId)
        return fromJson(json)
    }

    // -------------------------
    // Replay
    // -------------------------
    fun eventsOf(session: Session): List<Event> =
        session.events.sortedBy { it.timestamp }

    suspend fun replay(
        session: Session,
        delayMs: Long = 500,
        onEvent: (Event) -> Unit
    ) {
        val ordered = session.events.sortedBy { it.timestamp }
        for (e in ordered) {
            onEvent(e)
            delay(delayMs)
        }
    }

    /**
     * Developer can provide only sessionId. The SDK fetches and replays.
     */
    suspend fun replayById(
        sessionId: String,
        delayMs: Long = 500,
        onEvent: (Event) -> Unit
    ) {
        val session = fetch(sessionId)
        replay(session, delayMs, onEvent)
    }

    // -------------------------
    // Serialization
    // -------------------------
    fun toJson(session: Session): String =
        Json.encodeToString(session)

    fun fromJson(json: String): Session =
        jsonParser.decodeFromString(Session.serializer(), json)

    // -------------------------
    // Private
    // -------------------------
    private fun extractSessionId(raw: String): String? {
        return runCatching {
            val element = jsonParser.parseToJsonElement(raw)
            element.jsonObject["sessionId"]?.jsonPrimitive?.content
        }.getOrNull()
    }
}
