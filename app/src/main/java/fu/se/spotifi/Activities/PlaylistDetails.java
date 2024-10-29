package fu.se.spotifi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fu.se.spotifi.Adapters.SongAdapter;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Playlist;
import fu.se.spotifi.Entities.Queue;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.Entities.SongList;
import fu.se.spotifi.R;

public class PlaylistDetails extends BaseActivity {
    private static final int REQUEST_SELECT_SONG = 1;
    private RecyclerView songRecyclerView;
    private SongAdapter songAdapter;
    private ExecutorService executorService;
    private Playlist playlist;
    private List<Song> currentPlaylistSongs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_details);

        songRecyclerView = findViewById(R.id.songRecyclerView);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        executorService = Executors.newSingleThreadExecutor();
        playlist = (Playlist) getIntent().getSerializableExtra("playlist");

        ImageView back = findViewById(R.id.backButton);
        back.setOnClickListener(v -> onBackPressed());

        TextView playlistNameTextView = findViewById(R.id.playlistName);
        if (playlist != null) {
            playlistNameTextView.setText(playlist.getName());
        }

        // Set up play button click listener
        ImageButton playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(v -> {
            if (currentPlaylistSongs != null && !currentPlaylistSongs.isEmpty()) {
                startPlayback(currentPlaylistSongs, 0, false);
            }
        });

        // Set up shuffle button click listener
        ImageButton shuffleButton = findViewById(R.id.shuffleButton);
        shuffleButton.setOnClickListener(v -> {
            if (currentPlaylistSongs != null && !currentPlaylistSongs.isEmpty()) {
                ArrayList<Song> shuffledSongs = new ArrayList<>(currentPlaylistSongs);
                java.util.Collections.shuffle(shuffledSongs);
                startPlayback(shuffledSongs, 0, true);
            }
        });

        if (playlist != null) {
            loadSongs(playlist.getId());
        }

        setupBottomNavigation();
    }

    private void loadSongs(int playlistId) {
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            currentPlaylistSongs = db.songListDAO().loadSongsByPlaylistId(playlistId);
            runOnUiThread(() -> {
                songAdapter = new SongAdapter(
                        this,
                        (ArrayList<Song>) currentPlaylistSongs,
                        song -> startPlayback(currentPlaylistSongs, currentPlaylistSongs.indexOf(song), false),
                        song -> {},
                        false
                );
                songRecyclerView.setAdapter(songAdapter);

                // Set the playlist cover image to the first song's thumbnail
                if (currentPlaylistSongs != null && !currentPlaylistSongs.isEmpty()) {
                    ImageView playlistCoverImageView = findViewById(R.id.playlistCover);
                    Glide.with(this)
                            .load(currentPlaylistSongs.get(0).getThumbnail())
                            .into(playlistCoverImageView);
                }
            });
        });
    }

    private void startPlayback(List<Song> songs, int startIndex, boolean isShuffled) {
        if (songs == null || songs.isEmpty() || startIndex < 0 || startIndex >= songs.size()) {
            return;
        }

        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            db.queueDAO().clearQueue(); // Clear existing queue
            for (int i = 0; i < songs.size(); i++) {
                Queue queueEntry = new Queue();
                queueEntry.setSongId(songs.get(i).getId());
                queueEntry.setSongOrder(i);
                db.queueDAO().addQueue(queueEntry);
            }

            runOnUiThread(() -> {
                Intent intent = new Intent(this, PlayingMusic.class);
                intent.putExtra("playlistId", playlist.getId());
                intent.putExtra("songId", songs.get(startIndex).getId());
                intent.putExtra("isShuffled", isShuffled);
                startActivity(intent);
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_SONG && resultCode == RESULT_OK && data != null) {
            Song selectedSong = (Song) data.getSerializableExtra("selectedSong");
            if (selectedSong != null) {
                addSongToPlaylist(selectedSong);
            }
        }
    }

    private void addSongToPlaylist(Song song) {
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            db.songListDAO().addSongList(new SongList(playlist.getId(), song.getId()));
            loadSongs(playlist.getId());
        });
    }
}
