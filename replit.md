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
- **Gradle 8.12** — binary at `/tmp/gradle-8.12/bin/gradle`
- **local.properties** — points `sdk.dir` to `/home/runner/android-sdk`

### Build command
```bash
export ANDROID_HOME=/home/runner/android-sdk
/tmp/gradle-8.12/bin/gradle assembleDebug --no-daemon
```

> **Note**: Replit has no Android emulator — the app cannot be run/previewed here. The output is an APK at `app/build/outputs/apk/debug/app-debug.apk`.

## Secrets / env vars
- `GEMINI_API_KEY` — optional; add to `.env` if Gemini AI features are needed (see `.env.example`)
- `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD` — required only for release signing

## User preferences
<!-- Add any preferences here -->
