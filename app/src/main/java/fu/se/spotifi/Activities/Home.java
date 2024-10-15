package fu.se.spotifi.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fu.se.spotifi.Adapters.SongAdapter;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class Home extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongAdapter adapter;
    private List<Song> songList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        if (appBarLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(appBarLayout, (v, insets) -> {
                return insets;
            });
        } else {
            Log.e("Home", "AppBarLayout is null");
        }

        recyclerView = findViewById(R.id.songRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter with an empty list
        adapter = new SongAdapter(this, new ArrayList<>(), song -> {
            // Handle song item click
        });

        // Attach the adapter to the RecyclerView
        recyclerView.setAdapter(adapter);

        // Load songs from database asynchronously
        loadSongsFromDatabase();
    }

    private void loadSongsFromDatabase() {
        SpotifiDatabase db = SpotifiDatabase.getInstance(this);
        new Thread(() -> {
            songList = db.songDAO().loadAllSongs();

            runOnUiThread(() -> {
                adapter = new SongAdapter(this, songList, song -> {
                    // Handle song item click
                });
                recyclerView.setAdapter(adapter);
            });
        }).start();
    }
}

