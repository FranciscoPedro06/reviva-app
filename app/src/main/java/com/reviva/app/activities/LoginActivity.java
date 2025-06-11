package com.reviva.app.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.reviva.app.R;


public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText senhaEditText;
    private Button btnEntrar;
    private TextView cadastrarText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email);
        senhaEditText = findViewById(R.id.senha);
        btnEntrar = findViewById(R.id.btnEntar);
        cadastrarText = findViewById(R.id.cadastrar);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String senha = senhaEditText.getText().toString().trim();

                if (email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, senha)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, "Login bem-sucedido", Toast.LENGTH_SHORT).show();
                                // Redirecionar para a próxima tela
                                // startActivity(new Intent(this, MainActivity.class));
                                // finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Erro: email ou senha incorretos.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        // Ação do texto "Cadastrar"
        cadastrarText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Tela de cadastro", Toast.LENGTH_SHORT).show();
                // Abre a tela de cadastro
                // startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
            }
        });
    }
}
