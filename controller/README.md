# HomeGuard Controller — Planned

The controller will be a separate Android app for BEN, GMA, and GRANDPA phones.

Planned functions:
- Display hub status and recent events.
- Explicit arm-home, arm-away, and disarm requests.
- Show enrolled devices, battery, and last-seen status.
- Require local user authentication before sensitive actions.
- Connect locally to the hub; cloud relay is optional and off by default.

Transport and authorization are not implemented. The controller must never claim a command succeeded until the hub acknowledges it.
