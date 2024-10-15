package fu.se.spotifi.Activities;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fu.se.spotifi.Adapters.PlaylistAdapter;
import fu.se.spotifi.DAO.PlaylistDAO;
import fu.se.spotifi.DAO.SongListDAO;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Playlist;
import fu.se.spotifi.R;

public class Library extends BaseActivity implements PlaylistAdapter.OnPlaylistOptionClickListener {
    private RecyclerView playlistRecyclerView;
    private PlaylistAdapter playlistAdapter;
    private ArrayList<Playlist> playlistList;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.libraryLayout), (v, insets) -> {
            Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBar.left, systemBar.top, systemBar.right, systemBar.bottom);
            return insets;
        });

        // Initialize RecyclerView and Adapter
        playlistRecyclerView = findViewById(R.id.playlistRecyclerView);
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playlistList = new ArrayList<>();
        playlistAdapter = new PlaylistAdapter(this, playlistList, this);
        playlistRecyclerView.setAdapter(playlistAdapter);

        executorService = Executors.newSingleThreadScheduledExecutor();
        // Load playlists from the database
        loadPlaylists();

        // Set up the "New playlist" button
        Button newPlaylistButton = findViewById(R.id.newPlaylistButton);
        newPlaylistButton.setOnClickListener(v -> showAddDialog());

        setupBottomNavigation();
    }

    private void loadPlaylists() {
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            PlaylistDAO playlistDAO = db.playlistDAO();
            SongListDAO songListDAO = db.songListDAO();
            playlistList.clear();
            for (Playlist playlist : playlistDAO.loadAllPlaylist()) {
                int songCount = songListDAO.loadAllSongList().stream()
                        .filter(songList -> songList.getPlaylistId() == playlist.getId())
                        .mapToInt(songList -> 1)
                        .sum();
                playlist.setSongCount(songCount);
                playlistList.add(playlist);
            }
            runOnUiThread(() -> playlistAdapter.notifyDataSetChanged());
        });
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_new_playlist, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText playlistNameInput = dialogView.findViewById(R.id.playlistNameInput);
        Button createButton = dialogView.findViewById(R.id.createButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        createButton.setOnClickListener(v -> {
            String playlistName = playlistNameInput.getText().toString();
            if (!playlistName.isEmpty()){
                createNewPlaylist(playlistName);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showEditDialog(Playlist playlist) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_new_playlist, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText playlistNameInput = dialogView.findViewById(R.id.playlistNameInput);
        playlistNameInput.setText(playlist.getName());
        Button createButton = dialogView.findViewById(R.id.createButton);
        createButton.setText("Update");
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        createButton.setOnClickListener(v -> {
            String playlistName = playlistNameInput.getText().toString();
            if (!playlistName.isEmpty()){
                updatePlaylist(playlist, playlistName);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void createNewPlaylist(String playlistName) {
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            PlaylistDAO playlistDAO = db.playlistDAO();
            Playlist newPlaylist = new Playlist();
            newPlaylist.setName(playlistName);
            newPlaylist.setThumbnail(R.drawable.album_cover_image); // Set a default thumbnail
            playlistDAO.addPlaylist(newPlaylist);
            loadPlaylists();
        });
    }

    private void updatePlaylist(Playlist playlist, String newName) {
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            PlaylistDAO playlistDAO = db.playlistDAO();
            playlist.setName(newName);
            playlistDAO.updatePlaylist(playlist);
            loadPlaylists();
        });
    }

    private void deletePlaylist(Playlist playlist) {
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            PlaylistDAO playlistDAO = db.playlistDAO();
            playlistDAO.deletePlaylist(playlist);
            loadPlaylists();
        });
    }

    @Override
    public void onEditClick(Playlist playlist) {
        showEditDialog(playlist);
    }

    @Override
    public void onDeleteClick(Playlist playlist) {
        deletePlaylist(playlist);
    }
}
