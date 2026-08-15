package com.example.mubble;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mubble.adapters.TrackAdapter;
import com.example.mubble.database.FirestoreManager;
import com.example.mubble.models.Track;
import com.example.mubble.player.MusicPlayerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtistActivity extends AppCompatActivity {

    private RecyclerView rvArtistTracks;
    private TextView tvArtistTitle;
    private TextView tvArtistEmpty;
    private ImageButton btnArtistBack;
    private Button btnShuffleArtist;

    private TrackAdapter trackAdapter;

    private final ArrayList<Track> artistTracks =
            new ArrayList<>();

    private MusicPlayerManager playerManager;
    private FirestoreManager firestoreManager;

    private String artistName;

    @Override
    protected void onCreate(
            Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_artist
        );

        artistName =
                getIntent().getStringExtra(
                        "artistName"
                );

        if (artistName == null) {
            artistName = "Исполнитель";
        }

        playerManager =
                MusicPlayerManager.getInstance(
                        this
                );

        firestoreManager =
                new FirestoreManager();

        tvArtistTitle =
                findViewById(
                        R.id.tvArtistTitle
                );

        tvArtistEmpty =
                findViewById(
                        R.id.tvArtistEmpty
                );

        rvArtistTracks =
                findViewById(
                        R.id.rvArtistTracks
                );

        btnArtistBack =
                findViewById(
                        R.id.btnArtistBack
                );

        btnShuffleArtist =
                findViewById(
                        R.id.btnShuffleArtist
                );

        tvArtistTitle.setText(
                artistName
        );

        setupRecyclerView();

        btnArtistBack.setOnClickListener(
                v -> finish()
        );

        btnShuffleArtist.setOnClickListener(
                v -> shuffleTracks()
        );

        loadArtistTracks();
    }

    private void setupRecyclerView() {

        trackAdapter =
                new TrackAdapter(
                        artistTracks,
                        playerManager,
                        (track, isFavorite) -> {

                            if (isFavorite) {

                                firestoreManager.addFavorite(
                                        track.getId()
                                );

                            } else {

                                firestoreManager.removeFavorite(
                                        track.getId()
                                );
                            }
                        }
                );

        rvArtistTracks.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvArtistTracks.setAdapter(
                trackAdapter
        );
    }

    private void loadArtistTracks() {

        firestoreManager.getTracks(
                tracks -> {

                    artistTracks.clear();

                    if (tracks != null) {

                        for (Track track :
                                tracks) {

                            if (track.getArtist() == null) {
                                continue;
                            }

                            if (track.getArtist()
                                    .equalsIgnoreCase(
                                            artistName
                                    )) {

                                artistTracks.add(
                                        track
                                );
                            }
                        }
                    }

                    trackAdapter.updateTracks(
                            artistTracks
                    );

                    firestoreManager.getFavorites(
                            favoriteIds ->
                                    trackAdapter.setFavorites(
                                            favoriteIds
                                    )
                    );

                    updateEmptyState();
                }
        );
    }

    private void shuffleTracks() {

        if (artistTracks.isEmpty()) {
            return;
        }

        ArrayList<Track> shuffled =
                new ArrayList<>(
                        artistTracks
                );

        Collections.shuffle(
                shuffled
        );

        trackAdapter.updateTracks(
                shuffled
        );
    }

    private void updateEmptyState() {

        if (artistTracks.isEmpty()) {

            rvArtistTracks.setVisibility(
                    android.view.View.GONE
            );

            tvArtistEmpty.setVisibility(
                    android.view.View.VISIBLE
            );

        } else {

            rvArtistTracks.setVisibility(
                    android.view.View.VISIBLE
            );

            tvArtistEmpty.setVisibility(
                    android.view.View.GONE
            );
        }
    }
}