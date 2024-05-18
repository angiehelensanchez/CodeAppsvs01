package com.example.codeappsvs01;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {

    private static FirebaseDatabaseHelper instance;
    private FirebaseFirestore db;
    private CollectionReference playerResultCollection;
    private CollectionReference locationDataCollection;

    private FirebaseDatabaseHelper() {
        db = FirebaseFirestore.getInstance();
        playerResultCollection = db.collection("PlayerResults");
        locationDataCollection = db.collection("LocationData");
    }

    public static synchronized FirebaseDatabaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseDatabaseHelper();
        }
        return instance;
    }

    // Métodos para PlayerResult
    public void addPlayerResult(PlayerResult playerResult) {
        playerResultCollection.add(playerResult);
    }

    public void getAllPlayerResults(OnResultsListener<PlayerResult> listener) {
        playerResultCollection.orderBy("result", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<PlayerResult> playerResults = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PlayerResult playerResult = document.toObject(PlayerResult.class);
                            playerResults.add(playerResult);
                        }
                        listener.onSuccess(playerResults);
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    // Métodos para LocationDatas
    public void addLocationData(LocationData locationData) {
        locationDataCollection.add(locationData);
    }

    public void getAllLocationData(OnResultsListener<LocationData> listener) {
        locationDataCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<LocationData> locationDataList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    LocationData locationData = document.toObject(LocationData.class);
                    locationDataList.add(locationData);
                }
                listener.onSuccess(locationDataList);
            } else {
                listener.onFailure(task.getException());
            }
        });
    }
    public void getHighestResult(OnResultListener<PlayerResult> listener) {
        playerResultCollection.orderBy("result", Query.Direction.DESCENDING).limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        PlayerResult highestResult = task.getResult().getDocuments().get(0).toObject(PlayerResult.class);
                        listener.onSuccess(highestResult);
                    } else {
                        listener.onFailure(task.getException());
                    }
                });
    }

    // Interfaz de callback para un solo resultado
    public interface OnResultListener<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

    // Interfaz de callback genérica
    public interface OnResultsListener<T> {
        void onSuccess(List<T> results);
        void onFailure(Exception e);
    }
}
