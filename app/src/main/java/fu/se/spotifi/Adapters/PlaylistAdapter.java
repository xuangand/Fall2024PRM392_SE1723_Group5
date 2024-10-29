package fu.se.spotifi.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fu.se.spotifi.Activities.PlaylistDetails;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Playlist;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    Context context;
    ArrayList<Playlist> playlists;
    private OnPlaylistOptionClickListener optionClickListener;

    public PlaylistAdapter(@NonNull Context context, @NonNull ArrayList<Playlist> playlists, OnPlaylistOptionClickListener optionClickListener) {
        this.context = context;
        this.playlists = playlists;
        this.optionClickListener = optionClickListener;
    }

    @NonNull
    @Override
    public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_playlist, parent, false);
        return new PlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
        Playlist playlist = playlists.get(position);
        holder.playlistName.setText(playlist.getName());
        holder.playlistThumbnail.setImageResource(playlist.getThumbnail());
        holder.playlistSongCount.setText(playlist.getSongCount() + " songs");

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(context);
            List<Song> songLists = db.songListDAO().loadSongsByPlaylistId(playlist.getId());
            if (!songLists.isEmpty()) {
                Song firstSong = db.songDAO().getSongById(songLists.get(0).getId());
                if (firstSong != null) {
                    // Update the UI on the main thread
                    ((Activity) context).runOnUiThread(() -> Glide.with(holder.playlistThumbnail.getContext())
                            .load(firstSong.getThumbnail())
                            .into(holder.playlistThumbnail));
                }
            }
        });

        holder.optionsButton.setOnClickListener(v -> showPopupMenu(v, position));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlaylistDetails.class);
            intent.putExtra("playlist", playlist);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_playlist_options, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        int position;

        MyMenuItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.action_edit) {
                optionClickListener.onEditClick(playlists.get(position));
                return true;
            } else if (itemId == R.id.action_delete) {
                optionClickListener.onDeleteClick(playlists.get(position));
                return true;
            }
            return false;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView playlistName;
        ImageView playlistThumbnail;
        TextView playlistSongCount;
        ImageView optionsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.playlistTitle);
            playlistThumbnail = itemView.findViewById(R.id.playlistThumbnail);
            playlistSongCount = itemView.findViewById(R.id.playlistSongCount);
            optionsButton = itemView.findViewById(R.id.optionsButton);
        }
    }

    public interface OnPlaylistOptionClickListener {
        void onEditClick(Playlist playlist);
        void onDeleteClick(Playlist playlist);
    }

    public void setPlaylistCoverImage(ImageView imageView, Playlist playlist, List<Song> songs) {
        if (imageView == null) {
            return; // Add a null check to avoid NullPointerException
        }
        if (songs != null && !songs.isEmpty()) {
            Glide.with(imageView.getContext())
                    .load(songs.get(0).getThumbnail())
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.album_cover_image); // Set your default image resource
        }
    }
}
