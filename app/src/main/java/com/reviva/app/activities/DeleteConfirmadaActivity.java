package com.reviva.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.reviva.app.R;

public class DeleteConfirmadaActivity extends AppCompatActivity {

    private Button btnBackToMemories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleted_memory); // Nome do XML

        btnBackToMemories = findViewById(R.id.btnBackToMemories);

        btnBackToMemories.setOnClickListener(v -> {
            Intent intent = new Intent(this, MyMemoriesActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
