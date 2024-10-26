package fu.se.spotifi.DAO;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fu.se.spotifi.Entities.Queue;
import fu.se.spotifi.Entities.Song;

@Dao
public interface QueueDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addQueue(Queue queue);


    @Update
    void updateQueue(Queue queue);

    @Delete
    void deleteQueue(Queue queue);

    @Query("SELECT * FROM queue")
    List<Queue> loadAllQueues();

    @Query("SELECT * FROM queue WHERE queueId = :queueId")
    Queue getQueueById(int queueId);

    @Query("DELETE FROM queue")
    void clearQueue();
    @Query("SELECT * FROM queue WHERE songOrder = :nextOrder")
    Queue getSongByOrder(int nextOrder);

    @Query("SELECT songId FROM queue")
    List<Integer> getSongsFromQueue();

}


