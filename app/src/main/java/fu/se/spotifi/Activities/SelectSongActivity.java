package fu.se.spotifi.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import fu.se.spotifi.Adapters.SongAdapter;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class SelectSongActivity extends BaseActivity {
    private RecyclerView songRecyclerView;
    private SongAdapter songAdapter;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_song);

        songRecyclerView = findViewById(R.id.songRecyclerView);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        executorService = Executors.newSingleThreadExecutor();
        loadSongs();
    }

    private void loadSongs() {
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            List<Song> songList = db.songDAO().loadAllSongs();
            runOnUiThread(() -> {
                songAdapter = new SongAdapter(this, (ArrayList<Song>) songList, song -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("selectedSong", song);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                },false);
                songRecyclerView.setAdapter(songAdapter);
            });
        });
    }
}
