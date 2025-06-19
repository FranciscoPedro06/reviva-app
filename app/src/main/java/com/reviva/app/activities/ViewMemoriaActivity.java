package com.reviva.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.reviva.app.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;

import java.io.OutputStream;
public class ViewMemoriaActivity extends AppCompatActivity {

    private TextView titleMemory, dateMemory, contentMemory, btnBaixar;
    private ImageView imageMemory;

    private static final int REQUEST_DELETE_CONFIRM = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_memory);

        // UI
        titleMemory = findViewById(R.id.titleMemory);
        dateMemory = findViewById(R.id.dateMemory);
        contentMemory = findViewById(R.id.contentMemory);
        imageMemory = findViewById(R.id.imageMemory);
        btnBaixar = findViewById(R.id.btnBaixar);

        // Dados recebidos
        Intent intent = getIntent();
        String titulo = intent.getStringExtra("titulo");
        String descricao = intent.getStringExtra("descricao");
        String mediaUrl = intent.getStringExtra("image");
        String mediaType = intent.getStringExtra("mediaType");

        ImageView iconCategoria = findViewById(R.id.btnCategoria);
        String categoria = intent.getStringExtra("categoria");
        if (categoria == null) {
            Toast.makeText(this, "Categoria não recebida", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Categoria: " + categoria, Toast.LENGTH_SHORT).show();
        }


        if (categoria != null) {
            int iconRes;

            switch (categoria.toLowerCase()) {
                case "família":
                    iconRes = R.drawable.ic_familia;
                    break;
                case "amigos":
                    iconRes = R.drawable.ic_amigos;
                    break;
                case "viagens":
                    iconRes = R.drawable.ic_travel;
                    break;
                case "metas":
                    iconRes = R.drawable.ic_metas;
                    break;
                case "relacionamentos":
                    iconRes = R.drawable.ic_coracao;
                    break;
                default:
                    iconRes = R.drawable.ic_mais;
            }
            iconCategoria.setImageResource(iconRes);
        }



        // Data de desbloqueio
        long unlockAt = intent.getLongExtra("unlockAt", 0);
        String dataFormatada = unlockAt > 0
                ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(unlockAt))
                : "";

        // Preencher UI
        titleMemory.setText(titulo != null ? titulo : "Sem título");
        Log.d("ViewMemoria", "Descrição recebida: " + descricao);
        contentMemory.setText(descricao != null ? descricao : "");
        dateMemory.setText(!dataFormatada.isEmpty() ? "Desbloqueio: " + dataFormatada : "");

        // Exibir imagem
        if (mediaType != null && mediaType.equals("image")) {
            imageMemory.setVisibility(ImageView.VISIBLE);
            if (mediaUrl != null && !mediaUrl.isEmpty()) {
                if (mediaUrl.startsWith("http")) {
                    Picasso.get().load(mediaUrl).into(imageMemory);
                } else {
                    StorageReference ref = FirebaseStorage.getInstance().getReference().child(mediaUrl);
                    ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        Picasso.get().load(uri).into(imageMemory);
                    }).addOnFailureListener(e -> {
                        imageMemory.setImageResource(R.drawable.ampulhe);
                    });
                }
            } else {
                imageMemory.setImageResource(R.drawable.ampulhe);
            }
        } else {
            imageMemory.setImageResource(R.drawable.ampulhe);
        }

        // Botão voltar
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());


        btnBaixar.setOnClickListener(v -> {
            String mediaPath = getIntent().getStringExtra("image");
            if (mediaPath == null || mediaPath.isEmpty()) {
                Toast.makeText(this, "Caminho da mídia não disponível", Toast.LENGTH_SHORT).show();
                return;
            }

            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(mediaPath);
            salvarArquivoMediaStore(storageRef, this, storageRef.getName());
        });
    }

    public void salvarArquivoMediaStore(StorageReference storageRef, Context context, String nomeArquivo) {
        storageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
            Uri uri;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, nomeArquivo);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg"); // ou outro tipo, depende da mídia
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            } else {
                // Android 9 e anteriores, salva manualmente na pasta Downloads pública (requer permissão)
                Toast.makeText(context, "Use método alternativo para Android < 10", Toast.LENGTH_SHORT).show();
                return;
            }

            if (uri != null) {
                try (OutputStream out = context.getContentResolver().openOutputStream(uri)) {
                    out.write(bytes);
                    Toast.makeText(context, "Download salvo em Downloads", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(context, "Erro ao salvar arquivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Erro no download: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
