package com.teslamap.youtube;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private WebView webView;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        webView = new WebView(this);
        setContentView(webView);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new Bridge(), "Android");
        webView.loadUrl("file:///android_asset/index.html");
    }

    private class Bridge {
        @JavascriptInterface public void search(String query, String apiKey) {
            executor.execute(() -> {
                try {
                    String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&maxResults=20&q="
                        + URLEncoder.encode(query, "UTF-8") + "&key=" + URLEncoder.encode(apiKey, "UTF-8");
                    HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
                    c.setConnectTimeout(15000); c.setReadTimeout(15000);
                    int code = c.getResponseCode();
                    InputStream in = code >= 200 && code < 300 ? c.getInputStream() : c.getErrorStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    byte[] buf = new byte[4096]; int n;
                    while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
                    in.close(); c.disconnect();
                    if (code < 200 || code >= 300) throw new Exception("YouTube API error " + code);
                    JSONObject root = new JSONObject(out.toString(StandardCharsets.UTF_8.name()));
                    JSONArray items = root.optJSONArray("items");
                    JSONArray result = new JSONArray();
                    if (items != null) for (int i=0; i<items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        JSONObject id = item.optJSONObject("id");
                        JSONObject sn = item.optJSONObject("snippet");
                        if (id == null || sn == null || !id.has("videoId")) continue;
                        JSONObject thumbs = sn.optJSONObject("thumbnails");
                        JSONObject medium = thumbs == null ? null : thumbs.optJSONObject("medium");
                        JSONObject row = new JSONObject();
                        row.put("id", id.optString("videoId"));
                        row.put("title", sn.optString("title"));
                        row.put("channel", sn.optString("channelTitle"));
                        row.put("thumbnail", medium == null ? "" : medium.optString("url"));
                        result.put(row);
                    }
                    final String json = result.toString();
                    runOnUiThread(() -> webView.evaluateJavascript("renderResults(" + JSONObject.quote(json) + ")", null));
                } catch (Exception e) {
                    final String msg = e.getMessage() == null ? "Search failed" : e.getMessage();
                    runOnUiThread(() -> webView.evaluateJavascript("showError(" + JSONObject.quote(msg) + ")", null));
                }
            });
        }
    }

    @Override public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
    @Override protected void onDestroy() {
        executor.shutdownNow();
        if (webView != null) webView.destroy();
        super.onDestroy();
    }
}
