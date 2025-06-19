package com.reviva.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.reviva.app.R;

public class DeleteConfirmacaoActivity extends AppCompatActivity {

    private Button btnYes, btnNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout_delete_confirmation);

        btnYes = findViewById(R.id.btnYes);
        btnNo = findViewById(R.id.btnNo);

        btnYes.setOnClickListener(view -> {
            setResult(RESULT_OK);
            finish();
        });

        btnNo.setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}
