package fu.se.spotifi.Activities;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fu.se.spotifi.Adapters.QueueAdapter;
import fu.se.spotifi.Adapters.SongAdapter;
import fu.se.spotifi.Const.Utils;
import fu.se.spotifi.DAO.SongDAO;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Queue;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;

public class QueueSong extends AppCompatActivity {
    private ArrayList<Queue> queueArrayList=new ArrayList<>(); // Use Queue instead of Song
    private RecyclerView queueRecyclerView; // Rename variable for clarity
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    private MediaPlayer mediaPlayer;
    private QueueAdapter adapter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_queue_song);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        RecyclerView queueRecyclerView = findViewById(R.id.queueRecyclerView);
        queueRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Attach the adapter immediately with an empty list
        List<Queue> queueList = new ArrayList<>();


        // Load data from the database in the background
        executorService.execute(() -> {
            SpotifiDatabase db = SpotifiDatabase.getInstance(this);
            List<Queue> queues = db.queueDAO().loadAllQueues();

            runOnUiThread(() -> {
                queueList.addAll(queues);
                QueueAdapter adapter = new QueueAdapter(this, queueList);
                queueRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged(); // Notify the adapter that data has changed
            });
        });
    }


    private void playSong(Queue queue) {
        // If a song is already playing, stop and reset the player
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }

        // Create a new MediaPlayer instance and play the selected song
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(queue.getSongUrl()); // Get the song URL from the Queue object
            mediaPlayer.prepare(); // Prepare the player asynchronously
            mediaPlayer.start(); // Start the song
            Toast.makeText(this, "Playing: " + queue.getSongTitle(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error playing song", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown(); // Shut down the executor service
    }
}
