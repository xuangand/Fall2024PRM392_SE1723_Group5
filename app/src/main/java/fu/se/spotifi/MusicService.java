package fu.se.spotifi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import fu.se.spotifi.Activities.Home;

public class MusicService extends Service {

    private static final String CHANNEL_ID = "MusicServiceChannel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel(); // Set up the notification channel once on service creation
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "ACTION_PLAY".equals(intent.getAction())) {
            // Get song title and artist from Intent extras
            String songTitle = intent.getStringExtra("SONG_TITLE");
            String songArtist = intent.getStringExtra("SONG_ARTIST");

            // Show notification with song details
            showCustomNotification(songTitle, songArtist);
        }
        return START_STICKY;
    }

    private void showCustomNotification(String songTitle, String songArtist) {
        // Intent to open the app when the notification is clicked
        Log.d("MusicService", "Showing notification: " + songTitle + " by " + songArtist);
        Intent notificationIntent = new Intent(this, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
        );

        // Create and configure the notification
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(songTitle != null ? songTitle : "Now Playing")
                .setContentText(songArtist != null ? songArtist : "Unknown Artist")
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .build();

        // Start the foreground service with the notification
        startForeground(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("MusicService", "Creating notification channel");
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Playback Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
                Log.d("MusicService", "Notification channel created");
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // This service is unbound; return null for onBind
    }

    @Override
    public void onDestroy() {
        // Stop the foreground service and notification when the service is destroyed
        stopForeground(true);
        super.onDestroy();
    }
}

