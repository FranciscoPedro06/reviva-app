package com.reviva.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.reviva.app.R;

public class MemoriaActivity extends AppCompatActivity {

    private ImageButton btnVoltar, btnTexto, btnFoto, btnVideo, btnAudio;
    EditText edtTituloMemoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_memoria);

        btnVoltar = findViewById(R.id.btnVoltar);
        btnTexto = findViewById(R.id.btnTexto);
        btnFoto = findViewById(R.id.btnFoto);
        btnVideo = findViewById(R.id.btnVideo);
        btnAudio = findViewById(R.id.btnAudio);

        edtTituloMemoria = findViewById(R.id.edtTituloMemoria);

        btnVoltar.setOnClickListener(v -> finish());

        btnTexto.setOnClickListener(v ->
                Toast.makeText(this, "Texto selecionado", Toast.LENGTH_SHORT).show());

        btnFoto.setOnClickListener(v ->
                Toast.makeText(this, "Foto selecionada", Toast.LENGTH_SHORT).show());

        btnVideo.setOnClickListener(v ->
                Toast.makeText(this, "Vídeo selecionado", Toast.LENGTH_SHORT).show());

        btnAudio.setOnClickListener(v ->
                Toast.makeText(this, "Gravação de áudio iniciada", Toast.LENGTH_SHORT).show());
    }
}
