package com.reviva.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.reviva.app.R;

public class CadastroActivity extends AppCompatActivity {

    private EditText nomeEditText, emailEditText, senhaEditText, confirmaSenhaEditText;
    private Button cadastrarBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro); // Confere com o XML

        nomeEditText = findViewById(R.id.nome);
        emailEditText = findViewById(R.id.email);
        senhaEditText = findViewById(R.id.senha);
        confirmaSenhaEditText = findViewById(R.id.confirmaSenha);
        cadastrarBtn = findViewById(R.id.btnCadastrar);

        cadastrarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = nomeEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String senha = senhaEditText.getText().toString().trim();
                String confirmaSenha = confirmaSenhaEditText.getText().toString().trim();

                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmaSenha.isEmpty()) {
                    Toast.makeText(CadastroActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else if (!senha.equals(confirmaSenha)) {
                    Toast.makeText(CadastroActivity.this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
