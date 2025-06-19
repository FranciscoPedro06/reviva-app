package com.reviva.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.reviva.app.R;
import com.reviva.app.models.Memory;
import com.reviva.app.utils.FirebaseManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyMemoriesActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_DELETE_CONFIRM = 1001;

    private Memory memoriaParaDeletar;
    private ImageView btnBack;
    private TextView tvDateAvailable, btnView1, tvTitle1, btnDelete1;
    private TextView tvDateBlocked1, btnView2, tvTitle2, btnDelete2;
    private TextView tvDateBlocked2, btnView3, tvTitle3, btnDelete3;
    private List<Memory> memories = new ArrayList<>();

    private FirebaseManager firebaseManager;
    private int tiposDeMidiaEsperados = 3;
    private int tiposCarregados = 0;
    private ListenerRegistration listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_memories);

        firebaseManager = FirebaseManager.getInstance();

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        tvDateAvailable = findViewById(R.id.tvDateAvailable);
        btnView1 = findViewById(R.id.btnView1);
        tvTitle1 = findViewById(R.id.tvTitle1);
        btnDelete1 = findViewById(R.id.btnDelete1);

        tvDateBlocked1 = findViewById(R.id.tvDateBlocked1);
        btnView2 = findViewById(R.id.btnView2);
        tvTitle2 = findViewById(R.id.tvTitle2);
        btnDelete2 = findViewById(R.id.btnDelete2);

        tvDateBlocked2 = findViewById(R.id.tvDateBlocked2);
        btnView3 = findViewById(R.id.btnView3);
        tvTitle3 = findViewById(R.id.tvTitle3);
        btnDelete3 = findViewById(R.id.btnDelete3);


        configurarBotoes();
    }



    @Override
    protected void onStart() {
        super.onStart();

        String userId = FirebaseManager.getInstance().getAuth().getCurrentUser().getUid();

        listener = FirebaseManager.getInstance()
                .getFirestore()
                .collection("memories")
                .whereEqualTo("userId", userId)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Erro ao carregar memórias", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (querySnapshot != null) {
                        memories.clear();
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            Memory memory = doc.toObject(Memory.class);
                            if (memory != null) {
                                memories.add(memory);
                            }
                        }
                        atualizarUI();
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (listener != null) listener.remove();
    }


    private void verificarSeTerminouCarregamento() {
        tiposCarregados++;
        if (tiposCarregados == tiposDeMidiaEsperados) {
            atualizarUI();
        }
    }






    private void atualizarUI() {
        // Ordenar as memórias por unlockAt para exibir as desbloqueadas primeiro
        memories.sort((m1, m2) -> Long.compare(m1.getUnlockAt(), m2.getUnlockAt()));
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        // Exibir até 3 memórias
        if (memories.size() > 0) {
            Memory m1 = memories.get(0);
            String dataFormatada = sdf.format(new Date(m1.getCreatedAt()));

            tvDateAvailable.setText(dataFormatada);
            tvTitle1.setText(m1.getTitle());
            btnDelete1.setEnabled(true);
            ImageView lockIcon1 = findViewById(R.id.lockIcon1);
            btnView1.setEnabled(podeAbrirMemoria(m1));
            btnView1.setAlpha(podeAbrirMemoria(m1) ? 1f : 0.5f);
            lockIcon1.setVisibility(podeAbrirMemoria(m1) ? View.GONE : View.VISIBLE);
            // <-- aqui
        } else {
            tvDateAvailable.setText("Indisponível");
            tvTitle1.setText("Sem memória");
            btnDelete1.setEnabled(false);
            btnDelete1.setAlpha( false ? 1f : 0.5f);
            btnView1.setEnabled(false); // <-- aqui
            btnView1.setAlpha( false ? 1f : 0.5f);
        }

        if (memories.size() > 1) {
            Memory m2 = memories.get(1);
            String dataFormatada = sdf.format(new Date(m2.getUnlockAt()));

            tvDateBlocked1.setText(dataFormatada);
            tvTitle2.setText(m2.getTitle());
            btnDelete2.setEnabled(true);
            ImageView lockIcon2 = findViewById(R.id.lockIcon2);
            btnView2.setEnabled(podeAbrirMemoria(m2));
            btnView2.setAlpha(podeAbrirMemoria(m2) ? 1f : 0.5f);
            lockIcon2.setVisibility(podeAbrirMemoria(m2) ? View.GONE : View.VISIBLE);

        } else {
            tvDateBlocked1.setText("Indisponível");
            tvTitle2.setText("Sem memória");
            btnDelete2.setEnabled(false);
            btnDelete2.setAlpha( false ? 1f : 0.5f);
            btnView2.setEnabled(false);
            btnView2.setAlpha( false ? 1f : 0.5f);
        }

        if (memories.size() > 2) {
            Memory m3 = memories.get(2);
            String dataFormatada = sdf.format(new Date(m3.getUnlockAt()));

            tvDateBlocked2.setText(dataFormatada);
            tvTitle3.setText(m3.getTitle());
            btnDelete3.setEnabled(true);
            ImageView lockIcon3 = findViewById(R.id.lockIcon3);
            btnView3.setEnabled(podeAbrirMemoria(m3));
            btnView3.setAlpha(podeAbrirMemoria(m3) ? 1f : 0.5f);
            lockIcon3.setVisibility(podeAbrirMemoria(m3) ? View.GONE : View.VISIBLE);


        } else {
            tvDateBlocked2.setText("Indisponível");
            tvTitle3.setText("Sem memória");
            btnDelete3.setEnabled(false);
            btnDelete3.setAlpha( false ? 1f : 0.5f);
            btnView3.setEnabled(false);
            btnView3.setAlpha( false ? 1f : 0.5f);
        }
    }

    private void limparUI() {
        tvDateAvailable.setText("Indisponível");
        tvTitle1.setText("Sem memória");
        btnDelete1.setEnabled(false);

        tvDateBlocked1.setText("Indisponível");
        tvTitle2.setText("Sem memória");
        btnDelete2.setEnabled(false);

        tvDateBlocked2.setText("Indisponível");
        tvTitle3.setText("Sem memória");
        btnDelete3.setEnabled(false);
    }
    private boolean podeAbrirMemoria(Memory memoria) {
        long agora = System.currentTimeMillis();  // Timestamp atual
        return memoria.getUnlockAt() <= agora;    // Pode abrir se unlockAt já passou
    }


    private void configurarBotoes() {
        btnView1.setOnClickListener(v -> {
            if (memories.size() > 0) {
                Memory memory = memories.get(0);
                if (podeAbrirMemoria(memory)) {
                        abrirMemoria(memory);
                } else {
                    Toast.makeText(this, "Memória bloqueada. Aguarde a data!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDelete1.setOnClickListener(v -> {
            if (memories.size() > 0) {
                memoriaParaDeletar = memories.get(0);
                abrirConfirmacaoDeletar();
            }
        });

        btnView2.setOnClickListener(v -> {
            if (memories.size() > 1) {
                Memory memory = memories.get(1);
                if (podeAbrirMemoria(memory)) {
                    abrirMemoria(memory);
                } else {
                    Toast.makeText(this, "Memória bloqueada. Aguarde a data!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDelete2.setOnClickListener(v -> {
            if (memories.size() > 1) {
                memoriaParaDeletar = memories.get(1);
                abrirConfirmacaoDeletar();
            }
        });

        btnView3.setOnClickListener(v -> {
            if (memories.size() > 2) {
                Memory memory = memories.get(2);
                if (podeAbrirMemoria(memory)) {
                    abrirMemoria(memory);
                } else {
                    Toast.makeText(this, "Memória bloqueada. Aguarde a data!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDelete3.setOnClickListener(v -> {
            if (memories.size() > 2) {
                memoriaParaDeletar = memories.get(2);
                abrirConfirmacaoDeletar();
            }
        });
    }

    private void abrirConfirmacaoDeletar() {
        Intent intent = new Intent(this, DeleteConfirmacaoActivity.class);
        startActivityForResult(intent, REQUEST_CODE_DELETE_CONFIRM);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DELETE_CONFIRM) {
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(this, DeleteActivity.class));
                // Usuário confirmou
                if (memoriaParaDeletar != null) {
                    excluirMemoria(memoriaParaDeletar);
                    memoriaParaDeletar = null;
                }
            } else {
                // Usuário cancelou, não faz nada
                memoriaParaDeletar = null;
            }
        }
    }

    private void abrirMemoria(Memory memoria) {
        Intent intent = new Intent(this, ViewMemoriaActivity.class);
        intent.putExtra("userId", memoria.getUserId());
        intent.putExtra("titulo", memoria.getTitle());
        intent.putExtra("descricao", memoria.getDescription());
        intent.putExtra("image", memoria.getMediaUrl());
        intent.putExtra("mediaType", memoria.getMediaType());
        intent.putExtra("unlockAt", memoria.getUnlockAt());
        intent.putExtra("categoria", memoria.getCategoria());
        startActivity(intent);
    }

    private void excluirMemoria(Memory memoria) {
        String collectionName = "memories";


        // Apagar o documento no Firestore
        firebaseManager.getFirestore()
                .collection(collectionName)
                .document(memoria.getMemoryId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Agora remover o arquivo do Storage
                    if (memoria.getMediaUrl() != null && !memoria.getMediaUrl().isEmpty()) {
                        FirebaseStorage.getInstance().getReference()
                                .child(memoria.getMediaUrl())
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this, "Memória excluída com sucesso!", Toast.LENGTH_SHORT).show();

                                    memories.remove(memoria);
                                    atualizarUI();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Erro ao excluir mídia do Storage", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Memória excluída (sem mídia associada)", Toast.LENGTH_SHORT).show();
                        memories.remove(memoria);
                        atualizarUI();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao excluir memória", Toast.LENGTH_SHORT).show();
                });
    }

}
