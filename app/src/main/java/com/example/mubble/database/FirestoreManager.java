package com.example.mubble.database;

import android.util.Log;

import com.example.mubble.models.Playlist;
import com.example.mubble.models.Track;
import com.example.mubble.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreManager {

    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    public FirestoreManager() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    // ============================================================
    // USER
    // ============================================================

    public void createUser(User user) {

        db.collection("users")
                .document(user.getId())
                .set(user)
                .addOnSuccessListener(unused ->
                        Log.d(
                                "Firestore",
                                "Пользователь сохранён"
                        )
                )
                .addOnFailureListener(e ->
                        Log.e(
                                "Firestore",
                                "Ошибка сохранения пользователя",
                                e
                        )
                );
    }

    // ============================================================
    // TRACKS
    // ============================================================

    public interface OnTracksLoaded {
        void onLoaded(List<Track> tracks);
    }

    public void getTracks(
            OnTracksLoaded listener) {

        db.collection("tracks")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<Track> tracks =
                            new ArrayList<>();

                    for (DocumentSnapshot doc :
                            querySnapshot.getDocuments()) {

                        Track track =
                                doc.toObject(
                                        Track.class
                                );

                        if (track != null) {

                            if (track.getId() == null ||
                                    track.getId().isEmpty()) {

                                track.setId(
                                        doc.getId()
                                );
                            }

                            tracks.add(track);
                        }
                    }

                    listener.onLoaded(
                            tracks
                    );
                })
                .addOnFailureListener(e ->
                        Log.e(
                                "Firestore",
                                "Ошибка загрузки треков",
                                e
                        )
                );
    }

    // ============================================================
    // FAVORITES
    // ============================================================

    public void addFavorite(
            String trackId) {

        if (auth.getCurrentUser() == null) {
            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(trackId)
                .set(new HashMap<>())
                .addOnSuccessListener(unused ->
                        Log.d(
                                "Firestore",
                                "Трек добавлен в избранное"
                        )
                )
                .addOnFailureListener(e ->
                        Log.e(
                                "Firestore",
                                "Ошибка добавления в избранное",
                                e
                        )
                );
    }

    public void removeFavorite(
            String trackId) {

        if (auth.getCurrentUser() == null) {
            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("favorites")
                .document(trackId)
                .delete()
                .addOnSuccessListener(unused ->
                        Log.d(
                                "Firestore",
                                "Трек удалён из избранного"
                        )
                )
                .addOnFailureListener(e ->
                        Log.e(
                                "Firestore",
                                "Ошибка удаления из избранного",
                                e
                        )
                );
    }

    public interface OnFavoritesLoaded {
        void onLoaded(List<String> favoriteIds);
    }

    public void getFavorites(
            OnFavoritesLoaded listener) {

        if (auth.getCurrentUser() == null) {

            listener.onLoaded(
                    new ArrayList<>()
            );

            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("favorites")
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<String> ids =
                            new ArrayList<>();

                    for (DocumentSnapshot document :
                            snapshot.getDocuments()) {

                        ids.add(
                                document.getId()
                        );
                    }

                    listener.onLoaded(
                            ids
                    );
                })
                .addOnFailureListener(e -> {

                    Log.e(
                            "Firestore",
                            "Ошибка загрузки избранного",
                            e
                    );

                    listener.onLoaded(
                            new ArrayList<>()
                    );
                });
    }

    // ============================================================
    // PLAYLISTS
    // ============================================================

    public interface OnPlaylistsLoaded {
        void onLoaded(List<Playlist> playlists);
    }

    public void getPlaylists(
            OnPlaylistsLoaded listener) {

        if (auth.getCurrentUser() == null) {

            listener.onLoaded(
                    new ArrayList<>()
            );

            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("playlists")
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<Playlist> playlists =
                            new ArrayList<>();

                    for (DocumentSnapshot document :
                            snapshot.getDocuments()) {

                        Playlist playlist =
                                document.toObject(
                                        Playlist.class
                                );

                        if (playlist != null) {

                            playlist.setId(
                                    document.getId()
                            );

                            playlists.add(
                                    playlist
                            );
                        }
                    }

                    listener.onLoaded(
                            playlists
                    );
                })
                .addOnFailureListener(e -> {

                    Log.e(
                            "Firestore",
                            "Ошибка загрузки плейлистов",
                            e
                    );

                    listener.onLoaded(
                            new ArrayList<>()
                    );
                });
    }

    public interface OnPlaylistCreated {
        void onCreated(Playlist playlist);
    }

    public void createPlaylist(
            String name,
            OnPlaylistCreated listener) {

        if (auth.getCurrentUser() == null) {
            listener.onCreated(null);
            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        String playlistId =
                db.collection("users")
                        .document(userId)
                        .collection("playlists")
                        .document()
                        .getId();

        Playlist playlist =
                new Playlist(
                        playlistId,
                        name,
                        new ArrayList<>()
                );

        db.collection("users")
                .document(userId)
                .collection("playlists")
                .document(playlistId)
                .set(playlist)
                .addOnSuccessListener(unused -> {

                    Log.d(
                            "Firestore",
                            "Плейлист создан"
                    );

                    listener.onCreated(
                            playlist
                    );
                })
                .addOnFailureListener(e -> {

                    Log.e(
                            "Firestore",
                            "Ошибка создания плейлиста",
                            e
                    );

                    listener.onCreated(
                            null
                    );
                });
    }

    public interface OnPlaylistLoaded {
        void onLoaded(Playlist playlist);
    }

    public void getPlaylist(
            String playlistId,
            OnPlaylistLoaded listener) {

        if (auth.getCurrentUser() == null) {

            listener.onLoaded(
                    null
            );

            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("playlists")
                .document(playlistId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {

                        listener.onLoaded(
                                null
                        );

                        return;
                    }

                    Playlist playlist =
                            document.toObject(
                                    Playlist.class
                            );

                    if (playlist != null) {

                        playlist.setId(
                                document.getId()
                        );
                    }

                    listener.onLoaded(
                            playlist
                    );
                })
                .addOnFailureListener(e -> {

                    Log.e(
                            "Firestore",
                            "Ошибка загрузки плейлиста",
                            e
                    );

                    listener.onLoaded(
                            null
                    );
                });
    }

    public void updatePlaylist(
            Playlist playlist) {

        if (auth.getCurrentUser() == null) {
            return;
        }

        if (playlist == null ||
                playlist.getId() == null ||
                playlist.getId().isEmpty()) {

            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("playlists")
                .document(playlist.getId())
                .set(playlist)
                .addOnSuccessListener(unused ->
                        Log.d(
                                "Firestore",
                                "Плейлист обновлён"
                        )
                )
                .addOnFailureListener(e ->
                        Log.e(
                                "Firestore",
                                "Ошибка обновления плейлиста",
                                e
                        )
                );
    }

    public void deletePlaylist(
            String playlistId) {

        if (auth.getCurrentUser() == null) {
            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("playlists")
                .document(playlistId)
                .delete()
                .addOnSuccessListener(unused ->
                        Log.d(
                                "Firestore",
                                "Плейлист удалён"
                        )
                )
                .addOnFailureListener(e ->
                        Log.e(
                                "Firestore",
                                "Ошибка удаления плейлиста",
                                e
                        )
                );
    }

    // ============================================================
    // PLAYLIST TRACKS
    // ============================================================

    public void addTrackToPlaylist(
            String playlistId,
            String trackId) {

        if (auth.getCurrentUser() == null) {
            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("playlists")
                .document(playlistId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        return;
                    }

                    Playlist playlist =
                            document.toObject(
                                    Playlist.class
                            );

                    if (playlist == null) {
                        return;
                    }

                    playlist.setId(
                            document.getId()
                    );

                    List<String> trackIds =
                            playlist.getTrackIds();

                    if (!trackIds.contains(trackId)) {

                        trackIds.add(trackId);

                        playlist.setTrackIds(
                                trackIds
                        );

                        updatePlaylist(
                                playlist
                        );
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(
                                "Firestore",
                                "Ошибка добавления трека в плейлист",
                                e
                        )
                );
    }

    public void removeTrackFromPlaylist(
            String playlistId,
            String trackId) {

        if (auth.getCurrentUser() == null) {
            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("playlists")
                .document(playlistId)
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        return;
                    }

                    Playlist playlist =
                            document.toObject(
                                    Playlist.class
                            );

                    if (playlist == null) {
                        return;
                    }

                    playlist.setId(
                            document.getId()
                    );

                    List<String> trackIds =
                            playlist.getTrackIds();

                    trackIds.remove(
                            trackId
                    );

                    playlist.setTrackIds(
                            trackIds
                    );

                    updatePlaylist(
                            playlist
                    );
                })
                .addOnFailureListener(e ->
                        Log.e(
                                "Firestore",
                                "Ошибка удаления трека из плейлиста",
                                e
                        )
                );
    }

    // ============================================================
    // HISTORY
    // ============================================================

    /**
     * Добавляет трек в историю прослушивания.
     *
     * Структура Firestore:
     *
     * users
     *   └── USER_ID
     *       └── history
     *           └── HISTORY_DOCUMENT
     *               ├── trackId
     *               └── timestamp
     *
     * Для каждого прослушивания создаётся отдельная запись.
     */
    public void addToHistory(
            String trackId) {

        if (auth.getCurrentUser() == null) {
            return;
        }

        if (trackId == null ||
                trackId.isEmpty()) {

            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        Map<String, Object> historyEntry =
                new HashMap<>();

        historyEntry.put(
                "trackId",
                trackId
        );

        historyEntry.put(
                "timestamp",
                FieldValue.serverTimestamp()
        );

        db.collection("users")
                .document(userId)
                .collection("history")
                .add(historyEntry)
                .addOnSuccessListener(documentReference ->
                        Log.d(
                                "Firestore",
                                "Трек добавлен в историю"
                        )
                )
                .addOnFailureListener(e ->
                        Log.e(
                                "Firestore",
                                "Ошибка добавления в историю",
                                e
                        )
                );
    }

    public interface OnHistoryLoaded {
        void onLoaded(List<String> trackIds);
    }

    /**
     * Загружает историю от новых записей к старым.
     */
    public void getHistory(
            OnHistoryLoaded listener) {

        if (auth.getCurrentUser() == null) {

            listener.onLoaded(
                    new ArrayList<>()
            );

            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("history")
                .orderBy(
                        "timestamp",
                        Query.Direction.DESCENDING
                )
                .limit(50)
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<String> trackIds =
                            new ArrayList<>();

                    for (DocumentSnapshot document :
                            snapshot.getDocuments()) {

                        String trackId =
                                document.getString(
                                        "trackId"
                                );

                        if (trackId != null &&
                                !trackId.isEmpty()) {

                            trackIds.add(
                                    trackId
                            );
                        }
                    }

                    listener.onLoaded(
                            trackIds
                    );
                })
                .addOnFailureListener(e -> {

                    Log.e(
                            "Firestore",
                            "Ошибка загрузки истории",
                            e
                    );

                    listener.onLoaded(
                            new ArrayList<>()
                    );
                });
    }

    /**
     * Полностью очищает историю пользователя.
     */
    public void clearHistory() {

        if (auth.getCurrentUser() == null) {
            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("history")
                .get()
                .addOnSuccessListener(snapshot -> {

                    for (DocumentSnapshot document :
                            snapshot.getDocuments()) {

                        document.getReference()
                                .delete();
                    }

                    Log.d(
                            "Firestore",
                            "История очищена"
                    );
                })
                .addOnFailureListener(e ->
                        Log.e(
                                "Firestore",
                                "Ошибка очистки истории",
                                e
                        )
                );
    }
}