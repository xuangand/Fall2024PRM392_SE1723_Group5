package fu.se.spotifi.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import fu.se.spotifi.DAO.PlaylistDAO;
import fu.se.spotifi.DAO.SongDAO;
import fu.se.spotifi.DAO.SongListDAO;
import fu.se.spotifi.Entities.Playlist;
import fu.se.spotifi.Entities.Song;
import fu.se.spotifi.Entities.SongList;

@Database(entities = {Song.class, Playlist.class, SongList.class}, version = 1, exportSchema = false)
public abstract class SpotifiDatabase extends RoomDatabase {
    private static volatile SpotifiDatabase INSTANCE;

    public static SpotifiDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (SpotifiDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    SpotifiDatabase.class, "spotifi_database")
                            .fallbackToDestructiveMigration() // Allows migration to occur
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract SongDAO songDAO();
    public abstract PlaylistDAO playlistDAO();
    public abstract SongListDAO songListDAO();
}
