# HomeGuard BLE Protocol — Draft v0.1

This protocol is hardware-neutral. Sensors have not been selected, so no sensor brand or proprietary radio format is assumed.

## Design goals
- BLE GATT transport, local-first operation, and multiple peripherals.
- Explicit protocol versioning and capability discovery.
- Authenticated commands and events; BLE pairing alone is not application authorization.
- Reconnection without silently trusting an unknown device.
- No cloud dependency for local alarm decisions.

## Device roles
- HUB: Samsung Galaxy A11 running HomeGuard.
- CONTROLLER: authorized phone app.
- SENSOR: door, window, or motion sensor implementing this protocol.
- MOTION: future laptop motion service using a separate authenticated event bridge.

## Proposed GATT characteristics
Stable 128-bit UUIDs will be assigned before hardware implementation. Placeholder UUIDs must not ship as production identifiers.

1. Device Info — protocol version, device ID, sensor kind, firmware version, capabilities.
2. Challenge — hub writes a fresh random challenge.
3. Authentication — device returns a cryptographic proof for the challenge.
4. Event — sensor notifies state changes and health reports.
5. Command — hub writes approved configuration commands.
6. Acknowledgement — device confirms command ID and result.

## Message envelope
Every application message should contain:
- protocolVersion
- deviceId
- messageId (unique per device)
- sequence (monotonically increasing per device)
- timestamp (informational; hub receipt time is authoritative)
- messageType
- payload

Reject malformed messages, unknown protocol versions, duplicate message IDs, and replayed sequence numbers.

## Pairing and authentication
1. User explicitly starts enrollment from the hub while physically near the device.
2. Android BLE pairing/bonding is used where supported, but a bond alone does not authorize HomeGuard commands.
3. Hub and device perform challenge-response authentication using a per-device secret established during deliberate enrollment.
4. Production implementation should use Android Keystore-backed key storage and standard cryptographic APIs. Never store plaintext secrets in source control, logs, SharedPreferences, or GitHub Actions artifacts.
5. A device can be revoked from the hub. Revocation must survive app restart.
6. Do not invent a custom cipher or reuse one static shared key across all sensors.

## Reconnection and multi-device behavior
- Persist enrolled device IDs and public metadata locally.
- Reconnect with bounded exponential backoff and jitter.
- Treat a reconnect as unauthenticated until challenge-response succeeds.
- Track last-seen time, battery level (if supported), connection state, and protocol compatibility.
- One peripheral failure must not block other devices.
- Enforce a device limit only after testing the A11's real BLE connection capacity.

## Sensor event types
- CONTACT_OPEN, CONTACT_CLOSED
- MOTION_DETECTED, MOTION_CLEARED
- TAMPER
- BATTERY_LOW, BATTERY_STATUS
- HEARTBEAT

Events are processed locally by the hub. The hub records its own receipt timestamp and appends validated events to event history.

## Test plan before real hardware
- Protocol version mismatch.
- Unknown and revoked device.
- Invalid authentication proof.
- Duplicate/replayed event.
- Disconnect during a command.
- Hub restart and peripheral reconnect.
- Two or more simulated peripherals sending events concurrently.
- Low battery and missed heartbeat.
- A11 real-world BLE range and connection stability.

## Current app implementation
- BLE scan screen can request Android scan permissions and discover nearby BLE advertisements.
- A phone running HomeGuard can broadcast a non-connectable TEST beacon using this protocol's test service UUID.
- Scanner labels that advertisement as HOMEGUARD TEST BEACON.
- The test beacon is only a discovery check; it is not a GATT connection, pairing, authentication, or sensor.

## Status
Protocol remains a draft. BLE discovery and a non-connectable test beacon are implemented. GATT connections, pairing flow, cryptographic enrollment, sensor compatibility, and production device authentication are not implemented yet.
