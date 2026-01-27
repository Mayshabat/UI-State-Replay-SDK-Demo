
# Demo Application

The Demo Application is a **simple Android store app** whose only purpose
is to demonstrate how an application can integrate and use the
**UI State Replay SDK**.

The demo intentionally contains **minimal business logic**.
All recording, networking, and replay logic is handled by the SDK.

---

## Demo flow

Login → Shop → Product → Checkout

The user navigates normally through the app while the SDK records UI events.

---

## SDK integration in the demo

The demo app uses the SDK public API only.

### SDK initialization
Called once at app startup:

```kotlin
Replay.init(
    baseUrl = "https://ui-state-replay-sdk.onrender.com/"
)
```

## SDK controls (Floating Action Button)

The demo includes a floating overlay with the following actions:

-Start Recording
Begins recording UI interactions using the SDK.

-Stop & Upload
Stops the recording and uploads the session to the backend API.

-Replay
Fetches the last uploaded session and replays it automatically.

**These actions are implemented by calling** 
```kotlin
Replay.start()
Replay.stopAndUpload()
Replay.replayLast()

```
## Replay behavior

During replay, the demo application shows:

-Automatic navigation between screens

-Highlighted UI interactions

-Deterministic reproduction of the recorded user flow

## Design principle

The demo application does not:

-Implement recording logic

-Communicate directly with the backend

-Manage sessions or events

All core functionality is encapsulated inside the SDK.