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

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    Context context;
    ArrayList<Song> songs;
    Utils utils = new Utils();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    public SongAdapter(@NonNull Context context, @NonNull ArrayList<Song> songs, OnItemClickListener listener) {
        //super(context, 0, objects);
        this.context = context;
        this.songs = songs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate layout, giving the look of the row
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_song,parent, false);
        return new SongAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Assign data to the view
        Song song = songs.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.songArtist.setText(song.getArtist());
        holder.duration.setText(utils.milisecondsToString(song.getDuration()));
        Glide.with(context).load(song.getThumbnail()).into(holder.songThumbnail);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(song));
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
}
