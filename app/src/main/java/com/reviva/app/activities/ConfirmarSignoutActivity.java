package com.reviva.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.reviva.app.R;
import com.reviva.app.utils.FirebaseManager;

public class ConfirmarSignoutActivity extends AppCompatActivity {

    private Button btnSim; // Este será o botão "Sim"
    private Button btnNao; // Este será o botão "Não"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmar_signout);

        // CORREÇÃO AQUI: Troque os IDs para corresponderem ao XML
        btnSim = findViewById(R.id.btnYes); // No XML, o botão "Sim" tem id="btnYes"
        btnNao = findViewById(R.id.btnNo);  // No XML, o botão "Não" tem id="btnNo"

        // Ação para o botão "Sim" (Confirma o signout)
        btnSim.setOnClickListener(v -> {
            // Lógica para fazer o signout
            if (FirebaseManager.getInstance() != null && FirebaseManager.getInstance().getAuth() != null) {
                FirebaseManager.getInstance().signOut();
                Toast.makeText(ConfirmarSignoutActivity.this, "Sessão encerrada com sucesso.", Toast.LENGTH_SHORT).show();

                // Redireciona para a tela de Login
                Intent intent = new Intent(ConfirmarSignoutActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ConfirmarSignoutActivity.this, "Erro: Não foi possível realizar o logout.", Toast.LENGTH_SHORT).show();
            }
        });

        // Ação para o botão "Não" (Cancela o signout e volta para a tela anterior)
        btnNao.setOnClickListener(v -> {
            finish(); // Simplesmente fecha esta Activity e retorna para a anterior
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Escolha 'Sim' para sair ou 'Não' para permanecer.", Toast.LENGTH_SHORT).show();
    }
}