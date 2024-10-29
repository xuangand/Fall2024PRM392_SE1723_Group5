package fu.se.spotifi.Widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import fu.se.spotifi.R;

public class MusicPlayerWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), fu.se.spotifi.R.layout.widget_music_player);

        // Set up intents for the buttons with FLAG_IMMUTABLE
        Intent playPauseIntent = new Intent(context, MusicWidgetService.class);
        playPauseIntent.setAction("ACTION_PLAY_PAUSE");
        PendingIntent playPausePendingIntent = PendingIntent.getService(
                context, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widget_play_pause_button, playPausePendingIntent);

        Intent nextIntent = new Intent(context, MusicWidgetService.class);
        nextIntent.setAction("ACTION_NEXT");
        PendingIntent nextPendingIntent = PendingIntent.getService(
                context, 1, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widget_next_button, nextPendingIntent);

        Intent previousIntent = new Intent(context, MusicWidgetService.class);
        previousIntent.setAction("ACTION_PREVIOUS");
        PendingIntent previousPendingIntent = PendingIntent.getService(
                context, 2, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        views.setOnClickPendingIntent(R.id.widget_previous_button, previousPendingIntent);

        // Update the widget with the modified views
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

