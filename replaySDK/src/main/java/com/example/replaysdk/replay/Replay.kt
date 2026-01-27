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


    private val json = Json {
        ignoreUnknownKeys = true  // מתעלם משדות שהשרת מחזיר והם לא בדאטה קלאס
        encodeDefaults = true     // שומר ערכי ברירת מחדל
        isLenient = true          // מאפשר קריאת JSON פחות נוקשה
    }

    /**
     * אתחול ה-SDK
     */
    fun init(baseUrl: String) {
        ApiClient.init(baseUrl)
        Log.d("REPLAY_SDK", "Initialized with base URL: $baseUrl")
    }

    /** תמיכה במעקב אוטומטי לאפליקציות מרובות Activities */
    fun enableActivityScreenTracking(app: Application) {
        ScreenTracker.install(app)
    }

    fun isRecording(): Boolean = recording

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

    /** קריאה לשינוי מסך - ב-Compose או ב-Navigation */
    fun trackScreen(screen: String) {
        currentScreen = screen
        log(type = "SCREEN")
    }

    /** תיעוד לחיצה */
    fun trackClick(tag: String) {
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
            // ניקוי התשובה מתווים שעלולים להפריע (כמו גרשיים מיותרים בתחילת/סוף מחרוזת)
            val cleanedRaw = raw.trim().removeSurrounding("\"").replace("\\\"", "\"")
            json.decodeFromString<Session>(cleanedRaw)
        } catch (e: Exception) {
            Log.e("REPLAY_SDK", "JSON Decoding failed. Raw data: $raw", e)
            // ניסיון פענוח ישיר למקרה שהניקוי לא היה נחוץ
            try {
                json.decodeFromString<Session>(raw)
            } catch (inner: Exception) {
                throw inner
            }
        }
    }

    private fun extractSessionId(raw: String): String? {
        return runCatching {
            val element = json.parseToJsonElement(raw)
            element.jsonObject["sessionId"]?.jsonPrimitive?.content
        }.getOrNull()
    }
}