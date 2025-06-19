package com.reviva.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.reviva.app.R;
import com.reviva.app.models.User;
import com.reviva.app.utils.FirebaseManager;


public class CadastroActivity extends AppCompatActivity {

    private EditText nomeEditText, emailEditText, senhaEditText, confirmaSenhaEditText;
    private Button cadastrarBtn;
    private TextView loginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nomeEditText = findViewById(R.id.nome);
        emailEditText = findViewById(R.id.email);
        senhaEditText = findViewById(R.id.senha);
        confirmaSenhaEditText = findViewById(R.id.confirmaSenha);
        cadastrarBtn = findViewById(R.id.btnCadastrar);
        loginText = findViewById(R.id.login_link);

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
                    cadastrarUsuario(nome, email, senha);
                    Toast.makeText(CadastroActivity.this, "Cadastro completo com sucesso!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(CadastroActivity.this, LoginActivity.class));
                }
            }
        });
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CadastroActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Não faz nada, bloqueando o botão Voltar
    }
    private void cadastrarUsuario(String nome, String email, String password) {
        FirebaseManager.getInstance().registerUser(email, password, new FirebaseManager.OnRegisterCompleteListener() {
            @Override
            public void onSuccess(String uid) {
                User novoUsuario = new User(uid, nome, email, password);

                FirebaseManager.getInstance().saveUserData(novoUsuario, new FirebaseManager.OnCompleteListener() {
                    @Override
                    public void onSuccess() {
                            Toast.makeText(CadastroActivity.this, "Cadastro completo com sucesso!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(CadastroActivity.this, LoginActivity.class));
                            finish();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(CadastroActivity.this, "Erro ao salvar dados do usuário: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(CadastroActivity.this, "Erro ao cadastrar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
