# Security baseline

## Consent and visibility
- Network participation must be off until the user explicitly enables it.
- Persistent participation must have a visible, ongoing notification and a clear stop action.
- Explain that the helper device's own location is used to create a sighting report before requesting location permission.
- Never hide background tracking or bypass Android permission/foreground-service controls.

## Data minimization
- Never put account identifiers, phone numbers, IMEI, owner coordinates, or stable device IDs in BLE advertisements.
- Use short-lived rotating identifiers.
- Encrypt sighting payloads to an owner-controlled epoch key; the backend should store ciphertext only.
- Keep only the minimum metadata needed for abuse prevention and delivery, with a documented expiry policy.

## Threat model limits
- A finder can infer that a nearby device is participating and may estimate proximity from radio signal strength.
- RSSI is noisy and must not be presented as exact distance.
- The backend can still observe network and timing metadata even if it cannot decrypt report contents.
- End-to-end encryption does not provide complete anonymity.
- Key rotation, revocation, replay prevention, rate limiting, and anti-stalking protections are required before public release.

## Release gate
Do not enable real location collection or public network participation until the protocol, permission flow, retention policy, and abuse protections have been implemented and reviewed.
