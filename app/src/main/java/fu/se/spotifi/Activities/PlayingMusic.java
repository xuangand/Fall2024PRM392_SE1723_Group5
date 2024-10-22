package fu.se.spotifi.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import fu.se.spotifi.Const.Utils;
import fu.se.spotifi.DAO.SongDAO;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Playlist;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.Entities.SongList;
import fu.se.spotifi.R;

public class PlayingMusic extends AppCompatActivity {
    private ScheduledExecutorService scheduledExecutorService;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);
    private MediaPlayer musicPlayer = new MediaPlayer();
    //<editor-fold defaultstate="collapsed" desc="Get selected song">
    Intent getSong;
    int selectedSong = -1;
    //</editor-fold>

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
        //<editor-fold defaultstate="collapsed" desc="Initialization">
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
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Fill song data to layout">
        executorService.execute(() -> {
            getSong = getIntent();
            selectedSong = getSong.getIntExtra("songId", -1);
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            SongDAO songDAO = db.songDAO();
            Song selectedSongData = songDAO.getSongById(selectedSong);
            if (selectedSongData != null) {
                title.setText(selectedSongData.getTitle());
                artist.setText(selectedSongData.getArtist());
                runOnUiThread(() -> Glide.with(this).load(selectedSongData.getThumbnail()).into(albumArt));
            }

            try {
                // Set the data source from URL and prepare the player asynchronously
                musicPlayer.setDataSource(selectedSongData.getUrl());
                musicPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            musicPlayer.setOnPreparedListener(mp -> {
                // Set the duration once the song is ready
                String duration = utils.milisecondsToString(musicPlayer.getDuration());
                endDurationTimer.setText(duration);

                // Set up the SeekBar max value
                seekBar.setMax(musicPlayer.getDuration());

                // Play the song once prepared
                musicPlayer.start();
                playPauseButton.setImageResource(R.drawable.ic_pause);
            });

            musicPlayer.setLooping(true);
            musicPlayer.seekTo(0);
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Set up seekBar">
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if (isFromUser) {
                    musicPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update UI while song is playing">
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
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
        //</editor-fold>
        lyrics.setOnClickListener(view -> {
            Intent iLyrics = new Intent(PlayingMusic.this, Lyrics.class);
            startActivity(iLyrics);
        });
        details.setOnClickListener(view -> {
            Intent iDetails = new Intent(PlayingMusic.this, SongDetails.class);
            startActivity(iDetails);
        });
        upNext.setOnClickListener(view -> {
            Intent intent = new Intent(PlayingMusic.this, QueueSong.class);
            startActivity(intent);
        });

//        playPauseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (musicPlayer.isPlaying()) {
//                    musicPlayer.pause();
//                    playPauseButton.setImageResource(R.drawable.ic_play);
//                } else {
//                    musicPlayer.start();
//                    playPauseButton.setImageResource(R.drawable.ic_pause);
//                }
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
        //<editor-fold defaultstate="collapsed" desc="Retrieve metadata">
//        ffmmr = new FFmpegMediaMetadataRetriever();
//        try {
//
//            ffmmr.setDataSource(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.unlive));
//
//            String titleExtracted = ffmmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
//            String artistExtracted = ffmmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
//            byte[] artBytes = ffmmr.getEmbeddedPicture();
//            title.setText(titleExtracted != null ? titleExtracted : "Unknown Title");
//            artist.setText(artistExtracted != null ? artistExtracted : "Unknown Title");
//
//            if (artBytes != null) {
//                // Convert byte array to Bitmap
//                Bitmap bitmap = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);
//                // Set the Bitmap to the ImageView
//                albumArt.setImageBitmap(bitmap);
//            } else {
//                // Set a placeholder image if no album art is found
//                albumArt.setImageResource(R.drawable.album_art_placeholder);
//            }
//        } catch (Exception e) {
//            e.getMessage();
//            // Set a placeholder image in case of an error
//            albumArt.setImageResource(R.drawable.album_art_placeholder);
//        } finally {
//            ffmmr.release();
//        }
        //</editor-fold>
    }

    interface Callback<T> {
        void onResult(T result);
    }

    private void saveSongToPlaylist(String playlistName) {
        executorService.execute(() -> {
            selectedSong = getSong.getIntExtra("songId", -1);
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            Playlist playlist = db.playlistDAO().getPlaylistByName(playlistName);
            if (playlist != null) {
                SongList songList = new SongList(playlist.getId(), selectedSong);
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
        executorService.shutdown(); // Shut down the executor service
        if (musicPlayer != null) {
            musicPlayer.release(); // Release MediaPlayer resources
            musicPlayer = null;
        }
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown(); // Shut down the scheduled executor service
        }
    }
}
