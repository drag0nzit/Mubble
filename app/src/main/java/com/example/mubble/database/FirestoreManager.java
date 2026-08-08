package com.example.mubble.database;

import android.util.Log;

import com.example.mubble.models.Track;
import com.example.mubble.models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FirestoreManager {

    private final FirebaseFirestore db;


    public FirestoreManager() {
        db = FirebaseFirestore.getInstance();
    }


    // Сохранение пользователя после регистрации
    public void createUser(User user) {

        db.collection("users")
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(unused -> {

                    Log.d("Firestore",
                            "Пользователь сохранён");

                })
                .addOnFailureListener(e -> {

                    Log.e("Firestore",
                            "Ошибка сохранения пользователя",
                            e);

                });
    }


    // Интерфейс загрузки треков
    public interface OnTracksLoaded {
        void onLoaded(List<Track> tracks);
    }


    // Получение всех треков
    public void getTracks(OnTracksLoaded listener) {

        db.collection("tracks")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<Track> tracks = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

                        Track track = doc.toObject(Track.class);

                        if (track != null) {
                            tracks.add(track);
                        }
                    }

                    listener.onLoaded(tracks);

                })
                .addOnFailureListener(e -> {

                    Log.e("Firestore",
                            "Ошибка загрузки треков",
                            e);

                });
    }
}