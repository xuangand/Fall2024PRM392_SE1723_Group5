package fu.se.spotifi.Activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fu.se.spotifi.Adapters.SongAdapter;
import fu.se.spotifi.Const.Utils;
import fu.se.spotifi.DAO.SongDAO;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class QueueSong extends AppCompatActivity {
    ArrayList<Song> songArrayList;
    RecyclerView songs;
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_queue_song);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SpotifiDatabase db = SpotifiDatabase.getInstance(this);
        songs = findViewById(R.id.queueRecyclerView);

        songArrayList = new ArrayList<>();
        executorService.execute(() -> {
            List<Song> songs = db.songDAO().loadAllSongs(); // Replace with your database call
            //String songUrl = song.getUrl();
            songArrayList.addAll(songs);
        });

        SongAdapter songAdapter = new SongAdapter(this, songArrayList, this::playSong);
        songs.setAdapter(songAdapter);
        songs.setLayoutManager(new LinearLayoutManager(this));
    }
    private void playSong(Song song) {
        // If a song is already playing, stop and reset the player
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }

        // Create a new MediaPlayer instance and play the selected song
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(song.getUrl()); // Get the song URL from the Song object
            mediaPlayer.prepare(); // Prepare the player asynchronously
            mediaPlayer.start(); // Start the song
            Toast.makeText(this, "Playing: " + song.getTitle(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.getMessage();
            Toast.makeText(this, "Error playing song", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shut down the executor service
    }
}
