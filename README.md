# YouTube Music (WebView)

Android Java + WebView starter with YouTube Data API metadata search. Playback is limited to official YouTube playback or separately authorized audio sources.

## Build
Open in Android Studio or run `gradle assembleDebug` with JDK 17 and Android SDK 35. GitHub Actions builds a debug APK on push to `main`.

Add your YouTube Data API v3 key in the app settings field. For distribution, avoid embedding API keys in client apps; use a restricted server-side proxy.
