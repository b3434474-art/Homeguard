package com.homeguard.app.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.homeguard.app.core.DevicePolicyStatus
import com.homeguard.app.core.EventStore
import com.homeguard.app.core.GuardMode
import com.homeguard.app.core.SecurityState
import java.text.DateFormat
import java.util.Date

class MainActivity : Activity() {
    private lateinit var content: LinearLayout
    private val bg = Color.rgb(24, 31, 29)
    private val panel = Color.rgb(39, 47, 44)
    private val muted = Color.rgb(183, 194, 188)
    private val green = Color.rgb(13, 111, 83)
    private val mint = Color.rgb(108, 245, 198)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showDashboard()
    }

    private fun rounded(color: Int, radius: Float = 22f): GradientDrawable =
        GradientDrawable().apply { setColor(color); cornerRadius = radius }

    private fun baseLayout(title: String) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }
        val scroll = ScrollView(this)
        content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 22, 22, 24)
            setBackgroundColor(bg)
        }
        scroll.addView(content)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(6, 6, 6, 6)
            setBackgroundColor(Color.rgb(29, 35, 33))
        }
        addNavItem(nav, "⌂", "Home") { showDashboard() }
        addNavItem(nav, "◷", "Activity") { showEvents() }
        addNavItem(nav, "▣", "Devices") { showDevices() }
        addNavItem(nav, "⚙", "Settings") { showSettings() }
        root.addView(nav, LinearLayout.LayoutParams(-1, 76))
        setContentView(root)
    }

    private fun addNavItem(parent: LinearLayout, symbol: String, label: String, action: () -> Unit) {
        val item = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(4, 5, 4, 5)
            setOnClickListener { action() }
            addView(TextView(this@MainActivity).apply {
                text = symbol
                textSize = 23f
                gravity = Gravity.CENTER
                setTextColor(mint)
            })
            addView(TextView(this@MainActivity).apply {
                text = label
                textSize = 11f
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
            })
        }
        parent.addView(item, LinearLayout.LayoutParams(0, -1, 1f))
    }

    private fun addText(value: String, size: Float, color: Int, bold: Boolean = false) {
        content.addView(TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(0, 7, 0, 7)
        })
    }

    private fun addSection(title: String, trailing: String = "") {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 18, 0, 8)
        }
        val heading = TextView(this).apply {
            text = title
            textSize = 19f
            setTextColor(Color.WHITE)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
        row.addView(heading, LinearLayout.LayoutParams(0, -2, 1f))
        if (trailing.isNotBlank()) {
            row.addView(TextView(this).apply {
                text = trailing
                textSize = 12f
                setTextColor(mint)
            })
        }
        content.addView(row)
    }

    private fun addCard(title: String, subtitle: String, accent: Int = panel) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 14, 18, 14)
            background = rounded(accent, 24f)
        }
        card.addView(TextView(this).apply {
            text = title
            textSize = 16f
            setTextColor(Color.WHITE)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        })
        card.addView(TextView(this).apply {
            text = subtitle
            textSize = 13f
            setTextColor(muted)
            setPadding(0, 5, 0, 0)
        })
        content.addView(card, LinearLayout.LayoutParams(-1, -2).apply {
            setMargins(0, 5, 0, 9)
        })
    }

    private fun addAction(label: String, action: () -> Unit, tint: Int = panel) {
        val button = TextView(this).apply {
            text = label
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setPadding(14, 13, 14, 13)
            background = rounded(tint, 16f)
            setOnClickListener { action() }
        }
        content.addView(button, LinearLayout.LayoutParams(-1, -2).apply {
            setMargins(0, 5, 0, 5)
        })
    }

    private fun addShortcutRow(
        firstTitle: String, firstIcon: String, firstAction: () -> Unit,
        secondTitle: String, secondIcon: String, secondAction: () -> Unit
    ) {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        fun tile(title: String, icon: String, action: () -> Unit): TextView =
            TextView(this).apply {
                text = "$title    $icon"
                textSize = 14f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER_VERTICAL
                setPadding(14, 18, 10, 18)
                background = rounded(panel, 20f)
                setOnClickListener { action() }
            }
        row.addView(tile(firstTitle, firstIcon, firstAction),
            LinearLayout.LayoutParams(0, -2, 1f).apply { setMargins(0, 4, 5, 4) })
        row.addView(tile(secondTitle, secondIcon, secondAction),
            LinearLayout.LayoutParams(0, -2, 1f).apply { setMargins(5, 4, 0, 4) })
        content.addView(row)
    }

    private fun showDashboard() {
        baseLayout("Home")
        val state = SecurityState.snapshot(this)
        val role = getSharedPreferences("homeguard", MODE_PRIVATE).getString("role", "Hub not configured")
        val status = state.mode.name.replace("_", " ")
        addText("HOMEGUARD  •  $role", 12f, muted, true)
        if (role == "Hub not configured" || role == "Not selected") {
            addCard("Finish setting up your hub", "Choose Hub or Controller to personalize this device.")
            addAction("Start setup", { showSetup() }, green)
        }

        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 20, 22, 20)
            background = rounded(green, 26f)
        }
        hero.addView(TextView(this).apply {
            text = "YOUR HOME"
            textSize = 12f
            setTextColor(Color.rgb(210, 255, 239))
        })
        hero.addView(TextView(this).apply {
            text = status
            textSize = 30f
            setTextColor(Color.WHITE)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(0, 8, 0, 8)
        })
        hero.addView(TextView(this).apply {
            text = "HomeGuard hub status"
            textSize = 13f
            setTextColor(Color.rgb(218, 245, 235))
        })
        hero.addView(TextView(this).apply {
            text = "DEMO MODE • No physical sensors connected"
            textSize = 11f
            setTextColor(Color.rgb(255, 224, 173))
            setPadding(0, 16, 0, 0)
        })
        content.addView(hero, LinearLayout.LayoutParams(-1, -2).apply {
            setMargins(0, 12, 0, 12)
        })

        addSection("Security controls", "SIMULATION")
        addAction("Arm Home", { SecurityState.armHome(this); showDashboard() })
        addAction("Arm Away", { SecurityState.armAway(this); showDashboard() })
        addAction("Disarm", { SecurityState.disarm(this); showDashboard() })
        addAction("Test front door sensor", {
            SecurityState.sensorTriggered(this, "Front door")
            showDashboard()
        })

        addSection("Devices", "VIEW ALL")
        addCard("No sensors connected", "Your door, window, and motion sensors will appear here.")
        addCard("Bluetooth hub", "BLE pairing and device communication are not available yet.")

        addSection("Recent activity", "VIEW ALL")
        addCard(state.lastEvent, if (state.updatedAtMillis > 0)
            DateFormat.getDateTimeInstance().format(Date(state.updatedAtMillis))
            else "Waiting for the first event")

        addSection("Shortcuts")
        addShortcutRow("Your home", "⌂", { showDashboard() }, "Devices", "▣", { showDevices() })
        addShortcutRow("Activity", "◷", { showEvents() }, "Setup", "⚙", { showSetup() })
        addText("Device Owner: ${DevicePolicyStatus.isDeviceOwner(this)}", 12f, muted)
    }

    private fun showSetup() {
        baseLayout("Setup")
        addText("Set up this phone", 24f, Color.WHITE, true)
        addText("Choose the role this device will have in your HomeGuard system.", 15f, muted)
        addAction("Set up as Hub", { saveRole("HUB", "Hub") })
        addAction("Set up as Controller", { saveRole("CONTROLLER", "Controller") })
        addAction("Choose HomeGuard as default Home app", {
            EventStore.append(this, "SETUP", "Opened Home app settings")
            startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
        })
        addAction("Back to Home", { showDashboard() })
    }

    private fun saveRole(role: String, label: String) {
        getSharedPreferences("homeguard", MODE_PRIVATE).edit().putString("role", role).apply()
        EventStore.append(this, "SETUP", "Role set to $label")
        baseLayout("Setup saved")
        addText("This phone is marked as: $label", 20f, Color.WHITE, true)
        addText("You can change the role later in setup.", 14f, muted)
        addAction("Choose default Home app", { startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) })
        addAction("Back to Home", { showDashboard() })
    }

    private fun showDevices() {
        baseLayout("Devices")
        addText("Your connected devices", 23f, Color.WHITE, true)
        addCard("No Bluetooth devices paired", "HomeGuard has not enrolled any sensors yet.")
        addText("The BLE protocol is being designed to support compatible door, window, and motion sensors.", 14f, muted)
        addAction("Open Bluetooth settings", { startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS)) })
        addAction("Back to Home", { showDashboard() })
    }

    private fun showEvents() {
        baseLayout("Activity")
        addText("Event history", 23f, Color.WHITE, true)
        val events = EventStore.read(this)
        if (events.length() == 0) addCard("No activity yet", "HomeGuard events will appear here.")
        for (i in events.length() - 1 downTo 0) {
            val item = events.optJSONObject(i) ?: continue
            val time = DateFormat.getDateTimeInstance().format(Date(item.optLong("timestamp")))
            addCard(item.optString("message"), "${item.optString("source")} • $time")
        }
        addAction("Back to Home", { showDashboard() })
    }

    private fun showSettings() {
        baseLayout("Settings")
        addText("HomeGuard settings", 23f, Color.WHITE, true)
        val role = getSharedPreferences("homeguard", MODE_PRIVATE).getString("role", "Not selected")
        addCard("Phone role", role ?: "Not selected")
        addCard("Device Owner", DevicePolicyStatus.isDeviceOwner(this).toString())
        addCard("Device Admin active", DevicePolicyStatus.isAdminActive(this).toString())
        addText("No system restrictions are applied by this build.", 13f, muted)
        addAction("Change phone role", { showSetup() })
        addAction("Android Home settings", { startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) })
        addAction("App settings", {
            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = android.net.Uri.parse("package:$packageName")
            })
        })
        addAction("Back to Home", { showDashboard() })
    }
}
