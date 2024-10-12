package fu.se.spotifi.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import fu.se.spotifi.DAO.SongDAO;
import fu.se.spotifi.Database.SpotifiDatabase;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.R;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class SongManagement extends AppCompatActivity {
    private Button btnInsert, btnUpdate, btnDelete, btnLoad;
    private EditText edtId, edtName;
    private ListView lv;
    private ArrayList<Song> songList;
    private ArrayAdapter<Song> myAdapter;
    private SpotifiDatabase db;
    private String title, duration, artist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_song_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnInsert = (Button) findViewById(R.id.btnInsert);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnLoad = (Button) findViewById(R.id.btnLoad);
        edtName = (EditText) findViewById(R.id.edtName);
        edtId = (EditText) findViewById(R.id.edtId);
        lv = (ListView) findViewById(R.id.lv);
        songList = new ArrayList<>();
        myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, songList);
        lv.setAdapter(myAdapter);

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                db = SpotifiDatabase.getDatabase(getApplicationContext());
//                // Perform database operations here
//            }
//        }).start();
        db = Room.databaseBuilder(getApplicationContext(), SpotifiDatabase.class, "spotifi_database").allowMainThreadQueries().build();
        grandPermission();

        btnInsert.setOnClickListener(v -> {
            String url = edtName.getText().toString();
            retrieveMetadata(url);
            Song song = new Song(title, artist, url, duration);
            db.songDAO().addSong(song);
            Toast.makeText(SongManagement.this, "Insert Successful", Toast.LENGTH_SHORT).show();
            ShowList();
        });

        btnDelete.setOnClickListener(v -> {
            int id = Integer.parseInt(edtId.getText().toString());
            Song song = new Song();
            song.setId(id);
            db.songDAO().deleteSong(song);
            Toast.makeText(SongManagement.this, "Delete Successful", Toast.LENGTH_SHORT).show();
            ShowList();
        });
        btnLoad.setOnClickListener(v -> {
            ShowList();
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idItem = String.valueOf(songList.get(position).getId());
                edtId.setText(idItem);
                edtName.setText(songList.get(position).getUrl());
            }
        });
    }
    public void ShowList(){
        songList.clear();
        songList.addAll(db.songDAO().loadAllSongs());
        myAdapter.notifyDataSetChanged();
    }
    public void retrieveMetadata(String url){

        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        //FirebaseStorage storage = FirebaseStorage.getInstance();
        //StorageReference storageRef = storage.getReference();
        //StorageReference musicRef = storageRef.child("songs/Ima Boku Underground kara by Kessoku Band.mp3");
//        musicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                // The download URL is successfully retrieved
//                String url = uri.toString();
//
//                try {
//                    // Use the URL directly as the data source
//                    mmr.setDataSource(url, new HashMap<String, String>());
//
//                    // Retrieve metadata
//                    String titleExtracted = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
//                    String artistExtracted = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
//                    //String album = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
//                    String durationExtracted = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
//                    title = titleExtracted != null ? titleExtracted : "Unknown Title";
//                    artist = artistExtracted != null ? artistExtracted : "Unknown Artist";
//                    duration = durationExtracted != null ? durationExtracted : "0:00";
//
//                    // Use the metadata as needed
//                } catch (IllegalArgumentException e) {
//                    // Handle the error if metadata cannot be retrieved
//                } finally {
//                    mmr.release();  // Ensure to release the retriever when done
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle any errors (e.g., file not found or access denied)
//            }
//        });
        try {
            mmr.setDataSource(url);

            String titleExtracted = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
            String artistExtracted = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
            String durationExtracted = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
            title = titleExtracted != null ? titleExtracted : "Unknown Title";
            artist = artistExtracted != null ? artistExtracted : "Unknown Artist";
            duration = durationExtracted != null ? durationExtracted : "0:00";

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            mmr.release();
        }
    }
    public void grandPermission(){
        if (ContextCompat.checkSelfPermission(SongManagement.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(SongManagement.this, new String[]{Manifest.permission.INTERNET}, 101);
    }
}
