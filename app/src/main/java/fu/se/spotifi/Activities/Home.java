package fu.se.spotifi.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fu.se.spotifi.Adapters.SongAdapter;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Queue;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class Home extends BaseActivity {

    private BottomNavigationView bottomNavigationView;

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
        adapter = new SongAdapter(this, songList, this::onSongClick, this::onSongLongClick, true);
        songRecyclerView.setAdapter(adapter);

        // Load songs in a background thread
        executorService.execute(() -> {
            List<Song> songs = db.songDAO().loadAllSongs(); // Replace with your database call
            songList.addAll(songs);

            // Update the adapter on the main thread
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        });

        setupBottomNavigation();
    }

    // Correctly implementing the onItemClick method

    private void onSongClick(Song song) {
        // Add the clicked song to the queue
        addToQueue(song); // Assuming addQueue is the method to add to the queue
    }


    private void onSongLongClick(Song song) {
        addNewQueue(song);
    }

    private void addToQueue(Song song) {
        executorService.execute(() -> {

            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            List<Queue> currentQueue = db.queueDAO().loadAllQueues();
            int songOrder = currentQueue.size() + 1;
            Queue queue = new Queue();
            queue.setQueueId(currentQueue.size() + 1);
            queue.setSongOrder(songOrder);
            queue.setStatus("Paused");
            queue.setSongId(song.getId());
//        queue.setSongTitle(song.getTitle());
//        queue.setSongUrl(song.getUrl());
//        queue.setSongArtist(song.getArtist());
//        queue.setSongThumbnail(song.getThumbnail());

            // Use executor service to run the database operation in a background thread

            db.queueDAO().addQueue(queue); // Add the queue to the database

            // Log and show a Toast on the main thread
            runOnUiThread(() -> {
                Log.d("Home", "Added to queue: " + song.getTitle());
                Toast.makeText(this, song.getTitle() + " added to queue", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void addNewQueue(Song song) {
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            // Clear the queue
            db.queueDAO().clearQueue(); // Assuming you have a method in your DAO to clear the queue

            // Create a new Queue object
            Queue newQueueEntry = new Queue();
            newQueueEntry.setQueueId(1);
            newQueueEntry.setSongOrder(1);
            newQueueEntry.setStatus("Playing");
            newQueueEntry.setSongId(song.getId());

            // Add the song to the queue
            db.queueDAO().addQueue(newQueueEntry);

            // Notify on UI thread
            runOnUiThread(() -> {
                Toast.makeText(this, "New queue started with: " + song.getTitle(), Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shutdown the executor service when the activity is destroyed
    }

}

