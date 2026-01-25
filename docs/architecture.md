
# Architecture

The system is composed of three main layers:


Android Demo App
↓
UI State Replay SDK
↓
Backend REST API (Render)
↓
MongoDB Atlas


---

## Components

### Android SDK
- Captures UI events from the application
- Sends events to the backend API
- Replays recorded sessions with visual highlights

### Backend API
- RESTful API built with Flask
- Stores sessions in MongoDB Atlas
- Deployed to the cloud using Render

### Demo Application
- Android application demonstrating the SDK
- Allows recording, uploading, fetching, and replaying sessions

---

This separation allows developers to integrate the SDK into any Android application while using a centralized backend for storage and replay.
