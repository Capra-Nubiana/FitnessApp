package com.fitness;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.api.core.ApiFuture;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RemoteSyncManager {

    private static final ExecutorService executor = Executors.newCachedThreadPool();

    // General logger for steps, calories, etc.
    public static void logToFirebase(String label, int value) {
        Firestore db = FirestoreClient.getFirestore();

        HashMap<String, Object> data = new HashMap<>();
        data.put("label", label);
        data.put("value", value);
        data.put("timestamp", System.currentTimeMillis());

        ApiFuture<WriteResult> future = db.collection("logs").document().set(data);

        executor.submit(() -> {
            try {
                future.get();
                System.out.println("☁ Synced log: " + label + "=" + value);
            } catch (Exception e) {
                System.err.println("❌ Firebase sync failed: " + e.getMessage());
            }
        });
    }

    // 🔄 Sync individual user step count
    public static void syncStep(String user, int steps) {
        Firestore db = FirestoreClient.getFirestore();

        HashMap<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("steps", steps);
        data.put("timestamp", System.currentTimeMillis());

        ApiFuture<WriteResult> future = db.collection("steps").document(user).set(data);

        executor.submit(() -> {
            try {
                future.get();
                System.out.println("☁ Synced steps for user: " + user);
            } catch (Exception e) {
                System.err.println("❌ Firebase step sync failed: " + e.getMessage());
            }
        });
    }
}
