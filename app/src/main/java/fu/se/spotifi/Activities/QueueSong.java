package fu.se.spotifi.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fu.se.spotifi.Adapters.SongAdapter;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class QueueSong extends AppCompatActivity {
    ArrayList<Song> songArrayList;
    RecyclerView songs;
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

        songs = findViewById(R.id.queueRecyclerView);
        songArrayList = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            songArrayList.add(new Song("Song " + i, "Artist " + i, "Path " + i));
        }
        SongAdapter songAdapter = new SongAdapter(this, songArrayList);
        songs.setAdapter(songAdapter);
        songs.setLayoutManager(new LinearLayoutManager(this));
    }
}
