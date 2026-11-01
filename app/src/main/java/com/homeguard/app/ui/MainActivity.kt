package com.homeguard.app.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.homeguard.app.core.DevicePolicyStatus
import com.homeguard.app.core.EventStore
import java.text.DateFormat
import java.util.Date

class MainActivity : Activity() {
    private lateinit var content: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDashboard()
    }

    private fun baseLayout(title: String) {
        val scroll = ScrollView(this)
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 36, 28, 32)
            setBackgroundColor(Color.rgb(17, 24, 39))
        }
        scroll.addView(content)
        setContentView(scroll)
        addText(title, 30f, Color.WHITE, true)
        addText("LOCAL HOME SECURITY", 12f, Color.LTGRAY, true)
    }

    private fun addText(value: String, size: Float, color: Int, bold: Boolean = false) {
        content.addView(TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(0, 10, 0, 10)
        })
    }

    private fun addButton(label: String, action: () -> Unit) {
        content.addView(Button(this).apply {
            text = label
            isAllCaps = false
            setOnClickListener { action() }
        }, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 8, 0, 8) })
    }

    private fun showDashboard() {
        baseLayout("HomeGuard")
        addText("HUB STATUS", 13f, Color.LTGRAY, true)
        addText("Not armed", 25f, Color.rgb(134, 239, 172), true)
        addText("First build: no sensors are connected yet.", 16f, Color.WHITE)
        addText(if (DevicePolicyStatus.isDeviceOwner(this)) "Managed mode: Device Owner active"
            else "Standard mode: Device Owner not configured", 14f, Color.rgb(191, 219, 254))
        addButton("Setup HomeGuard") { showSetup() }
        addButton("Devices") { showDevices() }
        addButton("Event history") { showEvents() }
        addButton("Settings") { showSettings() }
    }

    private fun showSetup() {
        baseLayout("Setup HomeGuard")
        addText("Choose how this phone will be used.", 17f, Color.WHITE)
        addText("Hub: central HomeGuard device. Controller: a phone used to view status and send commands. Role selection is informational in this starter build.", 14f, Color.LTGRAY)
        addButton("Set up as Hub") { saveRole("HUB", "Hub") }
        addButton("Set up as Controller") { saveRole("CONTROLLER", "Controller") }
        addButton("Choose HomeGuard as default Home app") {
            EventStore.append(this, "SETUP", "Opened Home app settings")
            startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
        }
        addButton("Back") { showDashboard() }
    }

    private fun saveRole(role: String, label: String) {
        getSharedPreferences("homeguard", MODE_PRIVATE).edit().putString("role", role).apply()
        EventStore.append(this, "SETUP", "Role set to $label")
        baseLayout("Setup saved")
        addText("This phone is marked as: $label", 18f, Color.WHITE)
        addText("Next, choose HomeGuard as the default Home app in Android settings.", 15f, Color.LTGRAY)
        addButton("Choose default Home app") { startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) }
        addButton("Back to dashboard") { showDashboard() }
    }

    private fun showDevices() {
        baseLayout("Devices")
        addText("No Bluetooth devices paired yet.", 17f, Color.WHITE)
        addText("BLE pairing and the HomeGuard protocol are not implemented in this starter build.", 14f, Color.LTGRAY)
        addButton("Open Bluetooth settings") { startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) }
        addButton("Back") { showDashboard() }
    }

    private fun showEvents() {
        baseLayout("Event history")
        val events = EventStore.read(this)
        if (events.length() == 0) addText("No events recorded yet.", 15f, Color.LTGRAY)
        for (i in events.length() - 1 downTo 0) {
            val item = events.optJSONObject(i) ?: continue
            val time = DateFormat.getDateTimeInstance().format(Date(item.optLong("timestamp")))
            addText("${item.optString("source")} • $time\n${item.optString("message")}", 14f, Color.WHITE)
        }
        addButton("Back") { showDashboard() }
    }

    private fun showSettings() {
        baseLayout("Settings")
        val role = getSharedPreferences("homeguard", MODE_PRIVATE).getString("role", "Not selected")
        addText("Phone role: $role", 16f, Color.WHITE)
        addText("Device Owner: ${DevicePolicyStatus.isDeviceOwner(this)}", 16f, Color.WHITE)
        addText("Device Admin active: ${DevicePolicyStatus.isAdminActive(this)}", 16f, Color.WHITE)
        addText("No system restrictions are applied by this starter build.", 14f, Color.LTGRAY)
        addButton("Open Android Home settings") { startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) }
        addButton("Open app settings") {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.parse("package:$packageName")
            })
        }
        addButton("Back") { showDashboard() }
    }
}
