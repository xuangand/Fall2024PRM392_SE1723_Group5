package fu.se.spotifi.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class PlayingMusicTabView extends ConstraintLayout {
    ImageView currentSongThumbnail;
    TextView currentSongTitle;
    TextView currentArtist;
    private ConstraintLayout playingMusicTab;
    private ImageButton playPauseButton;
    private SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public PlayingMusicTabView(@NonNull Context context) {
        super(context);
        init(context);
    }
    public PlayingMusicTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PlayingMusicTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        // Inflate the layout
        LayoutInflater.from(context).inflate(R.layout.playing_music_tab, this, true);

        // Find views
        playPauseButton = findViewById(R.id.playPauseButton);
        playingMusicTab = findViewById(R.id.playingMusicTab);
        currentSongThumbnail = findViewById(R.id.currentSongThumbnail);
        currentSongTitle = findViewById(R.id.currentSongTitle);
        currentArtist = findViewById(R.id.currentArtist);

        // Retrieve selectedSong from SharedPreferences
        prefs = context.getSharedPreferences("music_app_spotifi", Context.MODE_PRIVATE);
        int selectedSong = prefs.getInt("selectedSong", -1);
        updateClickability(selectedSong != -1, selectedSong);

        // Initialize the listener to detect changes
        if (preferenceChangeListener == null) {
            preferenceChangeListener = (sharedPreferences, key) -> {
                if ("selectedSong".equals(key)) {
                    int updatedSelectedSong = prefs.getInt("selectedSong", -1);
                    updateClickability(updatedSelectedSong != -1, selectedSong);
                }
            };
        }

        // Register the listener
        prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);



        // Set a click listener on the main layout, excluding playPauseButton
        playingMusicTab.setOnClickListener(v -> {
            if (playingMusicTab.isClickable()) {
                openPlayingMusicActivity(context);
            }
        });

        // Override the click listener on playPauseButton to prevent it from opening the activity
        playPauseButton.setOnClickListener(v -> {
            // Handle play/pause action here
        });
    }

    private void openPlayingMusicActivity(Context context) {
        context.startActivity(new Intent(context, PlayingMusic.class));
    }
    private void fillSongData(int selectedSong){
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(getContext());
            Song song = db.songDAO().getSongById(selectedSong);
            if(song != null) {
                currentSongTitle.setText(song.getTitle());
                currentArtist.setText(song.getArtist());

                Activity activity = (Activity) getContext();
                activity.runOnUiThread(() -> Glide.with(getContext()).load(song.getThumbnail()).into(currentSongThumbnail));
            }
        });
    }
    private void updateClickability(boolean isClickable, int selectedSong) {
        playingMusicTab.setClickable(isClickable);
        playPauseButton.setClickable(isClickable);
        //Fill data if a song is selected
        if (isClickable) {
            fillSongData(selectedSong);
        }
    }
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Unregister the listener when the view is detached to avoid memory leaks
        if (prefs != null && preferenceChangeListener != null) {
            prefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        }
    }
}
