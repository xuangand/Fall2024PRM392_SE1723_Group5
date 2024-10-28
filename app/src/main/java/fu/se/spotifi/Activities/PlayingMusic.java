package fu.se.spotifi.Activities;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.AudioAttributes;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import fu.se.spotifi.Const.Utils;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Playlist;
import fu.se.spotifi.Entities.Queue;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.Entities.SongList;
import fu.se.spotifi.MusicService;
import fu.se.spotifi.R;
import fu.se.spotifi.Widgets.MusicPlayerWidget;
import fu.se.spotifi.Widgets.MusicWidgetService;

public class PlayingMusic extends AppCompatActivity {
    private ScheduledExecutorService scheduledExecutorService;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);
    private final ExecutorService playerService = Executors.newSingleThreadExecutor();
    private MediaPlayer musicPlayer = new MediaPlayer();
    static ExoPlayer player;
    private MediaSession mediaSession;
    Utils utils = new Utils();


    //<editor-fold defaultstate="collapsed" desc="Initialize selected song">
    Intent getSong;
    int selectedSong = -1;
    private List<MediaItem> playlist = new ArrayList<>();
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Initialization">
    TextView lyrics;
    TextView details;
    TextView upNext;
    TextView title;
    TextView artist;
    ImageButton playPauseButton;
    SeekBar seekBar;
    TextView progressDurationTimer;
    TextView endDurationTimer;
    ImageButton previousButton;
    ImageButton nextButton;
    ImageButton back_dropdownButton;
    Button saveButton;
    ImageView albumArt;
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
        SpotifiDatabase db = SpotifiDatabase.getInstance(this);

        lyrics = findViewById(R.id.lyrics);
        details = findViewById(R.id.details);
        upNext = findViewById(R.id.upNext);
        title = findViewById(R.id.songTitle);
        artist = findViewById(R.id.artist);
        playPauseButton = findViewById(R.id.playPauseButton);
        seekBar = findViewById(R.id.progressBar);
        progressDurationTimer = findViewById(R.id.progressDurationTimer);
        endDurationTimer = findViewById(R.id.endDurationTimer);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        back_dropdownButton = findViewById(R.id.back_dropdownButton);
        saveButton = findViewById(R.id.SaveSongToPlaylist);
        albumArt = findViewById(R.id.albumArt);
        //</editor-fold>

        setupExoPlayer();
        loadPlaylistFromDatabase();

        selectedSong = getIntent().getIntExtra("songId", -1);
        if (selectedSong != -1) {
            playSong(selectedSong);
        }
        SharedPreferences prefs = getSharedPreferences("music_app_spotifi", Context.MODE_PRIVATE);
        if (selectedSong == -1) {
            selectedSong = prefs.getInt("selectedSong", -1); //in case selecteSong didn't get from intent
        }
        //store selected song to reuse it later
        prefs.edit().putInt("selectedSong", selectedSong).apply();

        //<editor-fold defaultstate="collapsed" desc="Fill song data from intent">
//        executorService.execute(() -> {
//            SharedPreferences prefs = getSharedPreferences("music_app_spotifi", Context.MODE_PRIVATE);
//            if (selectedSong == -1) {
//                selectedSong = prefs.getInt("selectedSong", -1); //in case selecteSong didn't get from intent
//            }
//            //store selected song to reuse it later
//            prefs.edit().putInt("selectedSong", selectedSong).apply();
//
//            Song selectedSongData = db.songDAO().getSongById(selectedSong);
//            if (selectedSongData != null) {
//                title.setText(selectedSongData.getTitle());
//                artist.setText(selectedSongData.getArtist());
//                runOnUiThread(() -> Glide.with(this).load(selectedSongData.getThumbnail()).into(albumArt));
//                try {
//                    // Set the data source from URL and prepare the player asynchronously
//                    musicPlayer.setDataSource(selectedSongData.getUrl());
//                    musicPlayer.prepareAsync();
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            musicPlayer.setOnPreparedListener(mp -> {
//                // Set the duration once the song is ready
//                String duration = utils.milisecondsToString(musicPlayer.getDuration());
//                endDurationTimer.setText(duration);
//
//                // Set up the SeekBar max value
//                seekBar.setMax(musicPlayer.getDuration());
//
//                // Play the song once prepared
//                musicPlayer.start();
//                playPauseButton.setImageResource(R.drawable.ic_pause);
//            });
//
//            musicPlayer.setLooping(true);
//        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Set up seekBar">
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
//                if (isFromUser) {
//                    musicPlayer.seekTo(progress);
//                    seekBar.setProgress(progress);
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="Update UI while song is playing">
//        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
//        scheduledExecutorService.scheduleWithFixedDelay(() -> {
//            if (player != null && player.isPlaying()) {
//                final double current = player.getCurrentPosition();
//                final String elapseTime = utils.milisecondsToString((int) current);
//
//                // Update UI on the main thread
//                runOnUiThread(() -> {
//                    progressDurationTimer.setText(elapseTime);
//                    seekBar.setProgress((int) current);
//                });
//            }
//        }, 0, 1, TimeUnit.SECONDS);
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

        findViewById(R.id.playPauseButton).setOnClickListener(view -> {
            if (player.isPlaying()) {
                player.pause();
                playPauseButton.setImageResource(R.drawable.ic_play); // Update to play icon
            } else {
                player.play();
                playPauseButton.setImageResource(R.drawable.ic_pause); // Update to pause icon
            }
        });
        nextButton.setOnClickListener(view -> {
//            executorService.execute(() -> {
//                Queue currentQueueEntry = db.queueDAO().getSongFromQueue(selectedSong);
//                if (currentQueueEntry != null) {
//                    Song nextSong = getNextSongInQueue(currentQueueEntry.getSongOrder());
//                    if (nextSong != null) {
//                        playSong(nextSong); // Method to play the selected song
//                    } else {
//                        runOnUiThread(() -> Toast.makeText(this, "End of queue", Toast.LENGTH_SHORT).show());
//                    }
//                }
//            });
            player.seekToNextMediaItem();
        });
        previousButton.setOnClickListener(view -> {
//            executorService.execute(() -> {
//                Queue currentQueueEntry = db.queueDAO().getSongFromQueue(selectedSong);
//
//                if (currentQueueEntry != null) {
//                    Song previousSong = getPreviousSongInQueue(currentQueueEntry.getSongOrder());
//                    if (previousSong != null) {
//                        playSong(previousSong); // Method to play the selected song
//                    } else {
//                        runOnUiThread(() -> Toast.makeText(this, "Start of queue", Toast.LENGTH_SHORT).show());
//                    }
//                }
//            });
            player.seekToPreviousMediaItem();
        });
        setupSeekBar();

        saveButton.setOnClickListener(view -> showPlaylistDialog());
    }

    private void setupExoPlayer() {
        // Initialize ExoPlayer with MediaSession
        player = new ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, true) //Audio focus: turn off other sound if this one is playing
                .setHandleAudioBecomingNoisy(true) //Automatically pause when headphone is disconnected or when other app interrupt like phone call
                .setWakeMode(C.WAKE_MODE_LOCAL).build(); //Playing even when phone is turned off

        //mediaSession = new MediaSession.Builder(this, player).build();

        player.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(MediaItem mediaItem, int reason) {
                updateUIForMediaItem(mediaItem);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY && player.getDuration() > 0) {
                    endDurationTimer.setText(utils.milisecondsToString((int) player.getDuration()));
                    seekBar.setMax((int) player.getDuration());
                }
            }

            @Override
            public void onIsPlayingChanged(boolean isPlaying) {
                if (isPlaying) {
                    playPauseButton.setImageResource(R.drawable.ic_pause);
                    startSeekBarUpdater(); // Start updating seek bar when playing
                } else {
                    playPauseButton.setImageResource(R.drawable.ic_play);
                    stopSeekBarUpdater();  // Stop updating seek bar when paused
                }
            }
        });
    }

    private void updateUIForMediaItem(MediaItem mediaItem) {
        getSongFromMediaItem(mediaItem, song -> {
            if (song != null) {
                title.setText(song.getTitle());
                artist.setText(song.getArtist());
                Glide.with(this).load(song.getThumbnail()).into(albumArt);
            } else {
                Toast.makeText(this, "Song data not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSongFromMediaItem(MediaItem mediaItem, SongCallback callback) {
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            Song song = db.songDAO().getSongByUrl(mediaItem.localConfiguration.uri.toString());

            runOnUiThread(() -> callback.onSongRetrieved(song));
        });
    }


    private void setupSeekBar() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
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
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            selectedSong = getSong.getIntExtra("songId", -1);
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

    private Song getNextSongInQueue(int currentOrder) {
        int nextOrder = currentOrder + 1;
        SpotifiDatabase db = SpotifiDatabase.getInstance(this);
        Queue nextQueueEntry = db.queueDAO().getSongByOrder(nextOrder);

        if (nextQueueEntry != null) {
            return db.songDAO().getSongById(nextQueueEntry.getSongId());
        }
        return null; // Handle if there’s no next song
    }

    private Song getPreviousSongInQueue(int currentOrder) {
        int previousOrder = currentOrder - 1;
        SpotifiDatabase db = SpotifiDatabase.getInstance(this);
        Queue previousQueueEntry = db.queueDAO().getSongByOrder(previousOrder);

        if (previousQueueEntry != null) {
            return db.songDAO().getSongById(previousQueueEntry.getSongId());
        }
        return null; // Handle if there’s no previous song
    }

    private void playSong(int songId) {
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            Song selectedSongData = db.songDAO().getSongById(songId);

            if (selectedSongData != null) {
                MediaItem mediaItem = MediaItem.fromUri(selectedSongData.getUrl());
                runOnUiThread(() -> {
                    player.setMediaItem(mediaItem);
                    player.prepare();
                    player.play();
                    updateCurrentSongWidget(this);
                    Intent serviceIntent = new Intent(this, MusicService.class);
                    serviceIntent.setAction("ACTION_PLAY");
                    serviceIntent.putExtra("SONG_TITLE", selectedSongData.getTitle());
                    serviceIntent.putExtra("SONG_ARTIST", selectedSongData.getArtist());
                    startService(serviceIntent);
                });
            } else {
                runOnUiThread(() ->
                        Toast.makeText(this, "Song not found", Toast.LENGTH_SHORT).show()
                );
            }
        });

//        runOnUiThread(() -> {
//            title.setText(song.getTitle());
//            artist.setText(song.getArtist());
//            Glide.with(this).load(song.getThumbnail()).into(albumArt);
//        });
//        try {
//            musicPlayer.reset();
//            musicPlayer.setDataSource(song.getUrl());
//            musicPlayer.prepareAsync();
//        } catch (IOException e) {
//            e.getMessage();
//        }
//
//        musicPlayer.setOnPreparedListener(mp -> {
//            seekBar.setMax(musicPlayer.getDuration());
//            musicPlayer.start();
//            playPauseButton.setImageResource(R.drawable.ic_pause);
//        });
//
//        selectedSong = song.getId(); // Update the current song ID
    }

        private void loadPlaylistFromDatabase() {
            executorService.execute(() -> {
                SpotifiDatabase db = SpotifiDatabase.getInstance(this);
                List<Integer> currentQueue = db.queueDAO().getSongsFromQueue();
                List<Song> songs = db.songDAO().getSongsFromQueue(currentQueue);

                playlist.clear(); // Clear previous items from the playlist
                for (Song song : songs) {
                    playlist.add(MediaItem.fromUri(song.getUrl()));
                }

                runOnUiThread(() -> {
                    player.setMediaItems(playlist);
                    player.prepare();
                    if (!playlist.isEmpty()) {
                        player.play(); // Automatically start playing the first song
                        updateCurrentSongWidget(this); // Update widget with the first song
                    }
                });
            });
        }

        public static void togglePlayPause(Context context) {
            if (player != null) {
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.play();
                }
                // Update the widget with the current song info
                updateCurrentSongWidget(context);
            }
        }

        public static void playNext(Context context) {
            if (player != null) {
                player.seekToNextMediaItem();
                updateCurrentSongWidget(context);
            }
        }

        public static void playPrevious(Context context) {
            if (player != null) {
                player.seekToPreviousMediaItem();
                updateCurrentSongWidget(context);
            }
        }

        private static void updateCurrentSongWidget(Context context) {
            MediaItem currentItem = player.getCurrentMediaItem();
            if (currentItem != null) {
                Log.d("MusicWidget", "Current item: " + currentItem.mediaId);
                getSongFromMediaItem(context, currentItem, song -> {
                    if (song != null) {
                        Log.d("MusicWidget", "Updating widget with song: " + song.getTitle());
                        updateWidget(context, song.getTitle(), song.getArtist());
                    } else {
                        Log.d("MusicWidget", "Song not found in database.");
                    }
                });
            } else {
                Log.d("MusicWidget", "No current item found.");
            }
        }

        // Update this helper method to accept Context
        private static void getSongFromMediaItem(Context context, MediaItem mediaItem, SongCallback callback) {
            executorService.execute(() -> {
                SpotifiDatabase db = SpotifiDatabase.getInstance(context);
                String songUrl = mediaItem.localConfiguration.uri.toString();
                Log.d("MusicWidget", "Querying song with URL: " + songUrl);
                Song song = db.songDAO().getSongByUrl(songUrl);
                new Handler(Looper.getMainLooper()).post(() -> callback.onSongRetrieved(song));
            });
        }

        // Callback interface to handle song retrieval
        interface SongCallback {
            void onSongRetrieved(Song song);
        }

    // Update widget method provided
    public static void updateWidget(Context context, String songTitle, String artistName) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, MusicPlayerWidget.class));

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_music_player);
            views.setTextViewText(R.id.widget_song_title, songTitle);
            views.setTextViewText(R.id.widget_artist_name, artistName);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shut down the executor service
        if (player != null) {
            player.release(); // Release MediaPlayer resources
            player = null;
        }
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown(); // Shut down the scheduled executor service
        }
    }


    private final Handler handler = new Handler();
    private final Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if (player != null && player.isPlaying()) {
                int currentPosition = (int) player.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                progressDurationTimer.setText(utils.milisecondsToString(currentPosition));
                handler.postDelayed(this, 1000); // Update every second
            }
        }
    };

    private void startSeekBarUpdater() {
        handler.post(updateSeekBarRunnable);
    }

    private void stopSeekBarUpdater() {
        handler.removeCallbacks(updateSeekBarRunnable);
    }
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
