// PlaylistDetails.java
package fu.se.spotifi.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fu.se.spotifi.Adapters.SongAdapter;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Playlist;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.Entities.SongList;
import fu.se.spotifi.R;

public class PlaylistDetails extends BaseActivity {
    private static final int REQUEST_SELECT_SONG = 1;
    private RecyclerView songRecyclerView;
    private SongAdapter songAdapter;
    private ExecutorService executorService;
    private Playlist playlist;

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

        if (playlist != null) {
            loadSongs(playlist.getId());
        }

        setupBottomNavigation();
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

    private void loadSongs(int playlistId) {
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            List<Song> songList = db.songListDAO().loadSongsByPlaylistId(playlistId);
            runOnUiThread(() -> {
                songAdapter = new SongAdapter(this, (ArrayList<Song>) songList, song -> {
                }, song -> {
                }, false);
                songRecyclerView.setAdapter(songAdapter);
            });
        });
    }

    private void addSongToPlaylist(Song song) {
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            db.songListDAO().addSongList(new SongList(playlist.getId(), song.getId()));
            loadSongs(playlist.getId());
        });
    }

    //Bi deprecated luon roi, ong sua lai di
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(this, Library.class);
//        startActivity(intent);
//        finish();
//    }
}
