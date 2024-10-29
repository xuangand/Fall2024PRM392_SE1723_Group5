package fu.se.spotifi.Activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fu.se.spotifi.Const.Utils;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class SongDetails extends AppCompatActivity {

    Utils utils = new Utils();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_details);

        //Retrieve the songId from the intent
        int songId = getIntent().getIntExtra("songId", -1);

        executorService.execute(() -> {
            //Query the database for the song with the given songId
            Song song = SpotifiDatabase.getInstance(this).songDAO().getSongById(songId);

            runOnUiThread(() -> {
                //Update the UI with the song details
                if (song != null) {
                    TextView songTitle = findViewById(R.id.songTitle);
                    TextView artistName = findViewById(R.id.artistName);
                    TextView songDetails = findViewById(R.id.songDetails);
                    TextView artistDetails = findViewById(R.id.artistDetails);
                    ImageView albumThumbnail = findViewById(R.id.albumThumbnail);
                    String duration = utils.milisecondsToString(song.getDuration());

                    songTitle.setText(song.getTitle());
                    artistName.setText(song.getArtist());
                    songDetails.setText(song.getTitle());
                    artistDetails.setText("Written by\n" + song.getArtist() + "\n\nDuration\n" + duration);
                    Glide.with(this).load(song.getThumbnail()).into(albumThumbnail);
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
