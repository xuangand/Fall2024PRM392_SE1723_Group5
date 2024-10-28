package fu.se.spotifi.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "queue")
public class Queue {

    @PrimaryKey(autoGenerate = true)
    private int queueId;
    private int songId;
    private String status;
    private int songOrder;

    // Getter and Setter for queueId
    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    // Getter and Setter for songId
    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    // Getter and Setter for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter and Setter for order
    public int getSongOrder() {
        return songOrder;
    }

    public void setSongOrder(int songOrder) {
        this.songOrder = songOrder;
    }
}
