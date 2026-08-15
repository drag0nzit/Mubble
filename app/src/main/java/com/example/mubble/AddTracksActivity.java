package com.example.mubble;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mubble.adapters.AddTrackAdapter;
import com.example.mubble.database.FirestoreManager;
import com.example.mubble.models.Playlist;
import com.example.mubble.models.Track;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AddTracksActivity extends AppCompatActivity {

    private FirestoreManager firestoreManager;

    private RecyclerView rvTracks;

    private TextView tvPlaylistName;
    private TextView tvEmpty;

    private ImageButton btnBack;
    private ImageButton btnAddTracks;

    private View searchContainer;
    private TextInputEditText etSearch;

    private AddTrackAdapter adapter;

    private final ArrayList<Track> allTracks =
            new ArrayList<>();

    private final ArrayList<Track> displayedTracks =
            new ArrayList<>();

    private Playlist playlist;

    // false = просмотр плейлиста
    // true  = добавление треков
    private boolean addMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_tracks);

        firestoreManager =
                new FirestoreManager();

        rvTracks =
                findViewById(R.id.rvTracks);

        tvPlaylistName =
                findViewById(R.id.tvPlaylistName);

        tvEmpty =
                findViewById(R.id.tvEmpty);

        btnBack =
                findViewById(R.id.btnBack);

        btnAddTracks =
                findViewById(R.id.btnAddTracks);

        searchContainer =
                findViewById(R.id.searchContainer);

        etSearch =
                findViewById(R.id.etSearch);

        /*
         * Назад:
         *
         * если мы добавляем треки —
         * возвращаемся к плейлисту.
         *
         * если мы уже в плейлисте —
         * закрываем Activity.
         */
        btnBack.setOnClickListener(v -> {

            if (addMode) {

                addMode = false;

                etSearch.setText("");

                updateMode();

            } else {

                finish();
            }
        });

        /*
         * Кнопка +:
         *
         * включает режим добавления.
         */
        btnAddTracks.setOnClickListener(v -> {

            if (!addMode) {

                addMode = true;

                updateMode();

                etSearch.requestFocus();

            } else {

                addMode = false;

                etSearch.setText("");

                updateMode();
            }
        });

        String playlistId =
                getIntent().getStringExtra(
                        "playlistId"
                );

        if (playlistId == null ||
                playlistId.isEmpty()) {

            finish();
            return;
        }

        setupRecyclerView();

        setupSearch();

        loadPlaylist(playlistId);
    }

    private void setupRecyclerView() {

        adapter =
                new AddTrackAdapter(
                        displayedTracks,
                        addMode,
                        track -> {

                            if (playlist == null) {
                                return;
                            }

                            String trackId =
                                    track.getId();

                            if (addMode) {

                                /*
                                 * ДОБАВЛЕНИЕ
                                 */

                                if (!playlist
                                        .getTrackIds()
                                        .contains(trackId)) {

                                    firestoreManager
                                            .addTrackToPlaylist(
                                                    playlist.getId(),
                                                    trackId
                                            );

                                    playlist
                                            .getTrackIds()
                                            .add(trackId);
                                }

                            } else {

                                /*
                                 * УДАЛЕНИЕ
                                 */

                                if (playlist
                                        .getTrackIds()
                                        .contains(trackId)) {

                                    firestoreManager
                                            .removeTrackFromPlaylist(
                                                    playlist.getId(),
                                                    trackId
                                            );

                                    playlist
                                            .getTrackIds()
                                            .remove(trackId);
                                }
                            }

                            updateDisplayedTracks();
                        }
                );

        rvTracks.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvTracks.setAdapter(adapter);
    }

    private void loadPlaylist(
            String playlistId) {

        firestoreManager.getPlaylist(
                playlistId,
                loadedPlaylist -> {

                    if (loadedPlaylist == null) {

                        finish();

                        return;
                    }

                    playlist =
                            loadedPlaylist;

                    if (playlist.getTrackIds() == null) {

                        playlist.setTrackIds(
                                new ArrayList<>()
                        );
                    }

                    tvPlaylistName.setText(
                            playlist.getName()
                    );

                    loadTracks();
                }
        );
    }

    private void loadTracks() {

        firestoreManager.getTracks(
                tracks -> {

                    allTracks.clear();

                    allTracks.addAll(
                            tracks
                    );

                    updateDisplayedTracks();
                }
        );
    }

    private void updateDisplayedTracks() {

        displayedTracks.clear();

        if (playlist == null) {
            return;
        }

        String query =
                etSearch.getText()
                        .toString()
                        .trim()
                        .toLowerCase();

        for (Track track :
                allTracks) {

            boolean inPlaylist =
                    playlist
                            .getTrackIds()
                            .contains(
                                    track.getId()
                            );

            /*
             * Обычный режим:
             * показываем ТОЛЬКО треки плейлиста.
             */
            if (!addMode) {

                if (!inPlaylist) {
                    continue;
                }
            }

            /*
             * Режим добавления:
             * показываем ТОЛЬКО те треки,
             * которых ещё нет.
             */
            else {

                if (inPlaylist) {
                    continue;
                }
            }

            String title =
                    track.getTitle() == null
                            ? ""
                            : track.getTitle()
                            .toLowerCase();

            String artist =
                    track.getArtist() == null
                            ? ""
                            : track.getArtist()
                            .toLowerCase();

            if (query.isEmpty()
                    || title.contains(query)
                    || artist.contains(query)) {

                displayedTracks.add(
                        track
                );
            }
        }

        adapter.setAddMode(
                addMode
        );

        adapter.updateTracks(
                displayedTracks
        );

        updateEmptyState();
    }

    private void setupSearch() {

        etSearch.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        updateDisplayedTracks();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    private void updateMode() {

        if (addMode) {

            /*
             * Режим добавления
             */

            searchContainer.setVisibility(
                    View.VISIBLE
            );

            btnAddTracks.setImageResource(
                    R.drawable.ic_previous
            );

            btnAddTracks.setContentDescription(
                    "Вернуться к плейлисту"
            );

            tvPlaylistName.setText(
                    "Добавить треки"
            );

        } else {

            /*
             * Режим просмотра плейлиста
             */

            searchContainer.setVisibility(
                    View.GONE
            );

            btnAddTracks.setImageResource(
                    R.drawable.ic_add
            );

            btnAddTracks.setContentDescription(
                    "Добавить треки"
            );

            tvPlaylistName.setText(
                    playlist != null
                            ? playlist.getName()
                            : "Плейлист"
            );
        }

        updateDisplayedTracks();
    }

    private void updateEmptyState() {

        if (displayedTracks.isEmpty()) {

            rvTracks.setVisibility(
                    View.GONE
            );

            tvEmpty.setVisibility(
                    View.VISIBLE
            );

            if (addMode) {

                tvEmpty.setText(
                        "Все доступные треки уже в плейлисте"
                );

            } else {

                tvEmpty.setText(
                        "В плейлисте пока ничего нет"
                );
            }

        } else {

            rvTracks.setVisibility(
                    View.VISIBLE
            );

            tvEmpty.setVisibility(
                    View.GONE
            );
        }
    }
}