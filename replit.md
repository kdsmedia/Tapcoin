# Rupiah Tapper

Android coin-tapping game with upgrades, robot rental, daily tasks, spin wheel, referrals, and withdrawals.

## Stack
- **Language**: Kotlin 2.2.10
- **UI**: Jetpack Compose (Material 3)
- **Architecture**: MVVM — ViewModel + Room + Coroutines
- **Backend/Services**: Firebase (AI, App Check), Retrofit + OkHttp, AdMob
- **Build**: Android Gradle Plugin 9.1.1, Gradle 8.12
- **minSdk**: 24 | **compileSdk**: 36 (Android 16)
- **Package**: `com.altomedia.altotap`

## Project layout
```
app/
  src/main/java/com/altomedia/altotap/
    MainActivity.kt
    data/          — Room entities, DAO, Database, Repository
    ui/
      GameViewModel.kt
      components/  — BannerAdView, BottomNavBar, TopHeader
      screens/     — Home, Profile, Referral, SpinWheel, Splash, Tasks, Upgrade, Withdrawal
      theme/       — Color, Theme, Type
    util/          — AdMobManager, SoundManager
  google-services.json   ← Firebase config (altomedia-8f793)
```

## Build environment (Replit)
- **JDK 17** — installed via Nix (`jdk17` in `replit.nix`)
- **Android SDK** — installed at `/home/runner/android-sdk`
  - `platforms;android-36`, `platforms;android-36.1`
  - `build-tools;36.0.0`, `platform-tools`
- **Gradle 9.3.1** — binary at `/tmp/gradle-9.3.1/bin/gradle` (AGP 9.1.1 requires Gradle ≥ 9.3.1)
- **local.properties** — points `sdk.dir` to `/home/runner/android-sdk`
- **debug.keystore** — generated at repo root for debug signing (gitignored)

### First-time setup (fresh container)
```bash
# 1. Install Android SDK cmdline-tools
mkdir -p /home/runner/android-sdk/cmdline-tools
wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O /tmp/cmdline-tools.zip
unzip -q /tmp/cmdline-tools.zip -d /tmp/cmdline-tools-extracted
mv /tmp/cmdline-tools-extracted/cmdline-tools /home/runner/android-sdk/cmdline-tools/latest

# 2. Accept licences & install SDK components
export ANDROID_HOME=/home/runner/android-sdk
export PATH=$ANDROID_HOME/cmdline-tools/latest/bin:$PATH
yes | sdkmanager --licenses > /dev/null
sdkmanager "platforms;android-36" "build-tools;36.0.0" "platform-tools"

# 3. Download Gradle 9.3.1
wget -q https://services.gradle.org/distributions/gradle-9.3.1-bin.zip -O /tmp/gradle-9.3.1-bin.zip
unzip -q /tmp/gradle-9.3.1-bin.zip -d /tmp

# 4. Create local.properties
echo "sdk.dir=/home/runner/android-sdk" > local.properties

# 5. Generate debug keystore (if missing)
keytool -genkeypair -keystore debug.keystore -alias androiddebugkey \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass android -keypass android \
  -dname "CN=Android Debug,O=Android,C=US"
```

### Build command
```bash
export ANDROID_HOME=/home/runner/android-sdk
/tmp/gradle-9.3.1/bin/gradle assembleDebug --no-daemon
```

> **Note**: Replit has no Android emulator — the app cannot be run/previewed here. The output is an APK at `app/build/outputs/apk/debug/app-debug.apk`.

## Google Sign-In
Login screen appears after splash. Uses **Credential Manager** (no Firebase Auth) — account data is stored locally on the device in `SharedPreferences` (`alto_auth_prefs`). The web client ID is auto-generated from `app/google-services.json` as `R.string.default_web_client_id`.

> **Note**: For Google Sign-In to work on a real device, the SHA-1 fingerprint of the signing keystore must be registered in the Firebase Console (Project Settings → Android App → Add Fingerprint). The debug keystore SHA-1 can be obtained with: `keytool -list -v -keystore debug.keystore -storepass android`

## Secrets / env vars
- `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD` — required only for release signing

## User preferences
<!-- Add any preferences here -->
