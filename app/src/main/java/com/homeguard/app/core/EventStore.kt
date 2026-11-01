package com.homeguard.app.core
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
object EventStore {
    private const val PREFS = "homeguard_events"
    private const val KEY_EVENTS = "events"
    private const val MAX_EVENTS = 200
    @Synchronized fun append(context: Context, source: String, message: String) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val events = read(context)
        events.put(JSONObject().apply {
            put("timestamp", System.currentTimeMillis())
            put("source", source)
            put("message", message)
        })
        while (events.length() > MAX_EVENTS) events.remove(0)
        prefs.edit().putString(KEY_EVENTS, events.toString()).apply()
    }
    fun read(context: Context): JSONArray {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_EVENTS, "[]") ?: "[]"
        return try { JSONArray(raw) } catch (_: Exception) { JSONArray() }
    }
}