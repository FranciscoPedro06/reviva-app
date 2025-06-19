package com.reviva.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.reviva.app.R;

public class DeleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete); // sua tela intermediária, opcional

        // Aguarda 2 segundos e vai para a tela de confirmação
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(DeleteActivity.this, DeleteConfirmadaActivity.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}
