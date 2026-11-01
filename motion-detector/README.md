# HomeGuard Laptop Motion Detector — Planned

MOTION is a separate laptop-side service. The first version should process webcam frames locally and send only motion events to the hub; it should not upload or store video.

Planned safeguards:
- Visible camera-active indicator and an obvious stop control.
- No hidden recording, streaming, or cloud upload.
- Configurable detection zones and sensitivity.
- Send event metadata only (event type, source ID, local timestamp).
- Authenticated local connection to the hub.
- Clear offline queue and retry behavior.

The detector is not implemented yet. It must be tested with explicit camera permission and visible operation before use.
