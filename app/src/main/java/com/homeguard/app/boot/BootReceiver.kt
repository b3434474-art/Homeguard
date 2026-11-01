package com.homeguard.app.boot
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.homeguard.app.core.EventStore
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) EventStore.append(context, "SYSTEM", "Device restarted")
    }
}