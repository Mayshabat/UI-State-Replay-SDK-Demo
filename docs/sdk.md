# SDK Usage

The **UI State Replay Android SDK** allows developers to:
- Record UI interactions as structured events (not video)
- Upload recorded sessions to a backend REST API
- Replay sessions deterministically to reproduce user flows (UI iterations)

---

## Installation

### Local module (this repository)
This project includes the SDK as a **library module**, already connected to the Demo app.

### Public distribution (JitPack)
> If/when published, add the exact JitPack/Gradle instructions here.

---

## Initialization

Call once at app startup (e.g., `Application` / `MainActivity`):

```kotlin
Replay.init(
    baseUrl = "https://ui-state-replay-sdk.onrender.com/"
)
```

## Recording UI events

***Start recording*** 
```kotlin
Replay.start()
```
***Stop & upload*** 
```kotlin
Replay.stopAndUpload()

```
***Replay*** 
```kotlin
Replay.replayLast()

```
## Replay event handling
During replay, the SDK emits events.
The host app maps those events into real app actions, for example:

-If event.type == "NAVIGATE" → navigate to the relevant screen

-If event.type == "CLICK" → highlight and/or trigger the target UI element by its id

This design keeps the SDK reusable across different applications.

## API used by the SDK (high level)

The SDK communicates with a REST API:

-POST /sessions – upload a new session

-GET /sessions – list sessions

-GET /sessions/<id> – fetch session by id

-DELETE /sessions/<id> – delete session (optional)
