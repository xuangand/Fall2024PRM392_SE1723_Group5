package fu.se.spotifi.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import fu.se.spotifi.DAO.SongDAO;
import fu.se.spotifi.Entities.Song;

@Database(entities = {Song.class}, version = 1, exportSchema = false)
public abstract class SpotifiDatabase extends RoomDatabase {
    private static volatile SpotifiDatabase INSTANCE;

    public static SpotifiDatabase getDatabase(final Context context) {
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
}
