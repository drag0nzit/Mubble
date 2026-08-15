package com.example.mubble;

import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mubble.adapters.ArtistAdapter;
import com.example.mubble.adapters.ListenTrackAdapter;
import com.example.mubble.models.Track;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ListenManager {

    private final MainActivity activity;

    private ListenTrackAdapter trackAdapter;
    private ArtistAdapter artistAdapter;

    private final ArrayList<Track> trackList =
            new ArrayList<>();

    public ListenManager(
            MainActivity activity) {

        this.activity =
                activity;
    }

    public void setup() {

        setupTracks();
        setupArtists();
        setupMoodButtons();
        setupCollectionButtons();
    }

    private void setupTracks() {

        RecyclerView recyclerView =
                activity.findViewById(
                        R.id.rvNewTracks
                );

        if (recyclerView == null) {
            return;
        }

        trackAdapter =
                new ListenTrackAdapter(
                        trackList,
                        activity::playTrackFromListen
                );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        activity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        recyclerView.setAdapter(
                trackAdapter
        );
    }

    private void setupArtists() {

        RecyclerView recyclerView =
                activity.findViewById(
                        R.id.rvArtists
                );

        if (recyclerView == null) {
            return;
        }

        artistAdapter =
                new ArtistAdapter(
                        this::showArtistTracks
                );

        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        activity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        recyclerView.setAdapter(
                artistAdapter
        );
    }

    public void setTracks(
            List<Track> tracks) {

        trackList.clear();

        if (tracks != null) {
            trackList.addAll(
                    tracks
            );
        }

        if (trackAdapter != null) {
            trackAdapter.updateTracks(
                    trackList
            );
        }

        updateArtists();
    }

    private void updateArtists() {

        if (artistAdapter == null) {
            return;
        }

        Set<String> uniqueArtists =
                new LinkedHashSet<>();

        for (Track track :
                trackList) {

            if (track.getArtist() == null) {
                continue;
            }

            String artist =
                    track.getArtist()
                            .trim();

            if (!artist.isEmpty()) {
                uniqueArtists.add(
                        artist
                );
            }
        }

        artistAdapter.setArtists(
                new ArrayList<>(
                        uniqueArtists
                )
        );
    }

    private void showArtistTracks(
            String artist) {

        android.content.Intent intent =
                new android.content.Intent(
                        activity,
                        ArtistActivity.class
                );

        intent.putExtra(
                "artistName",
                artist
        );

        activity.startActivity(
                intent
        );
    }

    private void setupMoodButtons() {

        View calm =
                activity.findViewById(
                        R.id.btnMoodCalm
                );

        View energetic =
                activity.findViewById(
                        R.id.btnMoodEnergetic
                );

        View sad =
                activity.findViewById(
                        R.id.btnMoodSad
                );

        View background =
                activity.findViewById(
                        R.id.btnMoodBackground
                );

        if (calm != null) {
            calm.setOnClickListener(
                    v -> showMood(
                            "Спокойное"
                    )
            );
        }

        if (energetic != null) {
            energetic.setOnClickListener(
                    v -> showMood(
                            "Энергичное"
                    )
            );
        }

        if (sad != null) {
            sad.setOnClickListener(
                    v -> showMood(
                            "Грустное"
                    )
            );
        }

        if (background != null) {
            background.setOnClickListener(
                    v -> showMood(
                            "Для фона"
                    )
            );
        }
    }

    private void showMood(
            String mood) {

        if (trackList.isEmpty()) {

            Toast.makeText(
                    activity,
                    "Пока нет доступных треков",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        ArrayList<Track> result =
                new ArrayList<>(
                        trackList
                );

        if (mood.equals("Энергичное")) {

            Collections.reverse(
                    result
            );

        } else if (mood.equals("Грустное")) {

            if (result.size() > 1) {

                Track first =
                        result.remove(0);

                result.add(first);
            }

        } else if (mood.equals("Для фона")) {

            Collections.shuffle(
                    result
            );
        }

        trackAdapter.updateTracks(
                result
        );
    }

    private void setupCollectionButtons() {

        View popular =
                activity.findViewById(
                        R.id.cardPopular
                );

        View random =
                activity.findViewById(
                        R.id.cardRandom
                );

        if (popular != null) {
            popular.setOnClickListener(
                    v -> showPopular()
            );
        }

        if (random != null) {
            random.setOnClickListener(
                    v -> showRandom()
            );
        }
    }

    private void showPopular() {

        trackAdapter.updateTracks(
                new ArrayList<>(
                        trackList
                )
        );
    }

    private void showRandom() {

        ArrayList<Track> randomTracks =
                new ArrayList<>(
                        trackList
                );

        Collections.shuffle(
                randomTracks
        );

        trackAdapter.updateTracks(
                randomTracks
        );
    }
}