package com.example.mubble.player;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

public class MusicPlayerManager {

    private static MusicPlayerManager instance;

    private final ExoPlayer player;

    private String currentTrackUrl;


    private MusicPlayerManager(Context context) {

        player = new ExoPlayer.Builder(context.getApplicationContext())
                .build();

    }


    public static MusicPlayerManager getInstance(Context context) {

        if (instance == null) {
            instance = new MusicPlayerManager(context);
        }

        return instance;
    }


    public void play(String url) {

        if (url == null || url.isEmpty()) {
            return;
        }

        if (!url.equals(currentTrackUrl)) {

            MediaItem mediaItem =
                    MediaItem.fromUri(Uri.parse(url));

            player.setMediaItem(mediaItem);

            player.prepare();

            currentTrackUrl = url;
        }

        player.play();
    }


    public void pause() {

        player.pause();
    }


    public void resume() {

        player.play();
    }


    public void togglePlayPause() {

        if (player.isPlaying()) {
            player.pause();
        } else {
            player.play();
        }
    }


    public void stop() {

        player.stop();

        currentTrackUrl = null;
    }


    public boolean isPlaying() {

        return player.isPlaying();
    }


    @Nullable
    public String getCurrentTrackUrl() {

        return currentTrackUrl;
    }


    public ExoPlayer getPlayer() {

        return player;
    }


    public void release() {

        player.release();

        instance = null;
    }
}