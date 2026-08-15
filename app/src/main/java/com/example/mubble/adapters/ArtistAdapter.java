package com.example.mubble.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mubble.R;

import java.util.ArrayList;
import java.util.List;

public class ArtistAdapter
        extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    public interface OnArtistClickListener {
        void onArtistClick(String artist);
    }

    private final ArrayList<String> artists =
            new ArrayList<>();

    private final OnArtistClickListener listener;

    public ArtistAdapter(
            OnArtistClickListener listener) {

        this.listener = listener;
    }

    public void setArtists(
            List<String> newArtists) {

        artists.clear();

        if (newArtists != null) {
            artists.addAll(
                    newArtists
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
                        R.layout.item_artist,
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

        String artist =
                artists.get(position);

        holder.artistName.setText(
                artist
        );

        holder.itemView.setOnClickListener(
                v -> {

                    if (listener != null) {

                        listener.onArtistClick(
                                artist
                        );
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

    static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView artistImage;
        TextView artistName;

        ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            artistImage =
                    itemView.findViewById(
                            R.id.artistImage
                    );

            artistName =
                    itemView.findViewById(
                            R.id.artistName
                    );
        }
    }
}