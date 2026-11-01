package com.homeguard.app.core

/**
 * Local security-state model for the HomeGuard hub.
 * Foundation for UI and simulation; not yet a real alarm or sensor integration.
 */
enum class GuardMode { DISARMED, ARMING, ARMED_HOME, ARMED_AWAY, ENTRY_DELAY, ALARM }
enum class SensorKind { DOOR, WINDOW, MOTION }

data class SensorStatus(
    val id: String, val name: String, val kind: SensorKind,
    val online: Boolean, val open: Boolean = false, val batteryPercent: Int? = null
)

data class GuardSnapshot(
    val mode: GuardMode, val sensors: List<SensorStatus>,
    val updatedAtMillis: Long, val lastEvent: String
)

object SecurityState {
    private const val PREFS = "homeguard_security"
    private const val MODE = "mode"
    private const val LAST_EVENT = "last_event"
    private const val UPDATED = "updated"

    fun snapshot(context: android.content.Context): GuardSnapshot {
        val prefs = context.getSharedPreferences(PREFS, android.content.Context.MODE_PRIVATE)
        val mode = runCatching {
            GuardMode.valueOf(prefs.getString(MODE, GuardMode.DISARMED.name)!!)
        }.getOrDefault(GuardMode.DISARMED)
        return GuardSnapshot(mode, emptyList(), prefs.getLong(UPDATED, 0L),
            prefs.getString(LAST_EVENT, "System ready") ?: "System ready")
    }

    fun armHome(context: android.content.Context) =
        setMode(context, GuardMode.ARMED_HOME, "Demo state changed to Armed Home")

    fun armAway(context: android.content.Context) =
        setMode(context, GuardMode.ARMED_AWAY, "Demo state changed to Armed Away")

    fun disarm(context: android.content.Context) =
        setMode(context, GuardMode.DISARMED, "Demo state changed to Disarmed")

    /** Called by a future sensor adapter only after event validation. */
    fun sensorTriggered(context: android.content.Context, sensorName: String): GuardSnapshot {
        return when (snapshot(context).mode) {
            GuardMode.ARMED_HOME, GuardMode.ARMED_AWAY, GuardMode.ENTRY_DELAY ->
                setMode(context, GuardMode.ALARM, "Demo sensor event: $sensorName")
            else -> {
                EventStore.append(context, "SENSOR_SIMULATION", "$sensorName triggered while disarmed")
                snapshot(context)
            }
        }
    }

    private fun setMode(context: android.content.Context, mode: GuardMode, message: String): GuardSnapshot {
        val now = System.currentTimeMillis()
        context.getSharedPreferences(PREFS, android.content.Context.MODE_PRIVATE).edit()
            .putString(MODE, mode.name).putString(LAST_EVENT, message).putLong(UPDATED, now).apply()
        EventStore.append(context, "SECURITY_DEMO", message)
        return snapshot(context)
    }
}
