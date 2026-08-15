package com.example.mubble.player;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;

@UnstableApi
public class MusicPlayerManager {

    private static MusicPlayerManager instance;

    private final ExoPlayer player;

    private String currentTrackUrl;

    private OnTrackEndedListener onTrackEndedListener;

    private boolean trackEndHandled = false;

    public interface OnTrackEndedListener {
        void onTrackEnded();
    }

    private MusicPlayerManager(Context context) {

        player =
                new ExoPlayer.Builder(
                        context.getApplicationContext()
                ).build();

        player.addListener(
                new Player.Listener() {

                    @Override
                    public void onPlaybackStateChanged(
                            int playbackState) {

                        Log.d(
                                "MusicPlayerManager",
                                "Playback state: "
                                        + playbackState
                        );

                        if (playbackState ==
                                Player.STATE_READY) {

                            trackEndHandled = false;
                        }

                        if (playbackState ==
                                Player.STATE_ENDED) {

                            if (!trackEndHandled &&
                                    onTrackEndedListener != null) {

                                trackEndHandled = true;

                                onTrackEndedListener
                                        .onTrackEnded();
                            }
                        }
                    }

                    @Override
                    public void onPlayerError(
                            PlaybackException error) {

                        Log.e(
                                "MusicPlayerManager",
                                "Ошибка воспроизведения: "
                                        + error.getMessage()
                                        + " | errorCode: "
                                        + error.errorCode,
                                error
                        );
                    }
                }
        );
    }

    public static MusicPlayerManager getInstance(
            Context context) {

        if (instance == null) {
            instance =
                    new MusicPlayerManager(
                            context
                    );
        }

        return instance;
    }

    public void setOnTrackEndedListener(
            OnTrackEndedListener listener) {

        this.onTrackEndedListener =
                listener;
    }

    public void play(String url) {

        if (url == null ||
                url.isEmpty()) {

            Log.e(
                    "MusicPlayerManager",
                    "URL пустой"
            );

            return;
        }

        Log.d(
                "MusicPlayerManager",
                "Попытка воспроизведения: "
                        + url
        );

        try {

            trackEndHandled = false;

            MediaItem mediaItem =
                    MediaItem.fromUri(
                            Uri.parse(url)
                    );

            player.setMediaItem(
                    mediaItem
            );

            player.prepare();

            currentTrackUrl =
                    url;

            player.play();

            Log.d(
                    "MusicPlayerManager",
                    "Команда play() отправлена"
            );

        } catch (Exception e) {

            Log.e(
                    "MusicPlayerManager",
                    "Ошибка запуска воспроизведения",
                    e
            );
        }
    }

    public void pause() {

        player.pause();

        Log.d(
                "MusicPlayerManager",
                "Пауза"
        );
    }

    public void resume() {

        player.play();

        Log.d(
                "MusicPlayerManager",
                "Продолжение воспроизведения"
        );
    }

    public void togglePlayPause() {

        if (player.isPlaying()) {
            pause();
        } else {
            resume();
        }
    }

    public void stop() {

        player.stop();

        currentTrackUrl =
                null;

        trackEndHandled = false;

        Log.d(
                "MusicPlayerManager",
                "Воспроизведение остановлено"
        );
    }

    public boolean isPlaying() {

        return player.isPlaying();
    }

    public long getCurrentPosition() {

        return player.getCurrentPosition();
    }

    public long getDuration() {

        return player.getDuration();
    }

    public void seekTo(long position) {

        player.seekTo(
                position
        );
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

        instance =
                null;
    }
}