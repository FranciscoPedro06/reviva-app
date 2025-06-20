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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.reviva.app.R;
import com.reviva.app.models.Memory;
import com.reviva.app.utils.FirebaseManager;
import com.reviva.app.utils.StorageUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.UUID;

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
    private DrawerLayout drawerLayout;
    private TextView menuItemCriarMemoria; // Adicionado de volta, caso você queira um item "Criar Memória" no menu
    private TextView menuItemMinhasMemorias;
    // TextView menuItemTopicos; // Mantenha removido, conforme solicitado
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
    private ImageButton botaoMidiaSelecionado = null;
    private ImageButton botaoCategoriaSelecionado = null;
    private long unlockTimestamp = 0; // Inicializado no onCreate ou quando obtiver a data


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_memoria);

        // Permissões
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
        btnMenu = findViewById(R.id.btnMenu);
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
        drawerLayout = findViewById(R.id.drawer_layout);

        // Inicializar os TextViews que são os itens do seu menu lateral
        // Certifique-se de que 'menu_item_criar_memoria' exista no seu XML se for usá-lo.
        // Se não existir, comente ou remova esta linha.
        menuItemMinhasMemorias = findViewById(R.id.menu_item_minhas_memorias);
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

        // --- Configurar OnClickListeners para cada item do menu (TextViews) ---
        // Se menuItemCriarMemoria não for usado no XML, comente este bloco.
        if (menuItemCriarMemoria != null) { // Adicionado verificação para evitar NullPointerException
            menuItemCriarMemoria.setOnClickListener(v -> {
                Toast.makeText(this, "Criar Memória Clicado", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);
                // Se for para ir para a própria tela de MemoriaActivity,
                // você pode simplesmente fechar o drawer e não fazer mais nada.
                // Ou iniciar uma nova instância se for necessário reiniciar o estado da tela.
                // Exemplo: Intent intent = new Intent(MemoriaActivity.this, MemoriaActivity.class);
                // startActivity(intent);
            });
        }


        menuItemMinhasMemorias.setOnClickListener(v -> {
            Toast.makeText(this, "Minhas Memórias Clicado", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
            // Exemplo: Navegar para a tela de Minhas Memórias
            // Intent intent = new Intent(MemoriaActivity.this, MinhasMemoriasActivity.class);
            // startActivity(intent);
            // finish(); // Finaliza a MemoriaActivity para que MinhasMemoria seja a tela principal
        });

        menuItemConfiguracoes.setOnClickListener(v -> {
            Toast.makeText(this, "Configurações Clicado", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
            // Exemplo: Navegar para a tela de Configurações
            // Intent intent = new Intent(MemoriaActivity.this, ConfiguracoesActivity.class);
            // startActivity(intent);
            // finish(); // Finaliza a MemoriaActivity
        });

        menuItemSair.setOnClickListener(v -> {
            Toast.makeText(this, "Sair Clicado", Toast.LENGTH_SHORT).show();
            drawerLayout.closeDrawer(GravityCompat.START);
            // Inicia a nova Activity de confirmação de signout
            Intent intent = new Intent(MemoriaActivity.this, ConfirmarSignoutActivity.class);
            startActivity(intent);
            // NÃO chame finish() aqui. Deixe a ConfirmarSignoutActivity decidir finalizar esta Activity.
        });
        // --- FIM: Configuração do Menu Lateral (Drawer) ---

        // O restante dos seus OnClickListeners e métodos (mantidos do seu código original)

        btnTexto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_DOCUMENT_REQUEST);
            selecionarBotaoMidia(btnTexto); // Passando apenas o botão, o tipo é implícito ou pode ser passado via constante
        });

        btnFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
            selecionarBotaoMidia(btnFoto);
        });

        btnVideo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");
            startActivityForResult(intent, PICK_VIDEO_REQUEST);
            selecionarBotaoMidia(btnVideo);
        });

        btnAudio.setOnClickListener(v -> {
            showAudioOptions();
            selecionarBotaoMidia(btnAudio); // Chame aqui também para marcar o botão "Áudio"
        });

        // Categorias:
        btnRelacionamentos.setOnClickListener(v -> selecionarBotaoCategoria(btnRelacionamentos, "relacionamentos"));
        btnFamilia.setOnClickListener(v -> selecionarBotaoCategoria(btnFamilia, "família"));
        btnAmigos.setOnClickListener(v -> selecionarBotaoCategoria(btnAmigos, "amigos"));
        btnViagens.setOnClickListener(v -> selecionarBotaoCategoria(btnViagens, "viagens"));
        btnMetas.setOnClickListener(v -> selecionarBotaoCategoria(btnMetas, "metas"));
        btnMais.setOnClickListener(v -> selecionarBotaoCategoria(btnMais, "mais"));


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
            // Corrigido para usar as variáveis corretas de seleção de botão
            if (botaoMidiaSelecionado != null) {
                botaoMidiaSelecionado.setSelected(false);
                botaoMidiaSelecionado = null;
            }
            if (botaoCategoriaSelecionado != null) {
                botaoCategoriaSelecionado.setSelected(false);
                botaoCategoriaSelecionado = null;
            }
            categoriaSelecionada = null; // A categoria também é desmarcada

            // desmarcarTodosBotoes(); // Esta chamada se tornou redundante com o ajuste acima
        });
    }

    // Método onBackPressed para lidar com o menu lateral e bloquear o botão voltar
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START); // Fecha o menu se estiver aberto
        } else {
            // Se o menu não estiver aberto, não faz nada, BLOQUEANDO o botão de voltar.
            // Opcional: Adicione um Toast para avisar o usuário, ex:
            // Toast.makeText(this, "Esta ação está desabilitada.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Seus métodos existentes (mantidos e pequenos ajustes) ---

    // Nova versão de selecionarBotaoMidia - não precisa do 'tipoMidia' se o 'botaoMidiaSelecionado' for suficiente
    private void selecionarBotaoMidia(ImageButton botao) {
        if (botaoMidiaSelecionado != null && botaoMidiaSelecionado != botao) {
            botaoMidiaSelecionado.setSelected(false);
        }
        botao.setSelected(true);
        botaoMidiaSelecionado = botao;
    }

    private void selecionarBotaoCategoria(ImageButton botao, String categoria) {
        if (botaoCategoriaSelecionado != null && botaoCategoriaSelecionado != botao) {
            botaoCategoriaSelecionado.setSelected(false);
        }
        botao.setSelected(true);
        botaoCategoriaSelecionado = botao;
        categoriaSelecionada = categoria;
    }

    // Este método 'desmarcarTodosBotoes' pode ser removido, pois a lógica de desmarcação
    // é agora tratada em 'selecionarBotaoMidia' e 'selecionarBotaoCategoria' e 'btnCancelar'.
    // No entanto, se ele for chamado em outros lugares, mantenha-o.
    private void desmarcarTodosBotoes() {
        if (botaoMidiaSelecionado != null) {
            botaoMidiaSelecionado.setSelected(false);
            botaoMidiaSelecionado = null;
        }
        if (botaoCategoriaSelecionado != null) {
            botaoCategoriaSelecionado.setSelected(false);
            botaoCategoriaSelecionado = null;
        }
        // As linhas abaixo são redundantes se os ajustadores de seleção já desmarcam.
        // btnRelacionamentos.setSelected(false);
        // btnFamilia.setSelected(false);
        // btnAmigos.setSelected(false);
        // btnViagens.setSelected(false);
        // btnMetas.setSelected(false);
        // btnMais.setSelected(false);
        // btnTexto.setSelected(false);
        // btnFoto.setSelected(false);
        // btnVideo.setSelected(false);
        // btnAudio.setSelected(false);
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
        final String[] options = {"Gravar Áudio", "Selecionar Áudio"};
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
            audioFilePath = getExternalFilesDir(null).getAbsolutePath() + "/audio_" + System.currentTimeMillis() + ".3gp";
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
        final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    private void salvarMemoria() {
        String titulo = edtTituloMemoria.getText().toString().trim();
        String description = edtDescricao.getText().toString().trim(); // Obter descrição do EditText
        String visualizarEm = edtVisualizarEm.getText().toString().trim();

        // Validações dos campos de texto
        if (categoriaSelecionada == null) {
            Toast.makeText(this, "Selecione uma categoria", Toast.LENGTH_SHORT).show();
            return;
        }

        if (titulo.isEmpty() && visualizarEm.isEmpty()) {
            Toast.makeText(this, "Preencha o título e o campo de visualização", Toast.LENGTH_SHORT).show();
            return;
        } else if (titulo.isEmpty()) {
            Toast.makeText(this, "Preencha o título da memória", Toast.LENGTH_SHORT).show();
            return;
        } else if (visualizarEm.isEmpty()) {
            Toast.makeText(this, "Preencha o campo de visualização", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (!isDataValida(visualizarEm)) {
                Toast.makeText(this, "Digite uma data válida no formato dd/MM/yyyy", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            // A variável unlockTimestamp agora será definida aqui
            unlockTimestamp = sdf.parse(edtVisualizarEm.getText().toString().trim()).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao converter data", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificação de autenticação do usuário
        if (FirebaseManager.getInstance().getAuth().getCurrentUser() == null) {
            Toast.makeText(this, "Usuário não autenticado. Faça login novamente.", Toast.LENGTH_SHORT).show();
            return;
        }
        final String userId = FirebaseManager.getInstance().getAuth().getCurrentUser().getUid();

        // Lógica para salvar o tipo de mídia selecionado
        if (selectedImageUri != null) {
            salvarImagem(titulo, description, userId);
        } else if (selectedAudioUri != null) {
            salvarAudio(titulo, description, userId);
        } else if (selectedVideoUri != null) {
            salvarVideo(titulo, description, userId);
        } else if (selectedDocumentUri != null) {
            salvarDocumento(titulo, description, userId);
        } else {
            Toast.makeText(this, "Selecione um tipo de memória!", Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarDocumento(String titulo, String description, String userId) {
        final byte[] documentBytes = StorageUtils.readFileUriToByteArray(this, selectedDocumentUri);

        if (documentBytes == null) {
            Toast.makeText(this, "Erro ao processar documento", Toast.LENGTH_SHORT).show();
            return;
        }

        final String extension = getFileExtensionFromUri(selectedDocumentUri);
        final String fileName = "users/" + userId + "/memories/" + UUID.randomUUID() + (extension != null ? "." + extension : "");

        FirebaseManager.getInstance().uploadFile(documentBytes, fileName, new FirebaseManager.OnUploadCompleteListener() {
            @Override
            public void onSuccess(String downloadUrl) {
                salvarNoFirestore(titulo, description, userId, downloadUrl, "document", unlockTimestamp);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MemoriaActivity.this, "Erro no upload do documento", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(double progress) { /* NO_OP */ }
        });
    }

    private String getFileExtensionFromUri(Uri uri) {
        String extension = null;
        final String mimeType = getContentResolver().getType(uri);
        if (mimeType != null) {
            extension = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }
        return extension;
    }

    private void salvarImagem(String titulo, String description, String userId) {
        final byte[] imageBytes = StorageUtils.convertImageUriToByteArray(this, selectedImageUri);
        if (imageBytes == null) {
            Toast.makeText(this, "Erro ao processar imagem", Toast.LENGTH_SHORT).show();
            return;
        }

        final String fileName = "users/" + userId + "/memories/" + UUID.randomUUID() + ".jpg";
        FirebaseManager.getInstance().uploadFile(imageBytes, fileName, new FirebaseManager.OnUploadCompleteListener() {
            @Override
            public void onSuccess(String downloadUrl) {
                salvarNoFirestore(titulo, description, userId, downloadUrl, "image", unlockTimestamp);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MemoriaActivity.this, "Erro no upload da imagem", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(double progress) { /* NO_OP */ }
        });
    }

    private void salvarAudio(String titulo, String description, String userId) {
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

        final String fileName = "users/" + userId + "/memories/" + UUID.randomUUID() + ".3gp";
        FirebaseManager.getInstance().uploadFile(audioBytes, fileName, new FirebaseManager.OnUploadCompleteListener() {
            @Override
            public void onSuccess(String downloadUrl) {
                salvarNoFirestore(titulo, description, userId, downloadUrl, "audio", unlockTimestamp);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MemoriaActivity.this, "Erro no upload do áudio", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(double progress) { /* NO_OP */ }
        });
    }

    private void salvarVideo(String titulo, String description, String userId) {
        final byte[] videoBytes = StorageUtils.readFileUriToByteArray(this, selectedVideoUri);

        if (videoBytes == null) {
            Toast.makeText(this, "Erro ao processar vídeo", Toast.LENGTH_SHORT).show();
            return;
        }

        final String fileName = "users/" + userId + "/memories/" + UUID.randomUUID() + ".mp4";
        FirebaseManager.getInstance().uploadFile(videoBytes, fileName, new FirebaseManager.OnUploadCompleteListener() {
            @Override
            public void onSuccess(String downloadUrl) {
                salvarNoFirestore(titulo, description, userId, downloadUrl, "video", unlockTimestamp);
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MemoriaActivity.this, "Erro no upload do vídeo", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(double progress) { /* NO_OP */ }
        });
    }

    private void salvarNoFirestore(String titulo, String description, String userId, String url, String tipo, long unlockTimestamp) {
        final Memory memory = new Memory();
        memory.setMemoryId(UUID.randomUUID().toString());
        memory.setUserId(userId);
        memory.setTitle(titulo);
        memory.setDescription(description);
        memory.setMediaUrl(url);
        memory.setMediaType(tipo);
        memory.setCreatedAt(System.currentTimeMillis());
        memory.setUnlockAt(unlockTimestamp);
        memory.setUnlocked(true);
        memory.setCategoria(categoriaSelecionada);

        FirebaseManager.getInstance().saveMemory(memory, new FirebaseManager.OnCompleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MemoriaActivity.this, "Memória salva com sucesso", Toast.LENGTH_SHORT).show();
                // Navegação para ConfirmacaoActivity agora é feita após o upload com sucesso.
                // Se você quer que a navegação aconteça SOMENTE APÓS salvar no Firestore,
                // remova os 'startActivity' dos métodos de upload (salvarDocumento, salvarImagem, etc.)
                // e mantenha apenas este aqui:
                // startActivity(new Intent(MemoriaActivity.this, ConfirmacaoActivity.class));
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MemoriaActivity.this, "Erro ao salvar memória: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            if (botaoMidiaSelecionado != null) {
                botaoMidiaSelecionado.setSelected(false);
                botaoMidiaSelecionado = null;
            }
            return;
        }

        Uri uri = data.getData();

        switch (requestCode) {
            case PICK_DOCUMENT_REQUEST:
                selectedDocumentUri = uri;
                Toast.makeText(this, "Documento selecionado", Toast.LENGTH_SHORT).show();
                break;
            case PICK_AUDIO_REQUEST:
                selectedAudioUri = uri;
                Toast.makeText(this, "Áudio selecionado", Toast.LENGTH_SHORT).show();
                selecionarBotaoMidia(btnAudio); // Corrigido para selecionar o botão de áudio após o pick
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