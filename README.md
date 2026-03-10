# Rushd Android App

A production-ready Android WebView application for the Rushd website.

---

## Quick Start

### 1. Prerequisites
| Tool | Minimum Version |
|------|----------------|
| Android Studio | Hedgehog (2023.1.1) or newer |
| JDK | 17 |
| Android SDK | API 34 |
| Gradle | 8.4 (auto-downloaded) |

### 2. Set your website URL
Open `app/src/main/java/com/rushd/app/AppConfig.java` and update:

```java
public static final String WEBSITE_URL = "https://www.rushd.com"; // ← your URL
public static final String[] ALLOWED_DOMAINS = {
    "rushd.com",
    "www.rushd.com"   // ← your domains
};
```

### 3. Open in Android Studio
1. `File → Open` → select the `RushdApp` folder
2. Wait for Gradle sync to complete
3. Click **Run ▶** or press `Shift+F10`

---

## Building a Release APK

### Option A – Android Studio
1. `Build → Generate Signed Bundle / APK`
2. Choose **APK**
3. Create or select your keystore
4. Select **release** build variant
5. Click **Finish**

### Option B – Command Line
```bash
cd RushdApp

# macOS / Linux
./gradlew assembleRelease

# Windows
gradlew.bat assembleRelease
```

Signed APK output: `app/build/outputs/apk/release/app-release.apk`

> **Signing:** You must sign the APK with a keystore before distributing.  
> Create one via `Build → Generate Signed Bundle / APK → Create new...`

---

## Customisation Guide

### Branding
| What | Where |
|------|-------|
| App name | `res/values/strings.xml` → `app_name` |
| Brand colours | `res/values/colors.xml` |
| Logo | Replace `res/drawable/ic_logo.xml` with your SVG/PNG |
| Launcher icon | Replace files in `res/mipmap-*` folders |
| Splash duration | `AppConfig.SPLASH_DURATION_MS` |

### Allowed Domains
Add any sub-domains or partner domains that should open inside the WebView to `AppConfig.ALLOWED_DOMAINS`.  
Everything else opens in the device browser automatically.

### Permissions
The following permissions are declared:
- `INTERNET` – required for WebView
- `ACCESS_NETWORK_STATE` – required for offline detection
- `CAMERA` – only used if the website requests file upload with camera
- `READ_MEDIA_IMAGES` / `READ_EXTERNAL_STORAGE` – file picker

Remove `CAMERA` and storage permissions from `AndroidManifest.xml` if your website does not use file upload.

---

## Push Notifications (Firebase)

1. Create a Firebase project at https://console.firebase.google.com
2. Add an Android app with package name `com.rushd.app`
3. Download `google-services.json` → place it in the `app/` folder
4. In `app/build.gradle`, uncomment:
   ```groovy
   id 'com.google.gms.google-services'
   // ...
   implementation platform('com.google.firebase:firebase-bom:33.1.2')
   implementation 'com.google.firebase:firebase-messaging'
   ```
5. In `AndroidManifest.xml`, uncomment the `<service>` block for `RushdFirebaseMessagingService`
6. In `services/RushdFirebaseMessagingService.java`, uncomment the full class

---

## Google Play Compliance Notes

This app is built to avoid the "WebView wrapper" low-quality flag:

- ✅ Splash screen with branding animation
- ✅ Custom offline & error states
- ✅ Pull-to-refresh
- ✅ Back navigation within WebView
- ✅ File upload & download support
- ✅ HTTPS only (no cleartext traffic)
- ✅ Minimal, justified permissions
- ✅ ProGuard / R8 minification in release
- ✅ Adaptive launcher icon
- ✅ Target SDK 34 (Android 14)

---

## Project Structure

```
RushdApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/rushd/app/
│   │   │   ├── AppConfig.java          ← central config (URL, domains)
│   │   │   ├── SplashActivity.java
│   │   │   ├── MainActivity.java
│   │   │   ├── network/
│   │   │   │   └── NetworkMonitor.java
│   │   │   ├── webview/
│   │   │   │   ├── RushdWebViewClient.java
│   │   │   │   └── RushdWebChromeClient.java
│   │   │   └── services/
│   │   │       └── RushdFirebaseMessagingService.java
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_splash.xml
│   │   │   │   └── activity_main.xml
│   │   │   ├── values/
│   │   │   │   ├── strings.xml
│   │   │   │   ├── colors.xml
│   │   │   │   └── themes.xml
│   │   │   ├── drawable/
│   │   │   │   ├── ic_logo.xml
│   │   │   │   ├── ic_error.xml
│   │   │   │   ├── ic_wifi_off.xml
│   │   │   │   └── ic_notification.xml
│   │   │   └── xml/
│   │   │       ├── network_security_config.xml
│   │   │       ├── file_paths.xml
│   │   │       ├── backup_rules.xml
│   │   │       └── data_extraction_rules.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
└── gradle.properties
```
