package com.rajput.infox;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.UUID;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        EditText masterId = findViewById(R.id.masterId);
        EditText masterPass = findViewById(R.id.masterPass);
        EditText creditAmount = findViewById(R.id.creditAmount);
        Button genBtn = findViewById(R.id.genBtn);
        Button addLocalBtn = findViewById(R.id.addLocalCreditsBtn);
        TextView resultKey = findViewById(R.id.resultKey);

        genBtn.setOnClickListener(v -> {
            String id = masterId.getText().toString().trim();
            String pass = masterPass.getText().toString().trim();
            String crStr = creditAmount.getText().toString().trim();
            int cr = crStr.isEmpty() ? 20 : Integer.parseInt(crStr);

            if (id.equals("RAJPUT") && pass.equals("DIPAK999")) {
                String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                // Format: RAJPUT-[CREDITS]-[RANDOM]
                String newKey = "RAJPUT-" + cr + "-" + random;
                resultKey.setText(newKey);
                Toast.makeText(this, "Key Generated. Add to config.json", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Invalid Admin ID or Password", Toast.LENGTH_SHORT).show();
                resultKey.setText("---");
            }
        });

        addLocalBtn.setOnClickListener(v -> {
            String id = masterId.getText().toString().trim();
            String pass = masterPass.getText().toString().trim();
            String crStr = creditAmount.getText().toString().trim();
            int cr = crStr.isEmpty() ? 20 : Integer.parseInt(crStr);

            if (id.equals("RAJPUT") && pass.equals("DIPAK999")) {
                SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                int current = prefs.getInt("credits", 0);
                prefs.edit().putInt("credits", current + cr).apply();
                Toast.makeText(this, cr + " Credits added to this device!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Invalid Admin ID or Password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
