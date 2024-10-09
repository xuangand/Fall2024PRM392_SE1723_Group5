package fu.se.spotifi.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

import fu.se.spotifi.DAO.SongDAO;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.R;

public class PlayingMusic extends AppCompatActivity {

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
        ImageView albumArt = findViewById(R.id.albumArt);
        SpotifiDatabase db = SpotifiDatabase.getDatabase(this);
        SongDAO songDAO;

        MediaMetadataRetriever mediaMetadataRetriever;

        MediaPlayer musicPlayer = MediaPlayer.create(this,R.raw.unlive);
        musicPlayer.setLooping(true);
        musicPlayer.seekTo(0);

        String duration = milisecondsToString(musicPlayer.getDuration());
        endDurationTimer.setText(duration);

        seekBar.setMax(musicPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if (isFromUser){
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (musicPlayer != null){
                    if(musicPlayer.isPlaying()) {
                        try {
                            final double current = musicPlayer.getCurrentPosition();
                            final String elapseTime = milisecondsToString((int)current);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDurationTimer.setText(elapseTime);
                                    seekBar.setProgress((int)current);
                                }
                            });

                            Thread.sleep(1000);
                        } catch(InterruptedException e){}
                    }
                }
            }
        }).start();
//        title.setText(musicPlayer.getTrackInfo().);


        lyrics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iLyrics = new Intent(PlayingMusic.this, Lyrics.class);
                startActivity(iLyrics);
            }
        });
        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iDetails = new Intent(PlayingMusic.this, SongDetails.class);
                startActivity(iDetails);
            }
        });
        upNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent itent = new Intent(PlayingMusic.this, QueueSong.class);
                startActivity(itent);
            }
        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicPlayer.isPlaying()){
                    musicPlayer.pause();
                    playPauseButton.setImageResource(R.drawable.ic_play);
                }else{
                    musicPlayer.start();
                    playPauseButton.setImageResource(R.drawable.ic_pause);
                }
            }
        });

        mediaMetadataRetriever = new MediaMetadataRetriever();
        try {

            mediaMetadataRetriever.setDataSource(getApplicationContext(), Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.unlive));

            String titleExtracted = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artistExtracted = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            byte[] artBytes = mediaMetadataRetriever.getEmbeddedPicture();
            title.setText(titleExtracted != null ? titleExtracted : "Unknown Title");
            artist.setText(artistExtracted != null ? artistExtracted : "Unknown Title");

            if (artBytes != null) {
                // Convert byte array to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);
                // Set the Bitmap to the ImageView
                albumArt.setImageBitmap(bitmap);
            } else {
                // Set a placeholder image if no album art is found
                albumArt.setImageResource(R.drawable.album_art_placeholder);
            }
        }catch (Exception e) {
            e.printStackTrace();
            // Set a placeholder image in case of an error
            albumArt.setImageResource(R.drawable.album_art_placeholder);
        } finally {
            try {
                mediaMetadataRetriever.release(); // Always release the retriever when done
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public String milisecondsToString(int time){
        String elapsedTime = "";
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        elapsedTime = minutes + ":";
        if(seconds < 10)
            elapsedTime += "0";
        elapsedTime += seconds;
        return elapsedTime;
    }
}
