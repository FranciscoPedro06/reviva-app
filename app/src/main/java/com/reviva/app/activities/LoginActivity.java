package com.reviva.app.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.reviva.app.R;
import com.reviva.app.utils.FirebaseManager;


public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText senhaEditText;
    private Button btnEntrar;
    private TextView cadastrarText;
    private FirebaseAuth mAuth;
    private ImageView google;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.email);
        senhaEditText = findViewById(R.id.senha);
        btnEntrar = findViewById(R.id.btnEntrar);
        cadastrarText = findViewById(R.id.cadastrar);
        google = findViewById(R.id.google);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String senha = senhaEditText.getText().toString().trim();

                if (email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseManager.getInstance().loginUser(email, senha, new FirebaseManager.OnCompleteListener() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login bem-sucedido", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MyMemoriesActivity.class));
                            finish();
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Erro: email ou senha incorretos.", Toast.LENGTH_SHORT).show());
                    }
                });
            }
        });
        // Ação do botão Google
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Login com Google", Toast.LENGTH_SHORT).show();
            }
        });

        // Ação do texto "Cadastrar"
        cadastrarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Não faz nada, bloqueando o botão Voltar

    }
}