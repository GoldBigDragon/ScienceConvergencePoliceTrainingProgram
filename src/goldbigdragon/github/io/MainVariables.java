package goldbigdragon.github.io;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;


public class MainVariables {
    public long utc = 0;
    public short year = 0;
    public short month = 0;
    public short day = 0;
    public short dayOfWeek = 0;
    public int connectTimeout = 1000;
    public Map<String, Thread> threads = new HashMap<>();


    public boolean playSound = true;
    public char language = 'K';


    public MainVariables(){
        this.utc = Calendar.getInstance().getTimeInMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        String[] dTime = formatter.format(new Date()).split("/");
        this.year = Short.parseShort(dTime[0]);
        this.month = Short.parseShort(dTime[1]);
        this.day = Short.parseShort(dTime[2]);
        this.dayOfWeek = (short) Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }
}
