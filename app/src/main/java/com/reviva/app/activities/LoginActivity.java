package com.reviva.app.activities;


import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
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
    private TextView esqueciSenha;
    private TextView cadastrarText;
    private FirebaseAuth mAuth;
    private ImageView google;
    private ImageView btnOlho;
    private boolean senhaVisivel = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.email);
        senhaEditText = findViewById(R.id.senha);
        btnEntrar = findViewById(R.id.btnEntrar);
        esqueciSenha = findViewById(R.id.esqueciSenha);
        cadastrarText = findViewById(R.id.cadastrar);
        google = findViewById(R.id.google);
        btnOlho = findViewById(R.id.btnOlho);

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
                            startActivity(new Intent(LoginActivity.this, MemoriaActivity.class));
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

        esqueciSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edtEmail = findViewById(R.id.email); // ou o ID correto do campo de e-mail
                String email = edtEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Digite seu e-mail", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Email de redefinição enviado", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Erro ao enviar email", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        btnOlho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (senhaVisivel) {
                    // Ocultar senha
                    senhaEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btnOlho.setImageResource(R.drawable.olho_fechado);
                } else {
                    // Mostrar senha
                    senhaEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnOlho.setImageResource(R.drawable.olho);
                }
                senhaVisivel = !senhaVisivel;

                // Move o cursor para o fim do texto
                senhaEditText.setSelection(senhaEditText.getText().length());
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