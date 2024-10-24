package fu.se.spotifi.Activities;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fu.se.spotifi.Adapters.SongAdapter;
import fu.se.spotifi.DAO.SongDAO;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.SearchHistory;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class SearchActivity extends BaseActivity implements SongAdapter.OnItemClickListener, SongAdapter.OnItemLongClickListener {
    private SearchView searchView;
    private RecyclerView recyclerViewResults;
    private ArrayList<Song> searchResults;
    private SongAdapter songAdapter;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ExecutorService executorService;
    private SpotifiDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initializeComponents();
        setupSearchView();
        setupRecyclerView();
    }

    private void initializeComponents() {
        // Initialize Firebase
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("songs");

        // Initialize Database
        database = SpotifiDatabase.getInstance(this);

        // Initialize ExecutorService
        executorService = Executors.newSingleThreadExecutor();

        // Initialize Views
        searchView = findViewById(R.id.searchView);
        recyclerViewResults = findViewById(R.id.recyclerViewResults);

        // Initialize List and Adapter
        searchResults = new ArrayList<>();
        songAdapter = new SongAdapter(this, searchResults, this, this, false);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty()) {
                    performSearch(newText);
                } else {
                    clearResults();
                }
                return true;
            }
        });
    }
    @Override
    public void onItemClick(Song song) {
        // Save song to database when clicked
        saveSongToDatabase(song);

        // Start playing the song
//        Intent intent = new Intent(this, PlayingMusic.class);
//        intent.putExtra("songId", song.getId());  // Check if song.getId() returns a valid value
//        intent.putExtra("songTitle", song.getTitle());
//        intent.putExtra("songArtist", song.getArtist());
//        intent.putExtra("songUrl", song.getUrl());  // Assuming getUrl() returns the song's URL
//        intent.putExtra("songThumbnail", song.getThumbnail());
//        startActivity(intent);
    }
    private void setupRecyclerView() {
        recyclerViewResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewResults.setAdapter(songAdapter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
    @Override
    public void onItemLongClick(Song song) {
        // Handle long click if needed
        // For example, show additional options
    }
    private void performSearch(String query) {
        executorService.execute(() -> {
            try {
                // List all items in the songs directory
                ListResult result = Tasks.await(storageRef.listAll());
                ArrayList<Song> newResults = new ArrayList<>();

                for (StorageReference item : result.getItems()) {
                    String fileName = item.getName().toLowerCase();
                    if (fileName.contains(query.toLowerCase())) {
                        // Get download URL
                        String url = Tasks.await(item.getDownloadUrl()).toString();

                        // Create song object
                        Song song = createSongFromStorageItem(fileName, url);
                        newResults.add(song);
                    }
                }

                // Update UI on main thread
                runOnUiThread(() -> {
                    searchResults.clear();
                    searchResults.addAll(newResults);
                    songAdapter.notifyDataSetChanged();
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(SearchActivity.this,
                                "Search failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    private Song createSongFromStorageItem(String fileName, String url) throws IOException {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            // Set the data source to the file's URL
            retriever.setDataSource(url);

            // Extract metadata
            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // duration in milliseconds

            // Default values if metadata is missing
            if (title == null) {
                // Remove file extension and use filename as the title
                title = fileName.substring(0, fileName.lastIndexOf('.')).replace('_', ' ');
            }
            if (artist == null) {
                artist = "Unknown"; // Default if artist is not available
            }
            if (duration != null) {
                // Convert duration from milliseconds to "mm:ss" format
                long durationMs = Long.parseLong(duration);
                duration = convertMillisecondsToTime(durationMs); // Method to convert ms to "mm:ss"
            } else {
                duration = "0:00"; // Default duration
            }

            // Extract thumbnail (album art) if available
            byte[] artwork = retriever.getEmbeddedPicture();
            String thumbnail;
            if (artwork != null) {
                // Convert byte[] to a base64 string or save it to the device and use a URL
                thumbnail = "data:image/jpeg;base64," + Base64.encodeToString(artwork, Base64.DEFAULT);
            } else {
                thumbnail = "default_thumbnail_url"; // Default thumbnail if artwork is not available
            }

            // Return a new Song object with extracted metadata
            return new Song(title, artist, url, duration, thumbnail);

        } catch (Exception e) {
            e.printStackTrace();
            // Return a song with default values in case of an error
            return new Song(fileName, "Unknown", url, "0:00", "default_thumbnail_url");
        } finally {
            retriever.release(); // Always release the retriever
        }
    }

    // Helper method to convert milliseconds to "mm:ss" format
    private String convertMillisecondsToTime(long milliseconds) {
        int minutes = (int) (milliseconds / 1000 / 60);
        int seconds = (int) ((milliseconds / 1000) % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void clearResults() {
        searchResults.clear();
        songAdapter.notifyDataSetChanged();
    }

    private void saveSongToDatabase(Song song) {
        executorService.execute(() -> {
            try {
                database.songDAO().addSong(song);
                runOnUiThread(() ->
                        Toast.makeText(SearchActivity.this,
                                "Song saved to library",
                                Toast.LENGTH_SHORT).show()
                );
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(SearchActivity.this,
                                "Failed to save song: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}

