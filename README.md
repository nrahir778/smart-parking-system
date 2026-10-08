# 🚗 Smart Traffic Parking (3-Slot Visualizer & HC-05 IoT Gateway)

A modern Android application and real-time IoT gateway designed for **Arduino Uno + HC-05 Bluetooth**, visualizing parking occupancy with realistic traffic park graphics and streaming telemetry to a public GitHub Pages live web dashboard.

---

## 🚀 Building the APK using GitHub Actions

This repository includes an automated GitHub Actions workflow (`.github/workflows/build_apk.yml`) that compiles the APK automatically on the cloud.

### Option 1: Automatic Build on Push
Every time you push commits to the `main` or `master` branch:
1. GitHub Actions automatically checks out the repository.
2. Sets up JDK 17 and Gradle 9.3.1.
3. Decodes the keystore and runs `:app:assembleDebug`.
4. Uploads the generated APK (`SmartParking-Debug.apk`) as an artifact.

### Option 2: Run Workflow Manually (1-Click)
1. In your GitHub repository, click on the **Actions** tab at the top.
2. In the left sidebar, click **Assemble Android APK**.
3. Click the **Run workflow** dropdown on the right side and select **Branch: main**.
4. Click the green **Run workflow** button.
5. In ~2 minutes, the build will complete with a green checkmark (`✓`).
6. Click into the completed run and scroll down to **Artifacts** to download `SmartParking-Debug-APK`.

### Option 3: Create a GitHub Release
If you push a git tag starting with `v` (e.g. `v1.0.0`), GitHub Actions will build the APK and automatically create a GitHub Release with `SmartParking-Debug.apk` attached directly for download!

---

## 🌐 Public Live Link on GitHub Pages

The repository contains a standalone, real-time web dashboard inside the `docs/` folder (`docs/index.html`).

### How to Enable GitHub Pages (Free)
1. Go to your repository on GitHub.
2. Click **Settings** > **Pages** (under Code and automation).
3. Under **Build and deployment**:
   - **Source**: `Deploy from a branch`
   - **Branch**: `main` (or `master`)
   - **Folder**: `/docs`
4. Click **Save**.
5. Your public link will be live at: `https://<your-username>.github.io/<repo-name>/`

---

## ⚡ Real-Time Streaming Architecture

- **Phone as Gateway**: Connects to the HC-05 module over Bluetooth Classic RFCOMM (SPP) at 9600 baud.
- **Firebase Realtime Database**: The Android app sends HTTP `PUT` updates whenever a slot status changes.
- **Fast Real-Time Stream**: The GitHub Pages website listens via **Server-Sent Events (SSE)**, receiving updates in `<50ms` with zero polling latency (plus high-speed fallback).

---

## 🔌 Arduino Uno Hardware & Pinout Reference

| Component | Pin on Arduino Uno | Notes |
|---|---|---|
| **HC-05 TXD** | Pin `D2` | SoftwareSerial RX |
| **HC-05 RXD** | Pin `D3` | Via 2.2kΩ / 3.3kΩ voltage divider (3.3V logic) |
| **Slot 1 (HC-SR04)** | `TRIG: 4`, `ECHO: 5` | Threshold: <= 5.0 cm |
| **Slot 2 (HC-SR04)** | `TRIG: 6`, `ECHO: 7` | Threshold: <= 5.0 cm |
| **Slot 3 (HC-SR04)** | `TRIG: 9`, `ECHO: 10` | Threshold: <= 5.0 cm |
| **Active Buzzer (+)** | Pin `8` | Active HIGH (triggers when all 3 slots are full) |
| **MG995 Gate Servo** | Pin `11` | `0°` = OPEN, `90°` = CLOSED |
| **HC-05 Baud Rate** | `9600` Baud | Standard Classic SPP |

---

## 🎨 App Features
- **Photorealistic Top-Down Vehicles**: Crimson Sport Sedan (Slot 1), Midnight Luxury SUV (Slot 2), and Cyber Amber GT Coupe (Slot 3).
- **Traffic Park Environment**: Landscaped lawn verges, hedge bushes, concrete curbs, two-way road with zebra crosswalk, and stall wear markings.
- **Auto Dark / Light Theme**: Adapts to system display settings.
- **Real Hardware Only**: Runs purely on live data received from the HC-05 Arduino stream without demo simulations.
