package com.example.mubble;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mubble.adapters.PlaylistAdapter;
import com.example.mubble.adapters.TrackAdapter;
import com.example.mubble.database.FirestoreManager;
import com.example.mubble.databinding.ActivityMainBinding;
import com.example.mubble.models.Playlist;
import com.example.mubble.models.Track;
import com.example.mubble.models.User;
import com.example.mubble.player.MusicPlayerManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

@OptIn(markerClass = UnstableApi.class)
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private RecyclerView rvTracks;
    private RecyclerView rvFavorites;
    private RecyclerView rvPlaylists;

    private TrackAdapter trackAdapter;
    private TrackAdapter favoriteAdapter;
    private PlaylistAdapter playlistAdapter;

    private ArrayList<Track> trackList;

    private FirestoreManager firestoreManager;
    private MusicPlayerManager playerManager;
    private ListenManager listenManager;

    private ImageButton btnPrev;
    private ImageButton btnPlay;
    private ImageButton btnNext;

    private TextView tvTrack;
    private TextView tvArtist;
    private TextView tvCurrentTime;
    private TextView tvDuration;

    private SeekBar seekBar;

    private View miniPlayer;

    private TextView miniPlayerTitle;
    private TextView miniPlayerArtist;
    private ImageButton miniPlayerPlayButton;

    private Track currentTrack;

    private int currentTrackIndex = -1;

    private boolean isUserSeeking = false;

    private final Handler handler =
            new Handler(Looper.getMainLooper());

    private final Runnable progressRunnable =
            new Runnable() {
                @Override
                public void run() {
                    updatePlayerProgress();

                    handler.postDelayed(
                            this,
                            500
                    );
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding =
                ActivityMainBinding.inflate(
                        getLayoutInflater()
                );

        setContentView(
                binding.getRoot()
        );

        auth =
                FirebaseAuth.getInstance();

        db =
                FirebaseFirestore.getInstance();

        firestoreManager =
                new FirestoreManager();

        playerManager =
                MusicPlayerManager.getInstance(
                        this
                );

        listenManager =
                new ListenManager(
                        this
                );

        trackList =
                new ArrayList<>();

        loadUserProfile();

        showHome();

        setupButtonMenu();

        rvTracks =
                findViewById(
                        R.id.rvTracks
                );

        tvTrack =
                findViewById(
                        R.id.tvTrack
                );

        tvArtist =
                findViewById(
                        R.id.tvArtist
                );

        btnPrev =
                findViewById(
                        R.id.btnPrev
                );

        btnPlay =
                findViewById(
                        R.id.btnPlay
                );

        btnNext =
                findViewById(
                        R.id.btnNext
                );

        seekBar =
                findViewById(
                        R.id.seekBar
                );

        tvCurrentTime =
                findViewById(
                        R.id.tvCurrentTime
                );

        tvDuration =
                findViewById(
                        R.id.tvDuration
                );

        miniPlayer =
                findViewById(
                        R.id.miniPlayerContainer
                );

        miniPlayerTitle =
                findViewById(
                        R.id.miniPlayerTitle
                );

        miniPlayerArtist =
                findViewById(
                        R.id.miniPlayerArtist
                );

        miniPlayerPlayButton =
                findViewById(
                        R.id.miniPlayerPlayButton
                );

        setupTrackAdapter();

        rvTracks.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvTracks.setAdapter(
                trackAdapter
        );

        setupMainPlayer();

        setupMiniPlayer();

        setupFavorites();

        setupPlaylists();

        listenManager.setup();

        playerManager.setOnTrackEndedListener(
                this::playNextTrack
        );

        loadTracks();

        setupSearch();

        handler.post(
                progressRunnable
        );

        binding.profileLayout.logoutButton
                .setOnClickListener(v -> {
                    auth.signOut();

                    Intent intent =
                            new Intent(
                                    MainActivity.this,
                                    LoginActivity.class
                            );

                    startActivity(intent);

                    finishAffinity();
                });
    }

    private void setupTrackAdapter() {
        trackAdapter =
                new TrackAdapter(
                        trackList,
                        playerManager,
                        (track, isFavorite) -> {
                            if (isFavorite) {
                                firestoreManager
                                        .addFavorite(
                                                track.getId()
                                        );
                            } else {
                                firestoreManager
                                        .removeFavorite(
                                                track.getId()
                                        );
                            }

                            loadFavorites();
                        },
                        track -> {
                            currentTrack =
                                    track;

                            currentTrackIndex =
                                    trackList.indexOf(
                                            track
                                    );

                            playerManager.play(
                                    track.getAudioUrl()
                            );

                            showMiniPlayer(
                                    track
                            );

                            firestoreManager
                                    .addToHistory(
                                            track.getId()
                                    );
                        }
                );
    }

    public void playTrackFromListen(
            Track track) {

        if (track == null) {
            return;
        }

        int index =
                trackList.indexOf(
                        track
                );

        if (index >= 0) {
            playTrackAtIndex(
                    index
            );
            return;
        }

        String url =
                track.getAudioUrl();

        if (url == null ||
                url.isEmpty()) {
            return;
        }

        currentTrack =
                track;

        currentTrackIndex = -1;

        playerManager.play(
                url
        );

        firestoreManager.addToHistory(
                track.getId()
        );

        showMiniPlayer(
                track
        );
    }

    private void setupFavorites() {
        rvFavorites =
                binding.profileLayout.rvFavorites;

        favoriteAdapter =
                new TrackAdapter(
                        new ArrayList<>(),
                        playerManager,
                        (track, isFavorite) -> {
                            if (isFavorite) {
                                firestoreManager
                                        .addFavorite(
                                                track.getId()
                                        );
                            } else {
                                firestoreManager
                                        .removeFavorite(
                                                track.getId()
                                        );
                            }

                            loadFavorites();
                        }
                );

        rvFavorites.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvFavorites.setAdapter(
                favoriteAdapter
        );

        binding.profileLayout.favoriteContainer
                .setOnClickListener(
                        v -> showFavorites()
                );

        binding.profileLayout.btnFavoritesBack
                .setOnClickListener(
                        v -> hideFavorites()
                );
    }

    private void setupPlaylists() {
        rvPlaylists =
                binding.profileLayout.rvPlaylists;

        playlistAdapter =
                new PlaylistAdapter(
                        this::openPlaylist,
                        this::deletePlaylist
                );

        rvPlaylists.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvPlaylists.setAdapter(
                playlistAdapter
        );

        binding.profileLayout.playlistsContainer
                .setOnClickListener(
                        v -> showPlaylists()
                );

        binding.profileLayout.btnPlaylistsBack
                .setOnClickListener(
                        v -> hidePlaylists()
                );

        binding.profileLayout.btnAddPlaylist
                .setOnClickListener(
                        v -> showCreatePlaylistDialog()
                );
    }

    private void showFavorites() {
        binding.profileLayout.profileContent
                .setVisibility(
                        View.GONE
                );

        binding.profileLayout.playlistsContent
                .setVisibility(
                        View.GONE
                );

        binding.profileLayout.favoritesContent
                .setVisibility(
                        View.VISIBLE
                );

        loadFavorites();
    }

    private void hideFavorites() {
        binding.profileLayout.favoritesContent
                .setVisibility(
                        View.GONE
                );

        binding.profileLayout.profileContent
                .setVisibility(
                        View.VISIBLE
                );
    }

    private void loadFavorites() {
        firestoreManager.getFavorites(
                favoriteIds -> {
                    List<Track> favorites =
                            new ArrayList<>();

                    for (Track track :
                            trackList) {

                        if (favoriteIds.contains(
                                track.getId()
                        )) {
                            favorites.add(track);
                        }
                    }

                    favoriteAdapter
                            .setFavorites(
                                    favoriteIds
                            );

                    favoriteAdapter
                            .updateTracks(
                                    favorites
                            );

                    if (favorites.isEmpty()) {
                        binding.profileLayout
                                .tvEmptyFavorites
                                .setVisibility(
                                        View.VISIBLE
                                );

                        rvFavorites
                                .setVisibility(
                                        View.GONE
                                );
                    } else {
                        binding.profileLayout
                                .tvEmptyFavorites
                                .setVisibility(
                                        View.GONE
                                );

                        rvFavorites
                                .setVisibility(
                                        View.VISIBLE
                                );
                    }
                }
        );
    }

    private void showPlaylists() {
        binding.profileLayout.profileContent
                .setVisibility(
                        View.GONE
                );

        binding.profileLayout.favoritesContent
                .setVisibility(
                        View.GONE
                );

        binding.profileLayout.playlistsContent
                .setVisibility(
                        View.VISIBLE
                );

        loadPlaylists();
    }

    private void hidePlaylists() {
        binding.profileLayout.playlistsContent
                .setVisibility(
                        View.GONE
                );

        binding.profileLayout.profileContent
                .setVisibility(
                        View.VISIBLE
                );
    }

    private void loadPlaylists() {
        firestoreManager.getPlaylists(
                playlists -> {
                    playlistAdapter
                            .setPlaylists(
                                    playlists
                            );

                    if (playlists.isEmpty()) {
                        binding.profileLayout
                                .tvEmptyPlaylists
                                .setVisibility(
                                        View.VISIBLE
                                );

                        rvPlaylists
                                .setVisibility(
                                        View.GONE
                                );
                    } else {
                        binding.profileLayout
                                .tvEmptyPlaylists
                                .setVisibility(
                                        View.GONE
                                );

                        rvPlaylists
                                .setVisibility(
                                        View.VISIBLE
                                );
                    }
                }
        );
    }

    private void showCreatePlaylistDialog() {
        EditText input =
                new EditText(this);

        input.setHint(
                "Название плейлиста"
        );

        input.setSingleLine(true);

        int padding =
                (int) (
                        24 *
                                getResources()
                                        .getDisplayMetrics()
                                        .density
                );

        input.setPadding(
                padding,
                0,
                padding,
                0
        );

        new AlertDialog.Builder(this)
                .setTitle(
                        "Новый плейлист"
                )
                .setView(input)
                .setNegativeButton(
                        "Отмена",
                        null
                )
                .setPositiveButton(
                        "Создать",
                        (dialog, which) -> {
                            String name =
                                    input.getText()
                                            .toString()
                                            .trim();

                            if (name.isEmpty()) {
                                return;
                            }

                            firestoreManager
                                    .createPlaylist(
                                            name,
                                            playlist ->
                                                    loadPlaylists()
                                    );
                        }
                )
                .show();
    }

    private void deletePlaylist(
            Playlist playlist) {

        new AlertDialog.Builder(this)
                .setTitle(
                        "Удалить плейлист?"
                )
                .setMessage(
                        playlist.getName()
                )
                .setNegativeButton(
                        "Отмена",
                        null
                )
                .setPositiveButton(
                        "Удалить",
                        (dialog, which) -> {
                            firestoreManager
                                    .deletePlaylist(
                                            playlist.getId()
                                    );

                            loadPlaylists();
                        }
                )
                .show();
    }

    private void openPlaylist(
            Playlist playlist) {

        Intent intent =
                new Intent(
                        MainActivity.this,
                        AddTracksActivity.class
                );

        intent.putExtra(
                "playlistId",
                playlist.getId()
        );

        intent.putExtra(
                "playlistName",
                playlist.getName()
        );

        startActivity(intent);
    }

    private void setupButtonMenu() {
        binding.bottomNav
                .setOnItemSelectedListener(
                        item -> {
                            int id =
                                    item.getItemId();

                            if (id ==
                                    R.id.nav_home) {

                                showHome();

                                return true;
                            }

                            if (id ==
                                    R.id.nav_listen) {

                                showListen();

                                return true;
                            }

                            if (id ==
                                    R.id.nav_profile) {

                                showProfile();

                                return true;
                            }

                            return false;
                        }
                );
    }

    private void showHome() {
        binding.frameHome
                .setVisibility(
                        View.VISIBLE
                );

        binding.frameProfile
                .setVisibility(
                        View.GONE
                );

        binding.frameListen
                .setVisibility(
                        View.GONE
                );
    }

    private void showProfile() {
        binding.frameHome
                .setVisibility(
                        View.GONE
                );

        binding.frameProfile
                .setVisibility(
                        View.VISIBLE
                );

        binding.frameListen
                .setVisibility(
                        View.GONE
                );

        hideFavorites();
        hidePlaylists();
    }

    private void showListen() {
        binding.frameHome
                .setVisibility(
                        View.GONE
                );

        binding.frameProfile
                .setVisibility(
                        View.GONE
                );

        binding.frameListen
                .setVisibility(
                        View.VISIBLE
                );
    }

    private void loadUserProfile() {
        FirebaseUser firebaseUser =
                auth.getCurrentUser();

        if (firebaseUser == null) {
            return;
        }

        db.collection("users")
                .document(
                        firebaseUser.getUid()
                )
                .get()
                .addOnSuccessListener(
                        documentSnapshot -> {
                            if (!documentSnapshot
                                    .exists()) {
                                return;
                            }

                            User user =
                                    documentSnapshot
                                            .toObject(
                                                    User.class
                                            );

                            if (user != null) {
                                binding.profileLayout
                                        .profileName
                                        .setText(
                                                user.getUsername()
                                        );

                                binding.profileLayout
                                        .profileEmail
                                        .setText(
                                                user.getEmail()
                                        );
                            }
                        }
                );
    }

    private void loadTracks() {
        firestoreManager.getTracks(
                tracks -> {
                    trackList.clear();

                    trackList.addAll(
                            tracks
                    );

                    trackAdapter
                            .updateTracks(
                                    trackList
                            );

                    listenManager.setTracks(
                            trackList
                    );

                    firestoreManager
                            .getFavorites(
                                    favoriteIds ->
                                            trackAdapter
                                                    .setFavorites(
                                                            favoriteIds
                                                    )
                            );
                }
        );
    }

    private void setupMainPlayer() {
        btnPlay.setOnClickListener(
                v -> {
                    if (currentTrack == null) {
                        return;
                    }

                    playerManager
                            .togglePlayPause();

                    updatePlayButtons();
                }
        );

        btnPrev.setOnClickListener(
                v -> playPreviousTrack()
        );

        btnNext.setOnClickListener(
                v -> playNextTrack()
        );

        seekBar.setMax(1000);

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(
                            SeekBar seekBar,
                            int progress,
                            boolean fromUser) {

                        if (!fromUser) {
                            return;
                        }

                        long duration =
                                playerManager
                                        .getDuration();

                        if (duration <= 0) {
                            return;
                        }

                        long position =
                                duration *
                                        progress /
                                        1000L;

                        tvCurrentTime.setText(
                                formatTime(
                                        position
                                )
                        );
                    }

                    @Override
                    public void onStartTrackingTouch(
                            SeekBar seekBar) {

                        isUserSeeking = true;
                    }

                    @Override
                    public void onStopTrackingTouch(
                            SeekBar seekBar) {

                        long duration =
                                playerManager
                                        .getDuration();

                        if (duration <= 0) {
                            isUserSeeking = false;
                            return;
                        }

                        long position =
                                duration *
                                        seekBar.getProgress() /
                                        1000L;

                        playerManager.seekTo(
                                position
                        );

                        isUserSeeking = false;
                    }
                }
        );
    }

    private void setupMiniPlayer() {
        miniPlayer.setVisibility(
                View.GONE
        );

        miniPlayerPlayButton
                .setOnClickListener(
                        v -> {
                            if (currentTrack ==
                                    null) {
                                return;
                            }

                            playerManager
                                    .togglePlayPause();

                            updatePlayButtons();
                        }
                );

        miniPlayer.setOnClickListener(
                v -> showHome()
        );
    }

    private void showMiniPlayer(
            Track track) {

        currentTrack =
                track;

        currentTrackIndex =
                trackList.indexOf(
                        track
                );

        miniPlayer.setVisibility(
                View.VISIBLE
        );

        miniPlayerTitle.setText(
                track.getTitle()
        );

        miniPlayerArtist.setText(
                track.getArtist()
        );

        tvTrack.setText(
                track.getTitle()
        );

        tvArtist.setText(
                track.getArtist()
        );

        seekBar.setProgress(
                0
        );

        tvCurrentTime.setText(
                "0:00"
        );

        tvDuration.setText(
                "0:00"
        );

        updatePlayButtons();
    }

    private void playPreviousTrack() {
        if (trackList.isEmpty()) {
            return;
        }

        if (currentTrackIndex <= 0) {
            currentTrackIndex = 0;
        } else {
            currentTrackIndex--;
        }

        playTrackAtIndex(
                currentTrackIndex
        );
    }

    private void playNextTrack() {
        if (trackList.isEmpty()) {
            return;
        }

        if (currentTrackIndex < 0) {
            currentTrackIndex = 0;
        } else if (
                currentTrackIndex <
                        trackList.size() - 1) {

            currentTrackIndex++;
        } else {
            currentTrackIndex = 0;
        }

        playTrackAtIndex(
                currentTrackIndex
        );
    }

    private void playTrackAtIndex(
            int index) {

        if (index < 0 ||
                index >= trackList.size()) {
            return;
        }

        Track track =
                trackList.get(index);

        String url =
                track.getAudioUrl();

        if (url == null ||
                url.isEmpty()) {
            return;
        }

        currentTrackIndex =
                index;

        currentTrack =
                track;

        playerManager.play(
                url
        );

        firestoreManager.addToHistory(
                track.getId()
        );

        showMiniPlayer(
                track
        );
    }

    private void updatePlayButtons() {
        if (playerManager.isPlaying()) {
            btnPlay.setImageResource(
                    R.drawable.ic_pause
            );

            miniPlayerPlayButton
                    .setImageResource(
                            R.drawable.ic_pause
                    );
        } else {
            btnPlay.setImageResource(
                    R.drawable.ic_play
            );

            miniPlayerPlayButton
                    .setImageResource(
                            R.drawable.ic_play
                    );
        }
    }

    private void updatePlayerProgress() {
        if (currentTrack == null) {
            return;
        }

        long duration =
                playerManager
                        .getDuration();

        long position =
                playerManager
                        .getCurrentPosition();

        if (duration <= 0) {
            return;
        }

        if (!isUserSeeking) {
            int progress =
                    (int) (
                            position *
                                    1000L /
                                    duration
                    );

            seekBar.setProgress(
                    progress
            );

            tvCurrentTime.setText(
                    formatTime(
                            position
                    )
            );
        }

        tvDuration.setText(
                formatTime(
                        duration
                )
        );

        updatePlayButtons();
    }

    private String formatTime(
            long milliseconds) {

        if (milliseconds < 0) {
            milliseconds = 0;
        }

        long totalSeconds =
                milliseconds / 1000;

        long minutes =
                totalSeconds / 60;

        long seconds =
                totalSeconds % 60;

        return String.format(
                java.util.Locale.getDefault(),
                "%d:%02d",
                minutes,
                seconds
        );
    }

    private void setupSearch() {
        TextInputEditText search =
                binding.frameHome
                        .findViewById(
                                R.id.etSearch
                        );

        search.addTextChangedListener(
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

                        applyFilters(
                                s.toString()
                        );
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                }
        );
    }

    private void applyFilters(
            String searchText) {

        String query =
                searchText
                        .trim()
                        .toLowerCase();

        List<Track> filteredTracks =
                new ArrayList<>();

        for (Track track :
                trackList) {

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

                filteredTracks.add(
                        track
                );
            }
        }

        trackAdapter.updateTracks(
                filteredTracks
        );
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(
                progressRunnable
        );

        playerManager
                .setOnTrackEndedListener(
                        null
                );

        super.onDestroy();
    }
}