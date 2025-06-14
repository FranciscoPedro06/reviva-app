package com.reviva.app.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.reviva.app.R;
import com.reviva.app.models.Memory;
import com.reviva.app.utils.FirebaseManager;
import com.reviva.app.utils.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class MemoriaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 2001;
    private static final int PICK_AUDIO_REQUEST = 2002;
    private static final int PICK_VIDEO_REQUEST = 1003;


    private ImageButton btnMenu, btnTexto, btnFoto, btnVideo, btnAudio;
    private Button btnSalvar, btnCancelar;
    private EditText edtTituloMemoria;
    private Uri selectedImageUri;
    private Uri selectedAudioUri;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private Uri selectedVideoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_memoria);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO
                }, 101);
            }
        }

        btnMenu = findViewById(R.id.btnMenu);
        btnTexto = findViewById(R.id.btnTexto);
        btnFoto = findViewById(R.id.btnFoto);
        btnVideo = findViewById(R.id.btnVideo);
        btnAudio = findViewById(R.id.btnAudio);
        btnSalvar = findViewById(R.id.btnSalvar);
        edtTituloMemoria = findViewById(R.id.edtTituloMemoria);
        btnCancelar = findViewById(R.id.btnCancelar);

        btnMenu.setOnClickListener(v -> Toast.makeText(this, "Menu selecionado", Toast.LENGTH_SHORT).show());
        btnTexto.setOnClickListener(v -> Toast.makeText(this, "Texto selecionado", Toast.LENGTH_SHORT).show());

        btnFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        btnVideo.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                intent.setType("video/*");
                startActivityForResult(intent, PICK_VIDEO_REQUEST);
        });

        btnAudio.setOnClickListener(v -> showAudioOptions());

        btnSalvar.setOnClickListener(v -> salvarMemoria());

        btnCancelar.setOnClickListener(v -> {
            edtTituloMemoria.setText("");
            selectedImageUri = null;
            selectedAudioUri = null;
            selectedVideoUri = null;
        });

    }

    private void showAudioOptions() {
        String[] options = {"Gravar Áudio", "Selecionar Áudio"};
        new AlertDialog.Builder(this)
                .setTitle("Escolha uma opção")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        startRecordingAudio();
                    } else {
                        pickAudioFromGallery();
                    }
                }).show();
    }

    private void startRecordingAudio() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            return;
        }

        try {
            audioFilePath = getExternalFilesDir(null).getAbsolutePath() + "audio_" + System.currentTimeMillis() + ".3gp";
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();

            Toast.makeText(this, "Gravação iniciada", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(this::stopRecordingAudio, 5000);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao iniciar gravação", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecordingAudio() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
                mediaRecorder = null;

                selectedAudioUri = Uri.fromFile(new File(audioFilePath));
                Toast.makeText(this, "Áudio gravado com sucesso", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao finalizar gravação", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void pickAudioFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    private void salvarMemoria() {
        String titulo = edtTituloMemoria.getText().toString().trim();
        if (titulo.isEmpty()) {
            Toast.makeText(this, "Preencha o título da memória", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseManager.getInstance().getAuth().getCurrentUser().getUid();
        if (selectedImageUri != null) {
            salvarImagem(titulo, userId);
            Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MemoriaActivity.this, ConfirmacaoActivity.class));
        } else if (selectedAudioUri != null) {
            salvarAudio(titulo, userId);
            Toast.makeText(this, "Áudio selecionado", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MemoriaActivity.this, ConfirmacaoActivity.class));
        } else if (selectedVideoUri != null) {
            salvarVideo(titulo, userId);
            Toast.makeText(this, "Vídeo selecionado", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MemoriaActivity.this, ConfirmacaoActivity.class));
        } else {
            Toast.makeText(this, "Selecione um tipo de momória!", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarImagem(String titulo, String userId) {
        byte[] imageBytes = StorageUtils.convertImageUriToByteArray(this, selectedImageUri);
        if (imageBytes == null) {
            Toast.makeText(this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "users/" + userId + "/memories/" + UUID.randomUUID() + ".jpg";
        FirebaseManager.getInstance().uploadFile(imageBytes, fileName, new FirebaseManager.OnUploadCompleteListener() {
            @Override
            public void onSuccess(String downloadUrl) {
                salvarNoFirestore(titulo, userId, downloadUrl, "image");
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MemoriaActivity.this, "Erro no upload da imagem", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(double progress) {}
        });
    }

    private void salvarAudio(String titulo, String userId) {
        byte[] audioBytes;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFilePath != null) {
            try {
                audioBytes = Files.readAllBytes(new File(audioFilePath).toPath());
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Erro ao ler áudio", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            audioBytes = StorageUtils.readFileUriToByteArray(this, selectedAudioUri);
        }

        if (audioBytes == null) {
            Toast.makeText(this, "Erro ao processar áudio", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "users/" + userId + "/memories/" + UUID.randomUUID() + ".3gp";
        FirebaseManager.getInstance().uploadFile(audioBytes, fileName, new FirebaseManager.OnUploadCompleteListener() {
            @Override
            public void onSuccess(String downloadUrl) {
                salvarNoFirestore(titulo, userId, downloadUrl, "audio");
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MemoriaActivity.this, "Erro no upload do áudio", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(double progress) {}
        });
    }

    private void salvarVideo(String titulo, String userId) {
        byte[] videoBytes = StorageUtils.readFileUriToByteArray(this, selectedVideoUri);

        if (videoBytes == null) {
            Toast.makeText(this, "Erro ao processar vídeo", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "users/" + userId + "/memories/" + UUID.randomUUID() + ".mp4";
        FirebaseManager.getInstance().uploadFile(videoBytes, fileName, new FirebaseManager.OnUploadCompleteListener() {
            @Override
            public void onSuccess(String downloadUrl) {
                salvarNoFirestore(titulo, userId, downloadUrl, "video");
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MemoriaActivity.this, "Erro no upload do vídeo", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(double progress) {}
        });
    }


    private void salvarNoFirestore(String titulo, String userId, String url, String tipo) {
        Memory memory = new Memory();
        memory.setMemoryId(UUID.randomUUID().toString());
        memory.setUserId(userId);
        memory.setTitle(titulo);
        memory.setDescription("");
        memory.setMediaUrl(url);
        memory.setMediaType(tipo);
        memory.setCreatedAt(System.currentTimeMillis());
        memory.setUnlockAt(System.currentTimeMillis());
        memory.setUnlocked(true);

        FirebaseManager.getInstance().saveMemory(memory, new FirebaseManager.OnCompleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MemoriaActivity.this, "Memória salva com sucesso", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MemoriaActivity.this, ConfirmacaoActivity.class));
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MemoriaActivity.this, "Erro ao salvar memória", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedAudioUri = data.getData();
            Toast.makeText(this, "Áudio selecionado", Toast.LENGTH_SHORT).show();
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show();
        } else if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedVideoUri = data.getData();
            Toast.makeText(this, "Vídeo selecionado", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRecordingAudio();
        } else {
            Toast.makeText(this, "Permissão de gravação negada", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        // Bloqueia botão voltar
    }
}
