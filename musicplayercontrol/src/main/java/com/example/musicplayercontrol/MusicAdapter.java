package com.example.musicplayercontrol;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
    private List<MusicInfo> musicList;

    public MusicAdapter(List<MusicInfo> musicList) {
        this.musicList = musicList;
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicViewHolder holder, int position) {
        MusicInfo musicInfo = musicList.get(position);

        holder.titleTextView.setText(musicInfo.getTitle());
        holder.artistTextView.setText(musicInfo.getArtist());
        holder.albumTextView.setText(musicInfo.getAlbum());
        holder.durationTextView.setText(musicInfo.getDuration());

        // 这里你可以使用 Glide 或其他库加载专辑封面
        // Glide.with(holder.itemView.getContext()).load(musicInfo.getAlbumArtUri()).into(holder.albumArtImageView);
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView artistTextView;
        TextView albumTextView;
        TextView durationTextView;
        ImageView albumArtImageView;

        public MusicViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title);
            artistTextView = itemView.findViewById(R.id.artist);
            albumTextView = itemView.findViewById(R.id.album);
            durationTextView = itemView.findViewById(R.id.duration);
            albumArtImageView = itemView.findViewById(R.id.album_art);
        }
    }
}
