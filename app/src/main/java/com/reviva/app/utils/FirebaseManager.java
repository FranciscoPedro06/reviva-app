package com.reviva.app.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.reviva.app.models.Memory;
import com.reviva.app.models.User;

import java.util.ArrayList;
import java.util.List;

public class FirebaseManager {

    private static FirebaseManager instance;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            instance = new FirebaseManager();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return mAuth;
    }

    // Nova interface personalizada para cadastro com UID
    public interface OnRegisterCompleteListener {
        void onSuccess(String uid);
        void onFailure(Exception e);
    }

    public void registerUser(String email, String password, OnRegisterCompleteListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = task.getResult().getUser().getUid();
                        listener.onSuccess(uid);
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

    public FirebaseFirestore getFirestore() {
        return db;
    }

    public void saveUserData(User user, OnCompleteListener listener) {
        db.collection("users")
                .document(user.getUid())
                .set(user)
                .addOnSuccessListener(unused -> listener.onSuccess())
                .addOnFailureListener(listener::onFailure);
    }

    public void saveMemory(Memory memory, OnCompleteListener listener) {
        db.collection("memories")
                .document(memory.getMemoryId())
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

    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface OnGetDataListener {
        void onSuccess(List<Object> dataList);
        void onFailure(Exception e);
    }

    public interface OnGetDocumentListener {
        void onSuccess(Object document);
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
