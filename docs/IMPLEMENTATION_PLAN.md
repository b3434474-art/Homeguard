# HomeGuard Implementation Plan

## Phase 1 — Buildable hub foundation
- Android HOME/launcher entry point.
- Local event store.
- Guard-mode state model and UI simulation.
- No real sensor claims.

## Phase 2 — Protocol simulator
- Versioned BLE message envelope.
- Fake sensor devices and deterministic event generator.
- Multi-device event ordering, replay rejection, reconnect simulations.
- Unit tests for state transitions and malformed messages.

## Phase 3 — BLE transport
- Android GATT implementation validated against selected hardware.
- Explicit enrollment and challenge-response authentication.
- Device revocation, reconnect backoff, heartbeat and battery state.
- Real A11 multi-device and range testing.

## Phase 4 — Controller
- Separate Android controller app.
- Authenticated hub commands with acknowledgement.
- Device status and event history.

## Phase 5 — Laptop motion detector
- Local-only frame processing with visible camera indicator.
- Event-only bridge to the hub.
- No video storage or upload by default.

## Phase 6 — Device Owner / kiosk
- Document supported provisioning route for the exact phone and Android version.
- Test only on a spare/factory-reset device after recovery is understood.
- Add optional kiosk policies behind explicit setup; never auto-apply restrictions.

## Release rule
Do not label a feature production-ready until tests pass on intended hardware. Current security-state controls are simulation only.
