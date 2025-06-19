package com.reviva.app.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class StorageUtils {
    private StorageUtils() {

    }

    /**
     * Converte uma Uri de imagem em um array de bytes.
     * Útil para upload para o Firebase Storage.
     * @param context O contexto do aplicativo.
     * @param imageUri A Uri da imagem.
     * @return Array de bytes da imagem, ou null em caso de erro.
     */
    public static byte[] convertImageUriToByteArray(Context context, Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos); // Comprime a imagem
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lê um arquivo de mídia (áudio, vídeo) de uma Uri para um array de bytes.
     * @param context O contexto do aplicativo.
     * @param fileUri A Uri do arquivo.
     * @return Array de bytes do arquivo, ou null em caso de erro.
     */
    public static byte[] readFileUriToByteArray(Context context, Uri fileUri) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(fileUri);
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            return byteBuffer.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cria um nome de arquivo único usando UUID.
     * @param originalFileName O nome original do arquivo (para manter a extensão).
     * @return Um nome de arquivo único.
     */
    public static String generateUniqueFileName(String originalFileName) {
        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            extension = originalFileName.substring(i);
        }
        return UUID.randomUUID().toString() + extension;
    }

    /**
     * Cria um arquivo temporário no cache do aplicativo a partir de uma Uri.
     * Útil para processar arquivos de mídia antes de upload.
     * @param context O contexto do aplicativo.
     * @param uri A Uri do arquivo.
     * @param fileName O nome do arquivo temporário.
     * @return O objeto File temporário, ou null em caso de erro.
     */
    public static File getTempFileFromUri(Context context, Uri uri, String fileName) {
        File tempFile = new File(context.getCacheDir(), fileName);
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}


// Adicionar:
// Métodos para salvar arquivos de mídia no armazenamento interno/externo do dispositivo.

