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

import java.util.ArrayList;
import java.util.List;

import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    Context context;
    ArrayList<Song> songs;

    public SongAdapter(@NonNull Context context, @NonNull ArrayList<Song> songs) {
        //super(context, 0, objects);
        this.context = context;
        this.songs = songs;
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
        holder.songTitle.setText(songs.get(position).getTitle());
        holder.songArtist.setText(songs.get(position).getArtist());
        holder.duration.setText(songs.get(position).getDuration());
        holder.songThumbnail.setImageResource(songs.get(position).getThumbnail());
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

//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        ViewHolder viewHolder;
//
//        // Check if convertView is null (no reusable view available)
//        if (convertView == null) {
//            // Inflate a new view
//            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
//
//            // Create a new ViewHolder and cache views
//            viewHolder = new ViewHolder();
//            viewHolder.songTitle = convertView.findViewById(R.id.songTitle);
//            viewHolder.songArtist = convertView.findViewById(R.id.songArtist);
//
//            // Associate the ViewHolder with the convertView
//            convertView.setTag(viewHolder);
//        } else {
//            // Reuse the existing view (convertView) and get the ViewHolder from the tag
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
//
//        // Get the current song
//        Song song = getItem(position);
//
//        // Populate the data into the views
//        if (song != null) {
//            viewHolder.songTitle.setText(song.getTitle());
//            viewHolder.songArtist.setText(song.getArtist());
//        }
//
//        return convertView;
//    }
}
