package com.reviva.app.utils;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.reviva.app.models.Memory;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {

    private static FirebaseManager instance;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    // Construtor privado para Singleton
    private FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

     // Método para obter a instância Singleton
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    // --- Métodos para Firebase Authentication ---

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    public void registerUser(String email, String password, OnCompleteListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess();
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    public void loginUser(String email, String password, OnCompleteListener listener) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onSuccess();
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
    }


    // --- Métodos para Firebase Firestore (Database) ---

    public FirebaseFirestore getFirestore() {
        return db;
    }

    public void saveMemory(Memory memory, OnCompleteListener listener) {
        db.collection("memories")
                .document(memory.getMemoryId()) // Supondo que Memory tenha getId()
                .set(memory)
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }


    public void getMemoriesForUser(String userId, OnGetDataListener listener) {
        db.collection("memories")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Memory> memoryList = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Memory memory = doc.toObject(Memory.class);
                        if (memory != null) {
                            memoryList.add(memory);
                        }
                    }
                    listener.onSuccess(new ArrayList<>(memoryList));
                })
                .addOnFailureListener(listener::onFailure);
    }



    public void getMemoryById(String memoryId, OnGetDocumentListener listener) {
        db.collection("memories")
                .document(memoryId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Memory memory = doc.toObject(Memory.class);
                        listener.onSuccess(memory);
                    } else {
                        listener.onFailure(new Exception("Memória não encontrada."));
                    }
                })
                .addOnFailureListener(listener::onFailure);
    }


    // --- Métodos para Firebase Storage (Armazenamento de mídia) ---

    public FirebaseStorage getStorage() {
        return storage;
    }

    public void uploadFile(byte[] fileBytes, String filePath, OnUploadCompleteListener listener) {
        storage.getReference().child(filePath)
                .putBytes(fileBytes)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl()
                            .addOnSuccessListener(uri -> listener.onSuccess(uri.toString()))
                            .addOnFailureListener(listener::onFailure);
                })
                .addOnFailureListener(listener::onFailure)
                .addOnProgressListener(taskSnapshot -> {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    listener.onProgress(progress);
                });
    }


    public void getFileDownloadUrl(String filePath, OnDownloadUrlCompleteListener listener) {
        storage.getReference().child(filePath)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> listener.onSuccess(uri.toString()))
                .addOnFailureListener(listener::onFailure);
    }


    public void deleteFile(String filePath, OnCompleteListener listener) {
        storage.getReference().child(filePath)
                .delete()
                .addOnSuccessListener(aVoid -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }


    // --- Interfaces de Callback (exemplo, você pode criar suas próprias) ---

    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnGetDataListener {
        void onSuccess(java.util.List<Object> dataList); // Adapte para o tipo de dado correto
        void onFailure(Exception e);
    }

    public interface OnGetDocumentListener {
        void onSuccess(Object document); // Adapte para o tipo de dado correto (ex: Memory)
        void onFailure(Exception e);
    }

    public interface OnUploadCompleteListener {
        void onSuccess(String downloadUrl);
        void onFailure(Exception e);
        void onProgress(double progress);
    }

    public interface OnDownloadUrlCompleteListener {
        void onSuccess(String downloadUrl);
        void onFailure(Exception e);
    }
}