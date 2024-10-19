package fu.se.spotifi.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "queue")
public class Queue {

    @PrimaryKey(autoGenerate = true)
    private int queueId;
    private int songId;
    //private String songTitle;
    //private String songArtist;
    //private String songUrl;
    //private String songThumbnail;
    private String status;
    private int order;

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

//    // Getter and Setter for songTitle
//    public String getSongTitle() {
//        return songTitle;
//    }
//
//    public void setSongTitle(String songTitle) {
//        this.songTitle = songTitle;
//    }
//
//    // Getter and Setter for songArtist
//    public String getSongArtist() {
//        return songArtist;
//    }
//
//    public void setSongArtist(String songArtist) {
//        this.songArtist = songArtist;
//    }
//
//    // Getter and Setter for songUrl
//    public String getSongUrl() {
//        return songUrl;
//    }
//
//    public void setSongUrl(String songUrl) {
//        this.songUrl = songUrl;
//    }
//
//    // Getter and Setter for songThumbnail
//    public String getSongThumbnail() {
//        return songThumbnail;
//    }
//
//    public void setSongThumbnail(String songThumbnail) {
//        this.songThumbnail = songThumbnail;
//    }

    // Getter and Setter for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter and Setter for order
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
