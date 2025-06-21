package com.reviva.app.activities;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler; // Importar Handler
import android.content.Intent; // Importar Intent

import androidx.appcompat.app.AppCompatActivity;

import com.reviva.app.R;

public class DeleteActivity extends AppCompatActivity {

    private TextView tvDeleting;
    private ImageView imgTrashLoading;

    // Constante para o tempo de atraso (2 segundos = 2000 milissegundos)
    private static final long SPLASH_TIME_OUT = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete);

        tvDeleting = findViewById(R.id.tvDeleting);
        imgTrashLoading = findViewById(R.id.imgTrashLoading);

        // Carregar e iniciar a animação de rotação
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);
        imgTrashLoading.startAnimation(rotateAnimation);

        // AQUI: Adiciona o temporizador para navegar para a próxima tela
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Cria um Intent para ir para a tela de confirmação de exclusão
                // (assumindo que ConfirmacaoExclusaoActivity é a tela que mostra "Memória Apagada!")
                Intent i = new Intent(DeleteActivity.this, DeleteConfirmadaActivity.class); // <-- SUBSTITUA DeleteConfirmadaActivity.class se o nome for outro
                startActivity(i); // Inicia a próxima Activity
                finish(); // Finaliza esta Activity de carregamento para que o usuário não possa voltar a ela
            }
        }, SPLASH_TIME_OUT); // O atraso de 2 segundos
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (imgTrashLoading != null) {
            imgTrashLoading.clearAnimation();
        }
    }

    @Override
    public void onBackPressed() {
        // Bloqueia o botão voltar para que a tela de carregamento não seja fechada
        // Não faz nada.
    }
}