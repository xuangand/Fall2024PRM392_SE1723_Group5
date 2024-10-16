package fu.se.spotifi.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import fu.se.spotifi.Const.Utils;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> { // Use RecyclerView.ViewHolder as generic type
    Context context;
    ArrayList<Song> songs;
    Utils utils = new Utils();
    OnItemClickListener listener;

    private static final int HOME_VIEW_TYPE = 0;
    private static final int NORMAL_VIEW_TYPE = 1;

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    public SongAdapter(@NonNull Context context, @NonNull ArrayList<Song> songs, OnItemClickListener listener) {
        this.context = context;
        this.songs = songs;
        this.listener = listener;
    }

    public SongAdapter(@NonNull Context context, @NonNull ArrayList<Song> songs) {
        this.context = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;

        if (viewType == HOME_VIEW_TYPE) {
            view = inflater.inflate(R.layout.item_home_song, parent, false);
            return new HomeViewHolder(view); // Returning HomeViewHolder for home layout
        } else {
            view = inflater.inflate(R.layout.item_song, parent, false);
            return new ViewHolder(view); // Returning ViewHolder for normal layout
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Determine if the current item should be displayed with the home layout
        return isHomeLayout(position) ? HOME_VIEW_TYPE : NORMAL_VIEW_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Song song = songs.get(position);

        if (holder instanceof HomeViewHolder) {
            HomeViewHolder homeHolder = (HomeViewHolder) holder;
            homeHolder.songTitle.setText(song.getTitle());
            homeHolder.songArtist.setText(song.getArtist());
            Glide.with(context).load(song.getThumbnail()).into(homeHolder.songThumbnail);
        } else if (holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.songTitle.setText(song.getTitle());
            viewHolder.songArtist.setText(song.getArtist());
            viewHolder.duration.setText(utils.milisecondsToString(song.getDuration()));
            Glide.with(context).load(song.getThumbnail()).into(viewHolder.songThumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle, songArtist;
        ImageView songThumbnail;

        public HomeViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.songTitle); // Ensure this matches your item_home_song layout
            songArtist = itemView.findViewById(R.id.songArtist); // Ensure this matches your item_home_song layout
            songThumbnail = itemView.findViewById(R.id.songThumbnail); // Ensure this matches your item_home_song layout
        }
    }

    // Method to determine if the item at the given position should use the home layout
    private boolean isHomeLayout(int position) {
        // Implement your logic here; for example:
        return position == 0; // Assume the first item is a home layout
    }
}
