
# API Documentation

Base URL:
https://ui-state-replay-sdk.onrender.com


---

## Health Check
**GET /health**

Response:
```json
{
  "status": "ok",
  "db": "connected"
}
```
---

## Create Session

**POST /sessions**
Response:
```json
{
  "events": [...]
}
```

Response:
```json
{
  "sessionId": "abc123"
}
```
---
## Get Session
**GET /sessions/{sessionId}**
```json
{
  "_id": "abc123",
  "sessionId": "abc123",
  "events": [...]
}
```


## Delete Session
**DELETE /sessions/{sessionId}**

---

## 📦 docs/sdk.md
```md
# SDK Usage

## Installation (JitPack)

Add JitPack to your repositories:
```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```
## Add the dependency:
implementation "com.github.Mayshabat:ui-state-replay-sdk:v1.0.0"

## Initialization
```json
Replay.init(
    context = this,
    baseUrl = "https://ui-state-replay-sdk.onrender.com"
)
```
## Recording
```json
Replay.start()
Replay.log("click", "login_button")
Replay.stop()

```
## Replay
```json
Replay.replay(sessionId)

```

---

## 📱 docs/demo.md
```md
# Demo Application

The demo application showcases how to use the UI State Replay SDK.

---

## Features Demonstrated
- Start recording UI interactions
- Stop and upload a session
- Fetch sessions from the backend
- Replay a recorded session with visual highlights

---

## How to Run
1. Open the project in Android Studio
2. Run the app on an emulator or physical device
3. Use the floating action buttons to:
   - Start Recording
   - Stop & Upload
   - Replay

---

## Expected Behavior
- A session ID is returned after upload
- Replay automatically navigates between screens
- Active UI elements are visually highlighted

This application serves as a reference implementation for SDK integration.
