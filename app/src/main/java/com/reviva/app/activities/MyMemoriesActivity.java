package com.reviva.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.reviva.app.R;

public class MyMemoriesActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView btnView1, btnDelete1;
    private TextView btnView2, btnDelete2;
    private TextView btnView3, btnDelete3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_memories);

        // Voltar
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Botões da memória 1
        btnView1 = findViewById(R.id.btnView1);
        btnDelete1 = findViewById(R.id.btnDelete1);

        btnView1.setOnClickListener(v -> {
            Toast.makeText(this, "Memória bloqueada. Aguarde a data!", Toast.LENGTH_SHORT).show();
        });

        btnDelete1.setOnClickListener(v -> {
            Toast.makeText(this, "Memória 1 excluída", Toast.LENGTH_SHORT).show();
        });

        // Botões da memória 2
        btnView2 = findViewById(R.id.btnView2);
        btnDelete2 = findViewById(R.id.btnDelete2);

        btnView2.setOnClickListener(v -> {
            Toast.makeText(this, "Memória bloqueada. Aguarde a data!", Toast.LENGTH_SHORT).show();
        });

        btnDelete2.setOnClickListener(v -> {
            Toast.makeText(this, "Memória 2 excluída", Toast.LENGTH_SHORT).show();
        });

        // Botões da memória 3
        btnView3 = findViewById(R.id.btnView3);
        btnDelete3 = findViewById(R.id.btnDelete3);

        btnView3.setOnClickListener(v -> {
            Toast.makeText(this, "Memória bloqueada. Aguarde a data!", Toast.LENGTH_SHORT).show();
        });

        btnDelete3.setOnClickListener(v -> {
            Toast.makeText(this, "Memória 3 excluída", Toast.LENGTH_SHORT).show();
        });
    }

    /*private void abrirMemoria(String titulo, String data, String status, boolean bloqueada) {
        Intent intent = new Intent(context, ViewMemoriaActivity.class);
        intent.putExtra("userId", memoria.getUserId()); // ou onde você guarda o ID do dono
        intent.putExtra("titulo", memoria.getTitulo());
        intent.putExtra("data", memoria.getData());
        intent.putExtra("descricao", memoria.getDescricao());
        intent.putExtra("image", memoria.getImagePath());
        startActivity(intent);
    }*/
}
