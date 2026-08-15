package com.example.mubble.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mubble.R;
import com.example.mubble.models.Track;
import com.example.mubble.player.MusicPlayerManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TrackAdapter
        extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    public interface OnFavoriteClickListener {
        void onFavoriteClick(
                Track track,
                boolean isFavorite
        );
    }

    public interface OnTrackClickListener {
        void onTrackClick(Track track);
    }

    private final List<Track> tracks;

    private final MusicPlayerManager playerManager;

    private final Set<String> favoriteIds =
            new HashSet<>();

    private final OnFavoriteClickListener
            favoriteClickListener;

    private final OnTrackClickListener
            trackClickListener;

    public TrackAdapter(
            List<Track> tracks,
            MusicPlayerManager playerManager,
            OnFavoriteClickListener favoriteClickListener) {

        this(
                tracks,
                playerManager,
                favoriteClickListener,
                null
        );
    }

    public TrackAdapter(
            List<Track> tracks,
            MusicPlayerManager playerManager,
            OnFavoriteClickListener favoriteClickListener,
            OnTrackClickListener trackClickListener) {

        this.tracks =
                new ArrayList<>(tracks);

        this.playerManager =
                playerManager;

        this.favoriteClickListener =
                favoriteClickListener;

        this.trackClickListener =
                trackClickListener;
    }

    public void updateTracks(
            List<Track> newTracks) {

        tracks.clear();

        if (newTracks != null) {
            tracks.addAll(newTracks);
        }

        notifyDataSetChanged();
    }

    public void setFavorites(
            List<String> ids) {

        favoriteIds.clear();

        if (ids != null) {
            favoriteIds.addAll(ids);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.item_track,
                        parent,
                        false
                );

        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TrackViewHolder holder,
            int position) {

        Track track =
                tracks.get(position);

        holder.title.setText(
                track.getTitle()
        );

        holder.artist.setText(
                track.getArtist()
        );

        int duration =
                track.getDuration();

        holder.duration.setText(
                String.format(
                        java.util.Locale
                                .getDefault(),
                        "%d:%02d",
                        duration / 60,
                        duration % 60
                )
        );

        boolean isFavorite =
                favoriteIds.contains(
                        track.getId()
                );

        holder.favoriteButton.setImageResource(
                isFavorite
                        ? R.drawable.ic_favorite
                        : R.drawable.ic_favorite_border
        );

        holder.playButton.setOnClickListener(
                v -> {

                    Log.d(
                            "TrackAdapter",
                            "Нажат трек: "
                                    + track.getTitle()
                    );

                    Log.d(
                            "TrackAdapter",
                            "ID: "
                                    + track.getId()
                    );

                    Log.d(
                            "TrackAdapter",
                            "Audio URL: "
                                    + track.getAudioUrl()
                    );

                    String url =
                            track.getAudioUrl();

                    if (url == null ||
                            url.isEmpty()) {

                        Log.e(
                                "TrackAdapter",
                                "Audio URL пустой!"
                        );

                        return;
                    }

                    /*
                     * Если передан callback,
                     * отдаём управление MainActivity.
                     *
                     * Именно MainActivity теперь
                     * будет запускать трек,
                     * показывать мини-плеер
                     * и обновлять основной плеер.
                     */
                    if (trackClickListener != null) {

                        Log.d(
                                "TrackAdapter",
                                "Передаём трек в callback"
                        );

                        trackClickListener
                                .onTrackClick(track);

                    } else {

                        /*
                         * Старое поведение оставляем
                         * для адаптеров, которым callback
                         * не нужен.
                         */
                        Log.d(
                                "TrackAdapter",
                                "Callback отсутствует, запускаем напрямую"
                        );

                        playerManager.play(
                                url
                        );
                    }
                }
        );

        holder.favoriteButton
                .setOnClickListener(
                        v -> {

                            boolean newState =
                                    !favoriteIds.contains(
                                            track.getId()
                                    );

                            if (newState) {

                                favoriteIds.add(
                                        track.getId()
                                );

                            } else {

                                favoriteIds.remove(
                                        track.getId()
                                );
                            }

                            holder.favoriteButton
                                    .setImageResource(
                                            newState
                                                    ? R.drawable.ic_favorite
                                                    : R.drawable.ic_favorite_border
                                    );

                            if (favoriteClickListener != null) {

                                favoriteClickListener
                                        .onFavoriteClick(
                                                track,
                                                newState
                                        );
                            }
                        }
                );
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    static class TrackViewHolder
            extends RecyclerView.ViewHolder {

        TextView title;
        TextView artist;
        TextView duration;

        ImageButton playButton;
        ImageButton favoriteButton;

        TrackViewHolder(
                @NonNull View itemView) {

            super(itemView);

            title =
                    itemView.findViewById(
                            R.id.tvTitle
                    );

            artist =
                    itemView.findViewById(
                            R.id.tvArtist
                    );

            duration =
                    itemView.findViewById(
                            R.id.tvDuration
                    );

            playButton =
                    itemView.findViewById(
                            R.id.btnPlay
                    );

            favoriteButton =
                    itemView.findViewById(
                            R.id.btnFavorite
                    );
        }
    }
}