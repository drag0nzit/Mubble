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

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private final List<Track> tracks;
    private final MusicPlayerManager playerManager;


    public TrackAdapter(List<Track> tracks, MusicPlayerManager playerManager) {
        this.tracks = tracks;
        this.playerManager = playerManager;
    }


    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_track, parent, false);

        return new TrackViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull TrackViewHolder holder,
            int position) {

        Track track = tracks.get(position);

        holder.title.setText(track.getTitle());
        holder.artist.setText(track.getArtist());


        holder.playButton.setOnClickListener(v -> {

            String url = track.getAudioUrl();
            Log.d("mubble_player", "НАЖАТА КНОПКА "+track.getAudioUrl());

            if (url != null && !url.isEmpty()) {
                playerManager.play(url);
            }

        });
    }


    @Override
    public int getItemCount() {
        return tracks.size();
    }


    static class TrackViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView artist;
        ImageButton playButton;


        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvTitle);
            artist = itemView.findViewById(R.id.tvArtist);
            playButton = itemView.findViewById(R.id.btnMore);
        }
    }
}