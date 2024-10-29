package fu.se.spotifi.Activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fu.se.spotifi.Adapters.SongAdapter;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class SearchActivity extends BaseActivity implements SongAdapter.OnItemClickListener, SongAdapter.OnItemLongClickListener {
    private SearchView searchView;
    private RecyclerView recyclerViewResults;
    private ArrayList<Song> searchResults;
    private SongAdapter songAdapter;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ExecutorService executorService;
    private SpotifiDatabase database;
    private long lastSearchTime = 0;
    private static final long DEBOUNCE_DELAY_MS = 300;
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
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSearchTime < DEBOUNCE_DELAY_MS) return; // Only perform search after debounce delay
        lastSearchTime = currentTime;

        executorService.execute(() -> {
            try {
                ListResult result = Tasks.await(storageRef.listAll());
                ArrayList<Song> newResults = new ArrayList<>();
                for (StorageReference item : result.getItems()) {
                    if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                        String url = Tasks.await(item.getDownloadUrl()).toString();
                        Song song = createSongFromStorageItem(item.getName(), url);
                        newResults.add(song);
                    }
                }
                runOnUiThread(() -> updateSearchResults(newResults));
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(SearchActivity.this, "Search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
    private void updateSearchResults(ArrayList<Song> newResults) {
        searchResults.clear();
        searchResults.addAll(newResults);
        songAdapter.notifyDataSetChanged();
    }

    private Song createSongFromStorageItem(String fileName, String url) throws IOException {
        String title = fileName.substring(0, fileName.lastIndexOf('.')).replace('_', ' ');
        String artist = "Unknown";
        String duration = "0:00";
        String thumbnail = url;

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(url);
            String tempTitle = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String tempArtist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String tempDuration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            title = tempTitle != null ? tempTitle : title;
            artist = tempArtist != null ? tempArtist : artist;
            duration = tempDuration != null ? convertMillisecondsToTime(Long.parseLong(tempDuration)) : duration;

            byte[] artwork = retriever.getEmbeddedPicture();
            if (artwork != null) {
                thumbnail = "data:image/jpeg;base64," + Base64.encodeToString(artwork, Base64.DEFAULT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }

        return new Song(title, artist, url, duration, thumbnail);
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
                Song existingSong = database.songDAO().getSongByTitleAndArtist(song.getTitle(), song.getArtist());

                if (existingSong == null) {
                    // Generate thumbnail using album art
                    File albumArtFile = retrieveAlbumArt(SearchActivity.this, song.getUrl(), song.getTitle());
                    String thumbnailPath = albumArtFile != null ? albumArtFile.getAbsolutePath() : song.getUrl();

                    // Set the local thumbnail path
                    song.setThumbnail(thumbnailPath);
                    database.songDAO().addSong(song);

                    runOnUiThread(() ->
                            Toast.makeText(SearchActivity.this,
                                    "Song saved to library",
                                    Toast.LENGTH_SHORT).show()
                    );
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(SearchActivity.this,
                                    "Song already in library",
                                    Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(SearchActivity.this,
                                "Failed to save song: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    public File retrieveAlbumArt(Context context, String musicFilePath, String title) {
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        try {
            retriever.setDataSource(musicFilePath);
            byte[] albumArt = retriever.getEmbeddedPicture();

            if (albumArt != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
                // Save the file in the app's internal storage
                File outputFile = new File(context.getCacheDir(), title + "_cover.png");
                FileOutputStream out = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.close();
                return outputFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return null;
    }

}

