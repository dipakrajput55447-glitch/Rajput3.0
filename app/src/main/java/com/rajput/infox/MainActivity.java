package com.rajput.infox;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private MediaPlayer mediaPlayer;
    private static final String HOME_URL = "https://dipakrajput55447-glitch.github.io/RajputInfox/";
    private static final String CONFIG_URL = "https://raw.githubusercontent.com/dipakrajput55447-glitch/Rajput3.0/main/config.json";

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.bgm);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(0.2f, 0.2f);
                mediaPlayer.start();
            }
        } catch (Exception ignored) {}

        new CheckMaintenance().execute();

        webView = findViewById(R.id.webView);
        Button adminBtn = findViewById(R.id.adminBtn);
        
        webView.setBackgroundColor(Color.BLACK);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        webView.addJavascriptInterface(new Bridge(), "AndroidBridge");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
                String url = req.getUrl().toString();
                if (url.startsWith("https://dipakrajput55447-glitch.github.io")) return false;
                try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))); } catch (Exception ignored) {}
                return true;
            }
        });

        webView.loadUrl(HOME_URL);

        // Explicit Admin Button
        adminBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AdminActivity.class)));
    }

    private class CheckMaintenance extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL(CONFIG_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();
                return sb.toString();
            } catch (Exception e) { return null; }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.getBoolean("maintenance_mode")) {
                        String msg = json.getString("maintenance_msg");
                        new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                            .setTitle("Under Maintenance").setMessage(msg).setCancelable(false)
                            .setPositiveButton("Exit", (d, w) -> finish()).show();
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    private class Bridge {
        @JavascriptInterface
        public void saveFile(String filename, String content) {
            try {
                if (Build.VERSION.SDK_INT < 29) return;
                ContentValues v = new ContentValues();
                v.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
                v.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                v.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, v);
                if (uri != null) {
                    try (OutputStream os = getContentResolver().openOutputStream(uri)) {
                        os.write(content.getBytes(StandardCharsets.UTF_8));
                    }
                }
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Saved to Downloads", Toast.LENGTH_SHORT).show());
            } catch (Exception ignored) {}
        }

        // CREDITS SYSTEM CHECK
        @JavascriptInterface
        public boolean useCredit() {
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            int current = prefs.getInt("credits", 0);
            if (current > 0) {
                prefs.edit().putInt("credits", current - 1).apply();
                return true;
            }
            return false;
        }

        @JavascriptInterface
        public int getCredits() {
            return getSharedPreferences("AppPrefs", MODE_PRIVATE).getInt("credits", 0);
        }
    }

    @Override protected void onPause() { super.onPause(); if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause(); }
    @Override protected void onResume() { super.onResume(); if (mediaPlayer != null && !mediaPlayer.isPlaying()) mediaPlayer.start(); }
    @Override protected void onDestroy() { super.onDestroy(); if (mediaPlayer != null) { mediaPlayer.stop(); mediaPlayer.release(); } }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack();
        else super.onBackPressed();
    }
}
