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
    public static long stringToMilliseconds(String duration) {
        try {
            String[] timeParts = duration.split(":");
            int minutes = Integer.parseInt(timeParts[0]);
            int seconds = Integer.parseInt(timeParts[1]);
            return (minutes * 60 + seconds) * 1000L; // Convert to milliseconds
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return 0; // Return 0 if there's an issue parsing the string
        }
    }
    public static String milisecondsToString(String duration) {
        try {
            // Split the duration in "mm:ss" format
            String[] timeParts = duration.split(":");
            int minutes = Integer.parseInt(timeParts[0]);
            int seconds = Integer.parseInt(timeParts[1]);

            // Convert to milliseconds (1 minute = 60000 milliseconds, 1 second = 1000 milliseconds)
            int totalMilliseconds = (minutes * 60 + seconds) * 1000;

            // Format it as a string in "mm:ss"
            return String.format("%02d:%02d", minutes, seconds);
        } catch (Exception e) {
            e.printStackTrace();
            return "00:00"; // Default value if there's an error
        }
    }
}
