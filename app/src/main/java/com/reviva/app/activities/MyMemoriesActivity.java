package com.reviva.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.reviva.app.R;
import com.reviva.app.models.Memory;
import com.reviva.app.utils.FirebaseManager;

import java.util.ArrayList;
import java.util.List;

public class MyMemoriesActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView btnView1, btnDelete1;
    private TextView btnView2, btnDelete2;
    private TextView btnView3, btnDelete3;

    private List<Memory> memories = new ArrayList<>();

    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_memories);

        firebaseManager = FirebaseManager.getInstance();

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        btnView1 = findViewById(R.id.btnView1);
        btnDelete1 = findViewById(R.id.btnDelete1);

        btnView2 = findViewById(R.id.btnView2);
        btnDelete2 = findViewById(R.id.btnDelete2);

        btnView3 = findViewById(R.id.btnView3);
        btnDelete3 = findViewById(R.id.btnDelete3);

        carregarMemoriasDoUsuario();

        configurarBotoes();
    }

    private void carregarMemoriasDoUsuario() {
        String userId = firebaseManager.getAuth().getCurrentUser() != null ?
                firebaseManager.getAuth().getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Como você separa memórias por tipo, vamos buscar todas as coleções 'memories_{mediaType}'
        // Supondo que você queira buscar os 3 tipos mais comuns, ou você pode ajustar para buscar todos.
        // Aqui vamos buscar separadamente e juntar resultados para simplificar o exemplo.

        memories.clear();

        // Exemplo simples buscando 3 tipos comuns: "image", "audio", "video"
        carregarMemoriasPorTipo(userId, "image");
        carregarMemoriasPorTipo(userId, "audio");
        carregarMemoriasPorTipo(userId, "video");
    }

    private void carregarMemoriasPorTipo(String userId, String mediaType) {
        firebaseManager.getFirestore()
                .collection("memories_" + mediaType)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (var doc : queryDocumentSnapshots) {
                            Memory memory = doc.toObject(Memory.class);
                            if (memory != null) {
                                memories.add(memory);
                            }
                        }
                        atualizarUI();
                    } else {
                        if (memories.isEmpty()) {
                            limparUI();
                            Toast.makeText(this, "Nenhuma memória encontrada", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao carregar memórias", Toast.LENGTH_SHORT).show();
                });
    }

    private void atualizarUI() {
        // Ordenar as memórias por unlockAt para exibir as desbloqueadas primeiro
        memories.sort((m1, m2) -> Long.compare(m1.getUnlockAt(), m2.getUnlockAt()));

        // Exibir até 3 memórias
        if (memories.size() > 0) {
            Memory m1 = memories.get(0);
            btnView1.setText(m1.getTitle());
            btnDelete1.setEnabled(true);
        } else {
            btnView1.setText("Sem memória");
            btnDelete1.setEnabled(false);
        }

        if (memories.size() > 1) {
            Memory m2 = memories.get(1);
            btnView2.setText(m2.getTitle());
            btnDelete2.setEnabled(true);
        } else {
            btnView2.setText("Sem memória");
            btnDelete2.setEnabled(false);
        }

        if (memories.size() > 2) {
            Memory m3 = memories.get(2);
            btnView3.setText(m3.getTitle());
            btnDelete3.setEnabled(true);
        } else {
            btnView3.setText("Sem memória");
            btnDelete3.setEnabled(false);
        }
    }

    private void limparUI() {
        btnView1.setText("Sem memória");
        btnDelete1.setEnabled(false);
        btnView2.setText("Sem memória");
        btnDelete2.setEnabled(false);
        btnView3.setText("Sem memória");
        btnDelete3.setEnabled(false);
    }

    private void configurarBotoes() {
        btnView1.setOnClickListener(v -> {
            if (memories.size() > 0) {
                Memory memory = memories.get(0);
                if (memory.isUnlocked()) {
                    abrirMemoria(memory);
                } else {
                    Toast.makeText(this, "Memória bloqueada. Aguarde a data!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDelete1.setOnClickListener(v -> {
            if (memories.size() > 0) {
                excluirMemoria(memories.get(0));
            }
        });

        btnView2.setOnClickListener(v -> {
            if (memories.size() > 1) {
                Memory memory = memories.get(1);
                if (memory.isUnlocked()) {
                    abrirMemoria(memory);
                } else {
                    Toast.makeText(this, "Memória bloqueada. Aguarde a data!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDelete2.setOnClickListener(v -> {
            if (memories.size() > 1) {
                excluirMemoria(memories.get(1));
            }
        });

        btnView3.setOnClickListener(v -> {
            if (memories.size() > 2) {
                Memory memory = memories.get(2);
                if (memory.isUnlocked()) {
                    abrirMemoria(memory);
                } else {
                    Toast.makeText(this, "Memória bloqueada. Aguarde a data!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnDelete3.setOnClickListener(v -> {
            if (memories.size() > 2) {
                excluirMemoria(memories.get(2));
            }
        });
    }

    private void abrirMemoria(Memory memoria) {
        Intent intent = new Intent(this, ViewMemoriaActivity.class);
        intent.putExtra("userId", memoria.getUserId());
        intent.putExtra("titulo", memoria.getTitle());
        intent.putExtra("descricao", memoria.getDescription());
        intent.putExtra("image", memoria.getMediaUrl());
        intent.putExtra("mediaType", memoria.getMediaType());
        intent.putExtra("unlockAt", memoria.getUnlockAt());
        startActivity(intent);
    }

    private void excluirMemoria(Memory memoria) {
        String collectionName = "memories_" + memoria.getMediaType();
        firebaseManager.getFirestore()
                .collection(collectionName)
                .document(memoria.getMemoryId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Memória excluída com sucesso!", Toast.LENGTH_SHORT).show();
                    memories.remove(memoria);
                    atualizarUI();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao excluir memória", Toast.LENGTH_SHORT).show();
                });
    }
}
