package com.rajput.infox;

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
        Button genBtn = findViewById(R.id.genBtn);
        TextView resultKey = findViewById(R.id.resultKey);

        genBtn.setOnClickListener(v -> {
            String id = masterId.getText().toString().trim();
            String pass = masterPass.getText().toString().trim();

            if (id.equals("RAJPUT") && pass.equals("DIPAK999")) {
                String prefix = "RAJPUT-PRO-";
                String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
                String newKey = prefix + random;
                resultKey.setText(newKey);
                Toast.makeText(this, "Key Generated. Copy and add to config.json", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Invalid Admin ID or Password", Toast.LENGTH_SHORT).show();
                resultKey.setText("---");
            }
        });
    }
}
