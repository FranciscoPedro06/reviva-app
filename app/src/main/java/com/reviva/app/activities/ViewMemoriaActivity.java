package com.reviva.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.reviva.app.R;
import com.squareup.picasso.Picasso;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ViewMemoriaActivity extends AppCompatActivity {

    private TextView titleMemory, dateMemory, contentMemory, btnDelete;
    private ImageView imageMemory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_memory);
        Intent intent = getIntent();


        // Referência aos elementos de UI
        titleMemory = findViewById(R.id.titleMemory);
        dateMemory = findViewById(R.id.dateMemory);
        contentMemory = findViewById(R.id.contentMemory);
        imageMemory = findViewById(R.id.imageMemory);
        btnDelete = findViewById(R.id.btnDelete);

        // Dados recebidos pela intent
        String titulo = intent.getStringExtra("titulo");
        String data = intent.getStringExtra("data");
        String texto = intent.getStringExtra("descricao");
        String imagemPath = intent.getStringExtra("image");

        titleMemory.setText(titulo != null ? titulo : "Título não disponível");
        dateMemory.setText(data != null ? data : "");
        contentMemory.setText(texto != null ? texto : "");

        if (imagemPath != null && !imagemPath.isEmpty()) {
            if (imagemPath.startsWith("http")) {
                Picasso.get().load(imagemPath).into(imageMemory);
            } else {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(imagemPath);
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Picasso.get().load(uri.toString()).into(imageMemory);
                }).addOnFailureListener(e -> {
                    e.printStackTrace();
                    imageMemory.setImageResource(R.drawable.ampulhe);
                });
            }
        } else {
            imageMemory.setImageResource(R.drawable.ampulhe);
        }

        btnDelete.setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra("delete", true);
            setResult(RESULT_OK, result);
            finish();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());



        btnDelete.setOnClickListener(v -> {
            Intent result = new Intent();
            result.putExtra("delete", true);
            setResult(RESULT_OK, result);
            finish();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}
