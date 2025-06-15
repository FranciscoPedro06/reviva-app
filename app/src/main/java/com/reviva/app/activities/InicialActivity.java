package com.reviva.app.activities; // Ajuste o pacote conforme seu projeto

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

// Certifique-se de que o nome da classe está correto, ex: InicialActivity
public class InicialActivity extends AppCompatActivity {

    private Button btnCadastrar;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial); // Verifique se o nome do layout está correto

        // Inicializar Views
        btnCadastrar = findViewById(R.id.btnCadastrar);
        btnLogin = findViewById(R.id.btnLogin);

        // Configurar OnClickListener para o botão Cadastrar
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cria um Intent para iniciar a Activity de Cadastro
                Intent intent = new Intent(InicialActivity.this, CadastroActivity.class);
                startActivity(intent);
            }
        });

        // Configurar OnClickListener para o botão Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cria um Intent para iniciar a Activity de Login
                Intent intent = new Intent(InicialActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}