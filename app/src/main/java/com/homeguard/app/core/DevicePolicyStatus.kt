package com.homeguard.app.core
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import com.homeguard.app.admin.HomeGuardAdminReceiver
object DevicePolicyStatus {
    fun isDeviceOwner(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        return dpm.isDeviceOwnerApp(context.packageName)
    }
    fun isAdminActive(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        return dpm.isAdminActive(ComponentName(context, HomeGuardAdminReceiver::class.java))
    }
}