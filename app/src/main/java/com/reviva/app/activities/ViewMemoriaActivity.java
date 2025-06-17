package com.reviva.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reviva.app.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewMemoriaActivity extends AppCompatActivity {

    private TextView titleMemory, dateMemory, contentMemory, btnDelete;
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
        btnDelete = findViewById(R.id.btnDelete);

        // Dados recebidos
        Intent intent = getIntent();
        String titulo = intent.getStringExtra("titulo");
        String descricao = intent.getStringExtra("descricao");
        String mediaUrl = intent.getStringExtra("image");
        String mediaType = intent.getStringExtra("mediaType");

        // Data de desbloqueio
        long unlockAt = intent.getLongExtra("unlockAt", 0);
        String dataFormatada = unlockAt > 0
                ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(unlockAt))
                : "";

        // Preencher UI
        titleMemory.setText(titulo != null ? titulo : "Sem título");
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

        // Botão deletar com confirmação
        btnDelete.setOnClickListener(v -> {
            Intent confirmIntent = new Intent(ViewMemoriaActivity.this, DeleteConfirmacaoActivity.class);
            startActivityForResult(confirmIntent, REQUEST_DELETE_CONFIRM);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DELETE_CONFIRM && resultCode == RESULT_OK) {
            Intent result = new Intent();
            result.putExtra("delete", true);
            setResult(RESULT_OK, result);
            finish();
        }
    }
}
