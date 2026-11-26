package com.rajput.infox;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.UUID;

public class AdminActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        EditText adminId = findViewById(R.id.adminId);
        Button loginBtn = findViewById(R.id.loginBtn);
        LinearLayout loginSection = findViewById(R.id.loginSection);
        LinearLayout adminControls = findViewById(R.id.adminControls);

        Switch maintenanceSwitch = findViewById(R.id.maintenanceSwitch);
        Button genDefaultBtn = findViewById(R.id.genDefaultKeyBtn);
        EditText customAmount = findViewById(R.id.customCreditAmount);
        Button genCustomBtn = findViewById(R.id.genCustomKeyBtn);
        TextView resultKey = findViewById(R.id.resultKey);

        maintenanceSwitch.setChecked(prefs.getBoolean("is_maintenance", false));

        loginBtn.setOnClickListener(v -> {
            if (adminId.getText().toString().trim().equals("RAJPUT")) {
                loginSection.setVisibility(View.GONE);
                adminControls.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Invalid Admin ID", Toast.LENGTH_SHORT).show();
            }
        });

        maintenanceSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("is_maintenance", isChecked).apply();
            Toast.makeText(this, isChecked ? "Maintenance ON" : "Maintenance OFF", Toast.LENGTH_SHORT).show();
        });

        genDefaultBtn.setOnClickListener(v -> {
            String key = "RAJPUT-20-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            resultKey.setText(key);
        });

        genCustomBtn.setOnClickListener(v -> {
            String amtStr = customAmount.getText().toString().trim();
            if (amtStr.isEmpty()) {
                Toast.makeText(this, "Enter amount", Toast.LENGTH_SHORT).show();
                return;
            }
            int amt = Integer.parseInt(amtStr);
            String key = "RAJPUT-" + amt + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            resultKey.setText(key);
        });
    }
}
