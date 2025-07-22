package com.fitness;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.api.core.ApiFuture;

import java.util.HashMap;

public class UserProfileManager {
    private static String currentUser = "guest";
    private static final HashMap<String, Integer> userSteps = new HashMap<>();

    public static void login(String user) {
        currentUser = user;
        userSteps.putIfAbsent(user, 0);
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static int getSteps() {
        return userSteps.getOrDefault(currentUser, 0);
    }

    public static void addStep() {
        userSteps.put(currentUser, getSteps() + 1);
    }

    public static int getCalories() {
        return getSteps() / 20;
    }

    // 🔄 Sync full user profile to Firestore using Admin SDK
    public static void syncProfileToCloud() {
        Firestore db = FirestoreClient.getFirestore();

        HashMap<String, Object> data = new HashMap<>();
        data.put("steps", getSteps());
        data.put("calories", getCalories());
        data.put("lastSynced", System.currentTimeMillis());

        ApiFuture<WriteResult> future = db.collection("users").document(currentUser).set(data);

        try {
            WriteResult result = future.get(); // ✅ wait for sync
            System.out.println("✅ Synced user profile at: " + result.getUpdateTime());
        } catch (Exception e) {
            System.err.println("❌ Failed to sync profile: " + e.getMessage());
        }
    }
}
