package com.example.mubble.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mubble.R;
import com.example.mubble.models.Playlist;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter
        extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    public interface OnPlaylistClickListener {
        void onPlaylistClick(Playlist playlist);
    }

    public interface OnPlaylistDeleteListener {
        void onPlaylistDelete(Playlist playlist);
    }

    private final List<Playlist> playlists = new ArrayList<>();
    private final OnPlaylistClickListener clickListener;
    private final OnPlaylistDeleteListener deleteListener;

    public PlaylistAdapter(
            OnPlaylistClickListener clickListener,
            OnPlaylistDeleteListener deleteListener) {

        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    public void setPlaylists(List<Playlist> newPlaylists) {

        playlists.clear();
        playlists.addAll(newPlaylists);

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_playlist, parent, false);

        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PlaylistViewHolder holder,
            int position) {

        Playlist playlist = playlists.get(position);

        holder.name.setText(playlist.getName());

        int trackCount = playlist.getTrackIds().size();

        holder.count.setText(
                trackCount + " " +
                        getTrackWord(trackCount)
        );

        holder.itemView.setOnClickListener(v ->
                clickListener.onPlaylistClick(playlist)
        );

        holder.deleteButton.setOnClickListener(v ->
                deleteListener.onPlaylistDelete(playlist)
        );
    }

    private String getTrackWord(int count) {

        if (count % 10 == 1 && count % 100 != 11) {
            return "трек";
        }

        if (count % 10 >= 2
                && count % 10 <= 4
                && (count % 100 < 10
                || count % 100 >= 20)) {

            return "трека";
        }

        return "треков";
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    static class PlaylistViewHolder
            extends RecyclerView.ViewHolder {

        TextView name;
        TextView count;
        ImageButton deleteButton;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvPlaylistName);
            count = itemView.findViewById(R.id.tvPlaylistCount);
            deleteButton =
                    itemView.findViewById(R.id.btnDeletePlaylist);
        }
    }
}