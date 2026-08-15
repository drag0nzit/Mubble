package com.example.mubble.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mubble.R;
import com.example.mubble.models.Track;

import java.util.ArrayList;
import java.util.List;

public class ListenTrackAdapter
        extends RecyclerView.Adapter<ListenTrackAdapter.ViewHolder> {

    public interface OnTrackClickListener {
        void onTrackClick(Track track);
    }

    private final ArrayList<Track> tracks;
    private final OnTrackClickListener listener;

    public ListenTrackAdapter(
            List<Track> tracks,
            OnTrackClickListener listener) {

        this.tracks =
                new ArrayList<>(
                        tracks
                );

        this.listener =
                listener;
    }

    public void updateTracks(
            List<Track> newTracks) {

        tracks.clear();

        if (newTracks != null) {
            tracks.addAll(
                    newTracks
            );
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(
                        parent.getContext()
                ).inflate(
                        R.layout.item_listen_track,
                        parent,
                        false
                );

        return new ViewHolder(
                view
        );
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Track track =
                tracks.get(position);

        holder.trackTitle.setText(
                track.getTitle()
        );

        holder.trackArtist.setText(
                track.getArtist()
        );

        holder.btnPlay.setOnClickListener(
                v -> {

                    if (listener != null) {
                        listener.onTrackClick(
                                track
                        );
                    }
                }
        );

        holder.itemView.setOnClickListener(
                v -> {

                    if (listener != null) {
                        listener.onTrackClick(
                                track
                        );
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView trackCover;
        ImageButton btnPlay;
        TextView trackTitle;
        TextView trackArtist;

        ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            trackCover =
                    itemView.findViewById(
                            R.id.trackCover
                    );

            btnPlay =
                    itemView.findViewById(
                            R.id.btnPlay
                    );

            trackTitle =
                    itemView.findViewById(
                            R.id.trackTitle
                    );

            trackArtist =
                    itemView.findViewById(
                            R.id.trackArtist
                    );
        }
    }
}