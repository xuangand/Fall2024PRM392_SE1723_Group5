// PlayingMusic.java
package fu.se.spotifi.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fu.se.spotifi.Const.Utils;
import fu.se.spotifi.DAO.SongDAO;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Playlist;
import fu.se.spotifi.Entities.SongList;
import fu.se.spotifi.R;

public class PlayingMusic extends AppCompatActivity {
    private ScheduledExecutorService executorService;
    private MediaPlayer musicPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_playing_music);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView lyrics = findViewById(R.id.lyrics);
        TextView details = findViewById(R.id.details);
        TextView upNext = findViewById(R.id.upNext);
        TextView title = findViewById(R.id.songTitle);
        TextView artist = findViewById(R.id.artist);
        ImageButton playPauseButton = findViewById(R.id.playPauseButton);
        SeekBar seekBar = findViewById(R.id.progressBar);
        TextView progressDurationTimer = findViewById(R.id.progressDurationTimer);
        TextView endDurationTimer = findViewById(R.id.endDurationTimer);
        ImageButton previousButton = findViewById(R.id.previousButton);
        ImageButton nextButton = findViewById(R.id.nextButton);
        ImageButton back_dropdownButton = findViewById(R.id.back_dropdownButton);
        Button saveButton = findViewById(R.id.SaveSongToPlaylist);
        ImageView albumArt = findViewById(R.id.albumArt);
        Utils utils = new Utils();

        SpotifiDatabase db = SpotifiDatabase.getInstance(this);
        SongDAO songDAO;

        // Retrieve song details from the intent
        Intent intent = getIntent();
        String songUrl = intent.getStringExtra("songUrl");
        String songTitle = intent.getStringExtra("songTitle");
        String songArtist = intent.getStringExtra("songArtist");
        String songThumbnail = intent.getStringExtra("songThumbnail");

        // Set song details to the UI
        title.setText(songTitle);
        artist.setText(songArtist);
        if (songThumbnail != null && !songThumbnail.isEmpty()) {
            Glide.with(this)
                    .load(songThumbnail)
                    .placeholder(R.drawable.album_art_placeholder)
                    .into(albumArt);
        } else {
            albumArt.setImageResource(R.drawable.album_art_placeholder);
        }

        // Initialize and start the media player
        musicPlayer = new MediaPlayer();
        try {
            musicPlayer.setDataSource(songUrl);
            musicPlayer.prepare();
            musicPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String duration = utils.milisecondsToString(musicPlayer.getDuration());
        endDurationTimer.setText(duration);

        seekBar.setMax(musicPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if (isFromUser) {
                    musicPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleWithFixedDelay(() -> {
            if (musicPlayer != null && musicPlayer.isPlaying()) {
                final double current = musicPlayer.getCurrentPosition();
                final String elapseTime = utils.milisecondsToString((int) current);

                // Update UI on the main thread
                runOnUiThread(() -> {
                    progressDurationTimer.setText(elapseTime);
                    seekBar.setProgress((int) current);
                });
            }
        }, 0, 1, TimeUnit.SECONDS);

        lyrics.setOnClickListener(view -> {
            Intent iLyrics = new Intent(PlayingMusic.this, Lyrics.class);
            startActivity(iLyrics);
        });
        details.setOnClickListener(view -> {
            Intent iDetails = new Intent(PlayingMusic.this, SongDetails.class);
            startActivity(iDetails);
        });
        upNext.setOnClickListener(view -> {
            Intent itent = new Intent(PlayingMusic.this, QueueSong.class);
            startActivity(itent);
        });

        playPauseButton.setOnClickListener(view -> {
            if (musicPlayer.isPlaying()) {
                musicPlayer.pause();
                playPauseButton.setImageResource(R.drawable.ic_play);
            } else {
                musicPlayer.start();
                playPauseButton.setImageResource(R.drawable.ic_pause);
            }
        });

        saveButton.setOnClickListener(view -> showPlaylistDialog());
    }

    private void showPlaylistDialog() {
        getPlaylistsFromDatabase(playlists -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose a Playlist");

            builder.setItems(playlists.toArray(new String[0]), (dialog, which) -> {
                // Handle the playlist selection
                String selectedPlaylist = playlists.get(which);
                saveSongToPlaylist(selectedPlaylist);
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }

    private void getPlaylistsFromDatabase(Callback<List<String>> callback) {
        executorService.execute(() -> {
        SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            List<Playlist> playlists = db.playlistDAO().loadAllPlaylist();
            List<String> playlistNames = new ArrayList<>();
            for (Playlist playlist : playlists) {
                playlistNames.add(playlist.getName());
            }
            runOnUiThread(() -> callback.onResult(playlistNames));
        });
    }

    interface Callback<T> {
        void onResult(T result);
    }

    private void saveSongToPlaylist(String playlistName) {
        // Retrieve song details from the intent
        Intent intent = getIntent();
        int songId = intent.getIntExtra("songId", -1);

        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            Playlist playlist = db.playlistDAO().getPlaylistByName(playlistName);
            if (playlist != null) {
                SongList songList = new SongList(playlist.getId(), songId);
                db.playlistDAO().addSongToPlaylist(songList);
                runOnUiThread(() -> Toast.makeText(this, "Song added to " + playlistName, Toast.LENGTH_SHORT).show());
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Playlist not found", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicPlayer != null) {
            musicPlayer.release();
            musicPlayer = null;
        }
        executorService.shutdown(); // Shut down the executor service
    }
}

