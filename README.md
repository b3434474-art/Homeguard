# HomeGuard Android App — Starter 0.1.0

Fresh Android Studio project for a HomeGuard launcher and local hub foundation.

## Included
- Registers as an Android launcher and Home app.
- Basic dashboard and setup wizard.
- Local Hub / Controller role selection.
- Opens Android Home-app settings for user selection.
- Shows Device Owner and Device Admin status.
- Local event history (last 200 events).
- Records a boot event.

## Not implemented
- BLE GATT, pairing, authentication, reconnection, or multi-device testing.
- Real arming/disarming or sensor event processing.
- Device Owner provisioning workflow or kiosk restrictions.
- Controller app and laptop motion detector.

## Build
Open this folder in Android Studio with JDK 17 and Android SDK Platform 35, sync Gradle, then Build > Build APK(s). Gradle wrapper is not included in this starter source.

## Default launcher
Install the APK, open HomeGuard > Setup HomeGuard > Choose HomeGuard as default Home app. Android will ask the user to select it.

## Device Owner
Device Admin and Device Owner are different. This app declares a DeviceAdminReceiver but does not become Device Owner simply by installation or admin activation. Device Owner provisioning generally requires a freshly reset device and an approved provisioning method. Do not reset any phone until the APK, provisioning method, and recovery plan are prepared and reviewed. No policies are applied in this starter.

## Status
This is source code, not a tested APK. It has not been built or tested on a physical phone.