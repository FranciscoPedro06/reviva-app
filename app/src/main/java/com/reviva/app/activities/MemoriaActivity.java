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
import android.view.View; // Importar View
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView; // Importar TextView, pois os itens do menu são TextViews
import android.widget.Toast;

import androidx.annotation.NonNull; // Para o método onRequestPermissionsResult
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat; // Para abrir/fechar o drawer do lado correto
import androidx.drawerlayout.widget.DrawerLayout; // O componente do DrawerLayout

import com.reviva.app.R;
import com.reviva.app.models.Memory;
import com.reviva.app.utils.FirebaseManager;
import com.reviva.app.utils.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.UUID;

// MemoriaActivity NÃO implementará mais NavigationView.OnNavigationItemSelectedListener
public class MemoriaActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1001;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 2001;
    private static final int PICK_AUDIO_REQUEST = 2002;
    private static final int PICK_VIDEO_REQUEST = 1003;
    private static final int PICK_DOCUMENT_REQUEST = 1234;

    // Elementos do Layout Principal
    private ImageButton btnMenu, btnTexto, btnFoto, btnVideo, btnAudio;
    private ImageButton btnRelacionamentos, btnFamilia, btnAmigos, btnViagens, btnMetas, btnMais;
    private Button btnSalvar, btnCancelar;
    private EditText edtTituloMemoria, edtDescricao, edtVisualizarEm;

    // Variáveis do Menu Lateral (Drawer)
    private DrawerLayout drawerLayout; // Adicionado: Referência ao DrawerLayout
    // Referências aos itens do menu lateral (os TextViews)
    private TextView menuItemCriarMemoria;
    private TextView menuItemMinhasMemorias;
    private TextView menuItemTopicos;
    private TextView menuItemConfiguracoes;
    private TextView menuItemSair;

    // Variáveis de Mídia e Categoria
    private Uri selectedImageUri;
    private Uri selectedAudioUri;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private Uri selectedVideoUri;
    private Uri selectedDocumentUri;
    private String categoriaSelecionada = null;
    private ImageButton botaoAtualSelecionado = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_memoria); // Seu layout principal

        // Permissões (mantido do seu código original)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO
                }, 101);
            }
        }

        // Inicialização dos elementos do Layout Principal
        btnMenu = findViewById(R.id.btnMenu); // Seu botão de hambúrguer
        btnTexto = findViewById(R.id.btnTexto);
        btnFoto = findViewById(R.id.btnFoto);
        btnVideo = findViewById(R.id.btnVideo);
        btnAudio = findViewById(R.id.btnAudio);
        btnSalvar = findViewById(R.id.btnSalvar);
        edtTituloMemoria = findViewById(R.id.edtTituloMemoria);
        btnCancelar = findViewById(R.id.btnCancelar);
        edtDescricao = findViewById(R.id.edtDescricao);
        edtVisualizarEm = findViewById(R.id.edtVisualizarEm);
        btnRelacionamentos = findViewById(R.id.btnRelacionamentos);
        btnFamilia = findViewById(R.id.btnFamilia);
        btnAmigos = findViewById(R.id.btnAmigos);
        btnViagens = findViewById(R.id.btnViagens);
        btnMetas = findViewById(R.id.btnMetas);
        btnMais = findViewById(R.id.btnMais);

        // --- INÍCIO: Configuração do Menu Lateral (Drawer) ---
        drawerLayout = findViewById(R.id.drawer_layout); // Inicializa o DrawerLayout

        // Inicializar os TextViews que são os itens do seu menu lateral
        menuItemCriarMemoria = findViewById(R.id.menu_item_criar_memoria);
        menuItemMinhasMemorias = findViewById(R.id.menu_item_minhas_memorias);
        menuItemTopicos = findViewById(R.id.menu_item_topicos);
        menuItemConfiguracoes = findViewById(R.id.menu_item_configuracoes);
        menuItemSair = findViewById(R.id.menu_item_sair);

        // Configurar o clique do btnMenu para abrir/fechar a gaveta
        btnMenu.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Configurar OnClickListeners para cada item do menu (TextViews)
        menuItemCriarMemoria.setOnClickListener(v -> {
            Toast.makeText(this, "Criar Memória Clicado", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
            // Se for para ir para a própria tela ou uma nova instância
            // Intent intent = new Intent(MemoriaActivity.this, MemoriaActivity.class);
            // startActivity(intent);
        });

        menuItemMinhasMemorias.setOnClickListener(v -> {
            Toast.makeText(this, "Minhas Memórias Clicado", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
            // Exemplo: Intent intent = new Intent(MemoriaActivity.this, MinhasMemoriasActivity.class);
            // startActivity(intent);
        });

        menuItemTopicos.setOnClickListener(v -> {
            Toast.makeText(this, "Tópicos Clicado", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
            // Exemplo: Intent intent = new Intent(MemoriaActivity.this, TopicosActivity.class);
            // startActivity(intent);
        });

        menuItemConfiguracoes.setOnClickListener(v -> {
            Toast.makeText(this, "Configurações Clicado", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
            // Exemplo: Intent intent = new Intent(MemoriaActivity.this, ConfiguracoesActivity.class);
            // startActivity(intent);
        });

        menuItemSair.setOnClickListener(v -> {
            Toast.makeText(this, "Sair Clicado", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
            // Exemplo: Fazer logout e ir para a tela de Login
            // FirebaseManager.getInstance().signOut();
            // Intent intent = new Intent(MemoriaActivity.this, LoginActivity.class);
            // startActivity(intent);
            // finish(); // Finaliza a activity atual
        });
        // --- FIM: Configuração do Menu Lateral (Drawer) ---


        // O restante dos seus OnClickListeners e métodos (mantidos do seu código original)

        btnTexto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_DOCUMENT_REQUEST);
            alternarSelecaoBotao(btnTexto, "texto");
        });

        btnFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
            alternarSelecaoBotao(btnFoto, "imagem");
        });

        btnVideo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");
            startActivityForResult(intent, PICK_VIDEO_REQUEST);
            alternarSelecaoBotao(btnVideo, "video");
        });

        btnAudio.setOnClickListener(v -> {
            showAudioOptions();
            alternarSelecaoBotao(btnAudio, "audio");
        });

        btnRelacionamentos.setOnClickListener(v -> {
            alternarSelecaoBotao(btnRelacionamentos, "relacionamentos");
        });

        btnFamilia.setOnClickListener(v -> {
            alternarSelecaoBotao(btnFamilia, "família");
        });
        btnAmigos.setOnClickListener(v -> {
            alternarSelecaoBotao(btnAmigos, "amigos");
        });
        btnViagens.setOnClickListener(v -> {
            alternarSelecaoBotao(btnViagens, "viagens");
        });
        btnMetas.setOnClickListener(v -> {
            alternarSelecaoBotao(btnMetas, "metas");
        });
        btnMais.setOnClickListener(v -> {
            alternarSelecaoBotao(btnMais, "mais");
        });

        btnSalvar.setOnClickListener(v -> salvarMemoria());

        btnCancelar.setOnClickListener(v -> {
            edtTituloMemoria.setText("");
            edtTituloMemoria.clearFocus();
            edtDescricao.setText("");
            edtDescricao.clearFocus();
            edtVisualizarEm.setText("");
            edtVisualizarEm.clearFocus();
            selectedImageUri = null;
            selectedAudioUri = null;
            selectedVideoUri = null;
            botaoAtualSelecionado = null;
            categoriaSelecionada = null;

            desmarcarTodosBotoes();
        });
    }

    // Sobrescrever onBackPressed para lidar com o menu lateral
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START); // Fecha o menu se estiver aberto
        } else {
            // Se o menu não estiver aberto, execute o comportamento padrão
            onBackPressed();
        }
    }

    // --- Seus métodos existentes (mantidos como estão) ---
    private void alternarSelecaoBotao(ImageButton botao, String categoria) {
        if (botao.equals(botaoAtualSelecionado)) {
            botao.setSelected(false);
            botaoAtualSelecionado = null;
            categoriaSelecionada = null;
        } else {
            desmarcarTodosBotoes();
            botao.setSelected(true);
            botaoAtualSelecionado = botao;
            categoriaSelecionada = categoria;
        }
    }

    private void desmarcarTodosBotoes() {
        btnRelacionamentos.setSelected(false);
        btnFamilia.setSelected(false);
        btnAmigos.setSelected(false);
        btnViagens.setSelected(false);
        btnMetas.setSelected(false);
        btnMais.setSelected(false);
        btnTexto.setSelected(false);
        btnFoto.setSelected(false);
        btnVideo.setSelected(false);
        btnAudio.setSelected(false);
    }

    private boolean isDataValida(String data) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setLenient(false);
            sdf.parse(data);
            return true;
        } catch (Exception e) {
            return false;
        }
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
            audioFilePath = getExternalFilesDir(null).getAbsolutePath() + "/audio_" + System.currentTimeMillis() + ".3gp"; // Adicionado "/"
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

        if (titulo.isEmpty() && edtVisualizarEm.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Preencha o título e o campo de visualização", Toast.LENGTH_SHORT).show();
            return;
        }else if (titulo.isEmpty()) {
            Toast.makeText(this, "Preencha o título da memória", Toast.LENGTH_SHORT).show();
            return;
        }else if (edtVisualizarEm.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Preencha o campo de visualização", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String data = edtVisualizarEm.getText().toString().trim();
            if (!isDataValida(data)) {
                Toast.makeText(this, "Digite uma data válida no formato dd/MM/yyyy", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Adicionada verificação se o usuário atual não é nulo antes de obter o Uid
        if (FirebaseManager.getInstance().getAuth().getCurrentUser() == null) {
            Toast.makeText(this, "Usuário não autenticado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            // Redirecionar para tela de login ou fazer algo apropriado
            return;
        }
        String userId = FirebaseManager.getInstance().getAuth().getCurrentUser().getUid();

        // Alterada a lógica para exibir Toast apenas se nenhum tipo de mídia for selecionado.
        // A navegação para ConfirmacaoActivity agora acontece APENAS se o upload for iniciado.
        if (selectedImageUri != null) {
            salvarImagem(titulo, userId);
        } else if (selectedAudioUri != null) {
            salvarAudio(titulo, userId);
        } else if (selectedVideoUri != null) {
            salvarVideo(titulo, userId);
        } else if (selectedDocumentUri != null) {
            salvarDocumento(titulo, userId);
        } else {
            Toast.makeText(this, "Selecione um tipo de memória!", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarDocumento(String titulo, String userId) {
        byte[] documentBytes = StorageUtils.readFileUriToByteArray(this, selectedDocumentUri);

        if (documentBytes == null) {
            Toast.makeText(this, "Erro ao processar documento", Toast.LENGTH_SHORT).show();
            return;
        }

        String extension = getFileExtensionFromUri(selectedDocumentUri);
        String fileName = "users/" + userId + "/memories/" + UUID.randomUUID() + (extension != null ? "." + extension : "");


        FirebaseManager.getInstance().uploadFile(documentBytes, fileName, new FirebaseManager.OnUploadCompleteListener() {
            @Override
            public void onSuccess(String downloadUrl) {
                salvarNoFirestore(titulo, userId, downloadUrl, "document");
                // Navegar para ConfirmacaoActivity SOMENTE APÓS O SUCESSO DO UPLOAD/SAVE
                startActivity(new Intent(MemoriaActivity.this, ConfirmacaoActivity.class));
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MemoriaActivity.this, "Erro no upload do documento", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(double progress) {}
        });
    }

    private String getFileExtensionFromUri(Uri uri) {
        String extension = null;
        String mimeType = getContentResolver().getType(uri);
        if (mimeType != null) {
            extension = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }
        return extension;
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
                // Navegar para ConfirmacaoActivity SOMENTE APÓS O SUCESSO DO UPLOAD/SAVE
                startActivity(new Intent(MemoriaActivity.this, ConfirmacaoActivity.class));
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
                // Navegar para ConfirmacaoActivity SOMENTE APÓS O SUCESSO DO UPLOAD/SAVE
                startActivity(new Intent(MemoriaActivity.this, ConfirmacaoActivity.class));
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
                // Navegar para ConfirmacaoActivity SOMENTE APÓS O SUCESSO DO UPLOAD/SAVE
                startActivity(new Intent(MemoriaActivity.this, ConfirmacaoActivity.class));
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
        memory.setDescription(""); // Descrição está vazia, se for usada, deve vir de edtDescricao
        memory.setMediaUrl(url);
        memory.setMediaType(tipo);
        memory.setCreatedAt(System.currentTimeMillis());
        memory.setUnlockAt(System.currentTimeMillis());
        memory.setUnlocked(true); // Memória é criada desbloqueada
        memory.setCategoria(categoriaSelecionada); // Categoria selecionada pelos botões

        FirebaseManager.getInstance().saveMemory(memory, new FirebaseManager.OnCompleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MemoriaActivity.this, "Memória salva com sucesso", Toast.LENGTH_SHORT).show();
                // A navegação para ConfirmacaoActivity já é feita em onSuccess do upload,
                // então remover daqui para evitar navegação duplicada se o Firestore salvar depois do upload.
                // Mas se você quer que navegue APENAS após salvar no Firestore, pode deixar aqui e remover do upload.
                // Recomendo deixar aqui, pois é a última etapa.
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MemoriaActivity.this, "Erro ao salvar memória", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null) return;

        Uri uri = data.getData();

        switch (requestCode) {
            case PICK_DOCUMENT_REQUEST:
                selectedDocumentUri = uri;
                Toast.makeText(this, "Documento selecionado", Toast.LENGTH_SHORT).show();
                break;
            case PICK_AUDIO_REQUEST:
                selectedAudioUri = uri;
                Toast.makeText(this, "Áudio selecionado", Toast.LENGTH_SHORT).show();
                break;
            case PICK_IMAGE_REQUEST:
                selectedImageUri = uri;
                Toast.makeText(this, "Imagem selecionada", Toast.LENGTH_SHORT).show();
                break;
            case PICK_VIDEO_REQUEST:
                selectedVideoUri = uri;
                Toast.makeText(this, "Vídeo selecionado", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startRecordingAudio();
        } else {
            Toast.makeText(this, "Permissão de gravação negada", Toast.LENGTH_SHORT).show();
        }
    }

}