package fu.se.spotifi.Const;

import androidx.room.TypeConverter;

import java.util.Date;

public class Utils {
    public Utils() {
    }
    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
    public String milisecondsToString(int time){
        String elapsedTime = "";
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        elapsedTime = minutes + ":";
        if(seconds < 10)
            elapsedTime += "0";
        elapsedTime += seconds;
        return elapsedTime;
    }

    public String milisecondsToString(String stringTime){
        int time = Integer.parseInt(stringTime);
        String elapsedTime = "";
        int minutes = time / 1000 / 60;
        int seconds = time / 1000 % 60;
        elapsedTime = minutes + ":";
        if(seconds < 10)
            elapsedTime += "0";
        elapsedTime += seconds;
        return elapsedTime;
    }
}
