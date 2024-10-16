package fu.se.spotifi.Activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
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
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    private ArrayList<Song> songList;

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

        SpotifiDatabase db = SpotifiDatabase.getInstance(this);
        RecyclerView songRecyclerView = findViewById(R.id.songRecyclerView);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        songList = new ArrayList<>();
        songRecyclerView.setLayoutManager(horizontalLayoutManager);
        // Initialize the adapter with an empty song list
        adapter = new SongAdapter(this, songList,null,true);
        songRecyclerView.setAdapter(adapter);

        // Load songs in a background thread
        executorService.execute(() -> {
            List<Song> songs = db.songDAO().loadAllSongs(); // Replace with your database call
            songList.addAll(songs);

            // Update the adapter on the main thread
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shutdown the executor service when the activity is destroyed
    }
}
