package fu.se.spotifi.Activities;

import android.Manifest;
import android.content.Context;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StreamDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
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

public class SongManagement extends BaseActivity {
    private Button btnInsert, btnUpdate, btnDelete, btnLoad;
    private EditText edtId, edtName;
    private ListView lv;
    private ArrayList<Song> songList;
    private ArrayAdapter<Song> myAdapter;
    private SpotifiDatabase db;
    private String title, duration, artist, thumbnail;
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
        // <editor-fold desc="Declare variable">
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
        // </editor-fold>

        // <editor-fold desc="Init database and firebase">
        db = Room.databaseBuilder(getApplicationContext(), SpotifiDatabase.class, "spotifi_database").allowMainThreadQueries().build();
        grandPermission();
        FirebaseApp.initializeApp(this);
        // </editor-fold>

        btnInsert.setOnClickListener(v -> {
            String url = edtName.getText().toString();
            retrieveMetadataAndUploadToDatabase(url);
            System.out.println("Thumbnail here: " + thumbnail);

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
        setupBottomNavigation();
    }
    public void ShowList(){
        songList.clear();
        songList.addAll(db.songDAO().loadAllSongs());
        myAdapter.notifyDataSetChanged();
    }
    public void retrieveMetadataAndUploadToDatabase(String url){

        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        try {
            mmr.setDataSource(url);

            String titleExtracted = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE);
            String artistExtracted = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
            String durationExtracted = mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION);
            title = titleExtracted != null ? titleExtracted : "Unknown Title";
            artist = artistExtracted != null ? artistExtracted : "Unknown Artist";
            duration = durationExtracted != null ? durationExtracted : "0:00";

            File albumArtFile = retrieveAlbumArt(SongManagement.this, url, title);

            if (albumArtFile != null) {
                uploadImageToFirebase(albumArtFile,new UploadCallbacks() {
                    @Override
                    public void onSuccess(String downloadUrl) {
                        // The image was uploaded successfully, and here's the download URL
                        System.out.println("Image uploaded successfully! Download URL: " + downloadUrl);
                        thumbnail = downloadUrl;
                        System.out.println(thumbnail);
                        Song song = new Song(title, artist, url, duration, thumbnail);
                        db.songDAO().addSong(song);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        // The upload failed
                        System.out.println("Upload failed: " + exception.getMessage());
                    }
                });
            }

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
    public File retrieveAlbumArt(Context context, String musicFilePath, String title) {
        FFmpegMediaMetadataRetriever retriever = new FFmpegMediaMetadataRetriever();
        try {
            retriever.setDataSource(musicFilePath);
            byte[] albumArt = retriever.getEmbeddedPicture();

            if (albumArt != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);

                // Save the file in the app's internal storage
                File outputFile = new File(context.getCacheDir(), title +"_cover.png");

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
    public void uploadImageToFirebase(File imageFile, UploadCallbacks callbacks) {
        // Get the instance of Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a reference to the location where the image will be stored
        StorageReference storageRef = storage.getReference().child("album_art/" + imageFile.getName());

        // Get the Uri of the file
        Uri fileUri = Uri.fromFile(imageFile);

        // Start the upload task
        UploadTask uploadTask = storageRef.putFile(fileUri);

        // Monitor the upload progress
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Call the callback for success
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                callbacks.onSuccess(uri.toString());
            });

        }).addOnFailureListener(exception -> {
            // Call the callback for failure
            callbacks.onFailure(exception);
        });
    }
    public interface UploadCallbacks {
        void onSuccess(String downloadUrl);
        void onFailure(Exception exception);
    }
}
