package com.teslamap.youtube.extractor;

/**
 * Normalized metadata for a playable stream from a source the app is authorized to access.
 * Made by Suliko Ananidze.
 */
public final class StreamInfo {
    public final String title;
    public final String uploader;
    public final long durationSeconds;
    public final String audioUrl;
    public final String mimeType;

    public StreamInfo(String title, String uploader, long durationSeconds,
                      String audioUrl, String mimeType) {
        this.title = title;
        this.uploader = uploader;
        this.durationSeconds = durationSeconds;
        this.audioUrl = audioUrl;
        this.mimeType = mimeType;
    }
}
