# UI State Replay – Android SDK & Demo Application

UI State Replay is an Android SDK that allows developers to **record, store, and replay user interface interactions** without video recording.

This repository contains **both the Android SDK and a demo Android application** that showcases how to integrate and use the SDK.  
The backend API service is implemented in a **separate repository** and deployed to the cloud.

The SDK captures structured UI events (such as navigation, clicks, and screen transitions), uploads them to a cloud backend, and enables deterministic replay for debugging, UX analysis, and bug reproduction.

---

## ✨ Features

- Record UI events (navigation, actions, screen changes)
- Upload sessions to a cloud backend
- Retrieve recorded sessions from the server
- Replay user flows with automatic navigation and visual highlights
- No video recording
- No personal user data
- Lightweight and developer-friendly integration

---

## 🧱 Project Architecture

The project is composed of three main components:

### 1. Android SDK (Library)
- Captures UI events from the application
- Sends events to a REST API
- Fetches recorded sessions for replay
- Published as a public library via **JitPack**

### 2. Backend API Service
- RESTful API implemented with **Flask**
- Handles CRUD operations for recorded sessions
- Deployed to the cloud using **Render**

### 3. Database
- **MongoDB Atlas** (cloud-hosted)
- Stores sessions, timestamps, screens, and events

---

## 🗂 Repository Structure

- **replaySDK/** – Android SDK library
- **app/** – Demo Android application showcasing SDK usage
- **docs/** – Full project documentation published via GitHub Pages

---

## ☁️ Cloud Backend

**Base URL:**  
https://ui-state-replay-sdk.onrender.com

### Available Endpoints

- `GET /health` – Health check  
- `POST /sessions` – Create a new session  
- `GET /sessions` – Get recent sessions  
- `GET /sessions/{id}` – Get a specific session with events  
- `PUT /sessions/{id}` – Update a session  
- `DELETE /sessions/{id}` – Delete a session  

All data is exchanged in JSON format.

---

## 📦 Installation (via JitPack)

### Step 1: Add JitPack repository

```gradle
repositories {
    maven { url "https://jitpack.io" }
}
```

### Step 2: Add the dependency

```gradle
dependencies {
    implementation "com.github.Mayshabat:UI-State-Replay-SDK-Demo:v1.0.0"
}
```

##  Usage Example

###  Start recording
```kotlin
Replay.start()

```
### Log events
```kotlin
Replay.log("NAVIGATE", "HomeScreen")
Replay.log("ADD_TO_CART", "ProductScreen")
 ```

### Stop recording and upload
```kotlin
Replay.stopAndUpload()
```

### Replay a recorded session
```kotlin
Replay.replay(sessionId)
```

### Demo Application
The repository includes a demo Android application that demonstrates:

- Recording a real user flow
- Uploading a session to the backend
- Fetching a recorded session
- Replaying the flow with visual highlights and automatic navigation

###  Use Cases
- Debugging complex UI flows
- Reproducing hard-to-catch bugs
- UX and product behavior analysis
- QA automation support
- Developer tooling and SDK research
 
### 📋 Requirements

- Android API 26+
- Kotlin
- Internet permission
  
### Running the Backend Locally (Optional)
```bash
cd server
pip install -r requirements.txt
python app.py
```
## ⚙️ Configuration
The SDK and demo application communicate with the cloud backend API using the following base URL:
https://ui-state-replay-sdk.onrender.com

This base URL is configured in the SDK initialization or API client and can be replaced with a local server URL for development if needed.

## Documentation
Full project documentation (architecture, API reference, SDK usage, and demo instructions) is available here:

https://mayshabat.github.io/UI-State-Replay-SDK-Demo/

## Backend API Repository
https://github.com/Mayshabat/UI-State-Replay-Server
