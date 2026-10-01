package com.teslamap.youtube.extractor;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parses a source-provided JSON response into normalized stream metadata.
 * This deliberately does not fetch, discover, decrypt, or bypass protected media URLs.
 * The audioUrl must be supplied by an authorized source.
 * Made by Suliko Ananidze.
 */
public final class JsonStreamExtractor {
    public StreamInfo extract(String json) throws JSONException {
        if (json == null || json.trim().isEmpty()) {
            throw new JSONException("Empty stream response");
        }

        JSONObject root = new JSONObject(json);
        String title = root.optString("title", "").trim();
        String uploader = root.optString("uploader", "Unknown").trim();
        String audioUrl = root.optString("audioUrl", "").trim();
        String mimeType = root.optString("mimeType", "audio/mpeg").trim();
        long duration = root.optLong("duration", 0L);

        if (title.isEmpty()) {
            throw new JSONException("Missing stream title");
        }
        if (audioUrl.isEmpty()) {
            throw new JSONException("Missing authorized audio URL");
        }
        Uri uri = Uri.parse(audioUrl);
        if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) {
            throw new JSONException("Audio URL must be a valid HTTPS URL");
        }
        if (!mimeType.toLowerCase(java.util.Locale.ROOT).startsWith("audio/")) {
            throw new JSONException("Unsupported media type");
        }
        if (duration < 0L) {
            throw new JSONException("Duration cannot be negative");
        }

        return new StreamInfo(title, uploader.isEmpty() ? "Unknown" : uploader,
                duration, uri.toString(), mimeType);
    }
}
