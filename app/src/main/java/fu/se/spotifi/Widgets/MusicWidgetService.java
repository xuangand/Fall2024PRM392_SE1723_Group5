package fu.se.spotifi.Widgets;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import fu.se.spotifi.Activities.PlayingMusic;

public class MusicWidgetService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case "ACTION_PLAY_PAUSE":
                    PlayingMusic.togglePlayPause(this); // Pass the service context
                    break;
                case "ACTION_NEXT":
                    PlayingMusic.playNext(this); // Pass the service context
                    break;
                case "ACTION_PREVIOUS":
                    PlayingMusic.playPrevious(this); // Pass the service context
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // We are not binding this service
    }
}


