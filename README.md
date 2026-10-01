# YouTube Music (WebView)

Android Java + WebView starter with YouTube Data API metadata search. YouTube Data API returns metadata, not direct audio stream URLs. Playback of YouTube content must use supported official playback; the app does not extract protected YouTube media.

## Stream metadata layer

- `extractor/JsonStreamExtractor.java` parses JSON supplied by an authorized audio provider.
- `extractor/StreamInfo.java` contains normalized title, uploader, duration, audio URL, and MIME type.
- The parser validates HTTPS URLs and audio MIME types. It does not discover, decrypt, or bypass protected media streams.
- **Made by Suliko Ananidze.**

This is a metadata parsing foundation only; it is not yet connected to a playback service or background player.

## Build

Open in Android Studio or run `gradle assembleDebug` with JDK 17 and Android SDK 35. GitHub Actions builds a debug APK on push to `main`.

Add your YouTube Data API v3 key in the app settings field. For distribution, avoid embedding API keys in client apps; use a restricted server-side proxy.
