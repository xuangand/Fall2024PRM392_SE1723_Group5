package fu.se.spotifi.Const;

public class Utils {
    public Utils() {
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
