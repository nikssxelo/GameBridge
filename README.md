# GameBridge — Android Gaming Browser

**GameBridge** is a native Android gaming browser developed by **Nikssxelo** that empowers mobile gamers to play PC browser games on touchscreens using fully customizable virtual controls (WASD joystick, action buttons, and simulated camera aim trackpads).

---

## 🎮 Features

### 1. High-Performance Gaming WebView
- **Hardware-Accelerated WebGL/HTML5 Engine:** Delivers smooth 60 FPS graphics with full JavaScript and DOM Storage enabled.
- **Secure Navigation Sandboxing:** Strictly restricts navigation to HTTP/HTTPS destinations, blocking unsafe intent hijacking.
- **Auto-Focus Engine:** Programmatically activates and focuses game canvases upon page load.
- **Error Recovery:** Integrated retry screen with graceful error explanations.

### 2. Virtual Touchscreen Controls
- **Virtual Analog Joystick:** 
  - Dynamic 8-direction detection with diagonal combinations (e.g., `W+A`, `W+D`, `S+A`, `S+D`).
  - Analog dead-zone filtering and seamless release lifecycle.
  - Continuous `keydown` while held, and reliable `keyup` dispatch on release.
- **Configurable Action Buttons:**
  - Default mappings for **Space (Jump)**, **Shift (Sprint)**, **E (Interact)**, **F (Action)**, **R (Reload)**, and **C (Crouch)**.
  - **Left Mouse Button (Fire)** and **Right Mouse Button (Aim)** simulation.
- **Aim / Camera Touch Trackpad:**
  - High-precision swipe touchpad delivering synthetic `mousemove` delta coordinates (`movementX`, `movementY`) to the canvas.
  - Adjustable sensitivity multiplier (0.5x – 3.0x).
- **Haptic Feedback:** Sub-millisecond tactile vibration on button presses and joystick engagement.

### 3. Built-in Preset Profiles
1. **FPS Profile:** WASD joystick + Aim Trackpad + Left Click (Fire) + Right Click (Aim) + Jump, Sprint, Reload, Use, Action.
2. **Platformer Profile:** Directional joystick + Space (Jump) + Shift (Run) + Attack (Z) + Skill (X) + Up/Down.
3. **Driving / Racing Profile:** Steering joystick + Gas (W) + Brake (S) + Drift Handbrake (Space) + Boost (Shift) + Reset (R).

### 4. Interactive Control Editor
- Drag-and-drop buttons and joysticks to any screen position.
- Normalized coordinate system (`0.0f` to `1.0f`) that automatically adapts to portrait, landscape, tablets, and phones.
- Real-time opacity slider (`20%` – `100%`) and joystick scaling.
- Add, edit labels, rebind keyboard codes, or remove buttons on the fly.
- Save custom control mappings for specific game domains or restore default presets with 1 click.

### 5. Curated Quick Launch
One-tap instant access to verified browser games including *Slither.io*, *1v1.LOL*, *Paper.io 2*, *Retro Bowl*, *2048*, *Cookie Clicker*, and interactive WebGL simulations.

---

## 🛠️ Technology Stack
- **Platform:** Native Android (minSdk 24, targetSdk 36)
- **Language:** Kotlin (100%)
- **UI Framework:** Jetpack Compose (Material Design 3 dark gaming aesthetic)
- **Engine:** Android `WebView` with custom `WebChromeClient` and `WebViewClient`
- **Control Bridge:** High-throughput JavaScript event injection (`KeyboardEvent`, `MouseEvent`)
- **Persistence:** Android `SharedPreferences` + JSON serialization

---

## 🚀 Setup & Build Instructions

### 1. Open in Android Studio
1. Launch **Android Studio Ladybug (or newer)**.
2. Select **File > Open...** and select this project directory.
3. Wait for Android Studio to initialize project indexing.

### 2. Sync Gradle
1. Android Studio will prompt to sync Gradle files automatically.
2. Alternatively, click **File > Sync Project with Gradle Files**.

### 3. Run on an Android Device
1. Enable **Developer Options** and **USB Debugging** on your Android phone or tablet.
2. Connect your device via USB cable or wireless debugging.
3. Select your device from the target device drop-down menu in Android Studio.
4. Click the green **Run (▶)** button or press `Shift + F10`.

### 4. Build a Debug APK
To generate a standalone APK for manual installation:
- In Android Studio: Navigate to **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
- Or via terminal:
  ```bash
  gradle assembleDebug
  ```
The output APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 5. Uploading Source to GitHub
```bash
git init
git add .
git commit -m "Initial commit: GameBridge Android Gaming Browser by Nikssxelo"
git branch -M main
git remote add origin https://github.com/<your-username>/GameBridge.git
git push -u origin main
```

---

## ⚠️ Virtual Input Compatibility Notice
GameBridge simulates keyboard and mouse inputs via standard DOM event dispatching (`KeyboardEvent`, `MouseEvent`). 
- **HTML5 Canvas & WebGL games** (2D platformers, arcade games, driving games, casual web games) generally have 100% full support.
- Games that strictly enforce `event.isTrusted === true` or require native OS Pointer Lock API may restrict synthetic inputs. GameBridge displays a built-in compatibility notice and offers preset adjustments to provide the best possible mobile gaming experience.

---

## 👨‍💻 Author
**Created by Nikssxelo**
