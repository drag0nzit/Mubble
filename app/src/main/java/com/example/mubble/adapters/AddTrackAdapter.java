package com.example.mubble.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mubble.R;
import com.example.mubble.models.Track;

import java.util.ArrayList;
import java.util.List;

public class AddTrackAdapter
        extends RecyclerView.Adapter<AddTrackAdapter.TrackViewHolder> {

    public interface OnAddTrackClickListener {
        void onTrackClick(Track track);
    }

    private final List<Track> tracks;

    private final OnAddTrackClickListener listener;

    private boolean addMode;

    public AddTrackAdapter(
            List<Track> tracks,
            boolean addMode,
            OnAddTrackClickListener listener) {

        this.tracks =
                new ArrayList<>(tracks);

        this.addMode =
                addMode;

        this.listener =
                listener;
    }

    public void setAddMode(
            boolean addMode) {

        this.addMode =
                addMode;

        notifyDataSetChanged();
    }

    public void updateTracks(
            List<Track> newTracks) {

        tracks.clear();

        tracks.addAll(
                newTracks
        );

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
                        R.layout.item_add_track,
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
                        java.util.Locale.getDefault(),
                        "%d:%02d",
                        duration / 60,
                        duration % 60
                )
        );

        /*
         * В режиме добавления:
         * +
         *
         * В обычном режиме:
         * удалить
         */
        if (addMode) {

            holder.actionButton.setImageResource(
                    R.drawable.ic_add
            );

            holder.actionButton.setContentDescription(
                    "Добавить трек"
            );

        } else {

            holder.actionButton.setImageResource(
                    R.drawable.ic_delete
            );

            holder.actionButton.setContentDescription(
                    "Удалить трек"
            );
        }

        holder.actionButton.setOnClickListener(
                v -> listener.onTrackClick(track)
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

        ImageButton actionButton;

        public TrackViewHolder(
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

            actionButton =
                    itemView.findViewById(
                            R.id.btnAdd
                    );
        }
    }
}