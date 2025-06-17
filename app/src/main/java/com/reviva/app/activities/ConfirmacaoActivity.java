package com.reviva.app.activities;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.reviva.app.R;


public class ConfirmacaoActivity extends AppCompatActivity {


    private Button btnBackMemory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_confimar);

        btnBackMemory = findViewById(R.id.btnBackMemory);

        btnBackMemory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ConfirmacaoActivity.this, MemoriaActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Não faz nada, bloqueando o botão Voltar
    }
}
