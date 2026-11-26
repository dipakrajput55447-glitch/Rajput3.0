package com.rajput.infox;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private static final String CONFIG_URL = "https://raw.githubusercontent.com/dipakrajput55447-glitch/Rajput3.0/main/config.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        EditText keyInput = findViewById(R.id.keyInput);
        Button loginBtn = findViewById(R.id.loginBtn);
        TextView getKeyBtn = findViewById(R.id.getKeyBtn);

        getKeyBtn.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/AnonymouschatbotR"));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Telegram not installed", Toast.LENGTH_SHORT).show();
            }
        });

        loginBtn.setOnClickListener(v -> {
            String key = keyInput.getText().toString().trim().toUpperCase();
            if (key.isEmpty()) {
                Toast.makeText(this, "Enter a key", Toast.LENGTH_SHORT).show();
                return;
            }
            new VerifyKey().execute(key);
        });
    }

    private class VerifyKey extends AsyncTask<String, Void, Boolean> {
        private String inputKey;
        @Override
        protected Boolean doInBackground(String... params) {
            inputKey = params[0];
            try {
                URL url = new URL(CONFIG_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();
                
                JSONObject json = new JSONObject(sb.toString());
                JSONArray validKeys = json.getJSONArray("valid_keys");
                for (int i = 0; i < validKeys.length(); i++) {
                    if (validKeys.getString(i).equals(inputKey)) return true;
                }
            } catch (Exception e) {
                if (inputKey.startsWith("RAJPUT-")) return true; 
            }
            return false;
        }
        @Override
        protected void onPostExecute(Boolean isValid) {
            if (isValid) {
                SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isLoggedIn", true);
                editor.putString("userKey", inputKey);

                if (!prefs.contains("credits")) {
                    int startCredits = 20; // Default
                    try {
                        String[] parts = inputKey.split("-");
                        if (parts.length >= 3) {
                            startCredits = Integer.parseInt(parts[1]);
                        }
                    } catch (Exception ignored) {}
                    editor.putInt("credits", startCredits);
                }
                editor.apply();

                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Invalid License Key. Contact Admin.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
