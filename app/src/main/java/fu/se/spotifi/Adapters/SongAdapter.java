package fu.se.spotifi.Adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import fu.se.spotifi.Const.Utils;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    Context context;
    List<Song> songs;
    Utils utils = new Utils();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    public SongAdapter(@NonNull Context context, @NonNull List<Song> songs, OnItemClickListener listener) {
        //super(context, 0, objects);
        this.context = context;
        this.songs = songs;
        this.listener = listener;
    }

    public void updateSongList(List<Song> newSongs) {
        this.songs = newSongs;
        notifyDataSetChanged(); // Notify adapter about the data change
    }

//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        //Inflate layout, giving the look of the row
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.item_song,parent, false);
//        return new SongAdapter.ViewHolder(view);
//    }

//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        //Assign data to the view
//        Song song = songs.get(position);
//        holder.songTitle.setText(song.getTitle());
//        holder.songArtist.setText(song.getArtist());
//        holder.duration.setText(utils.milisecondsToString(song.getDuration()));
//        Glide.with(context).load(song.getThumbnail()).into(holder.songThumbnail);
//        holder.itemView.setOnClickListener(v -> listener.onItemClick(song));
//    }
@NonNull
@Override
public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
    return new SongViewHolder(view);
}

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.bind(song);
    }

    @Override
    public int getItemCount() {
        //Show how many item on the we want to display
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        //Grabbing the view from the recycler_view_row layout file
        TextView songTitle, songArtist, duration;
        ImageView songThumbnail;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.songTitle);
            songArtist = itemView.findViewById(R.id.songArtist);
            duration = itemView.findViewById(R.id.songDuration);
            songThumbnail = itemView.findViewById(R.id.songThumbnail);
        }
    }
    public class SongViewHolder extends RecyclerView.ViewHolder {
        private ImageView songCoverImage;
        private TextView songName, artistName;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songCoverImage = itemView.findViewById(R.id.songCoverImage);
            songName = itemView.findViewById(R.id.songName);
            artistName = itemView.findViewById(R.id.artistName);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(songs.get(position));
                }
            });
        }

        public void bind(Song song) {
            // Assuming Song entity has 'name', 'artist', and 'image' fields
            songName.setText(song.getTitle());
            artistName.setText(song.getArtist());
            // Load image (assuming it's a drawable resource)
            songCoverImage.setImageResource(Integer.parseInt(song.getThumbnail())); // or use Glide/Picasso for image loading
        }
    }
}
