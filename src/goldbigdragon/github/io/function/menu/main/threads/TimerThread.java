package goldbigdragon.github.io.function.menu.main.threads;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.ThreadType;
import goldbigdragon.github.io.function.BaseThread;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimerThread extends BaseThread {

    private int count = 0;

    public TimerThread(){
        super(ThreadType.BASIC, "Timmer");
        Main.mainVariables.threads.put("Timer", this);
        this.start();
    }

    @Override
    public void run() {
        try {
            while(true){
                if(count < 180) {
                    count++;
                    Main.mainVariables.utc+=1000;
                } else {
                    count = 0;
                    Main.mainVariables.utc = Calendar.getInstance().getTimeInMillis();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
                    String[] dTime = formatter.format(new Date()).split("/");

                    if (Main.mainVariables.day != Integer.parseInt(dTime[2])
                            || Main.mainVariables.month != Integer.parseInt(dTime[1])
                            || Main.mainVariables.year != Short.parseShort(dTime[0]))
                    {
                        Main.mainVariables.year = Short.parseShort(dTime[0]);
                        Main.mainVariables.month = Short.parseShort(dTime[1]);
                        Main.mainVariables.day = Short.parseShort(dTime[2]);
                        int dayOfWeekChanged = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
                        Main.mainVariables.dayOfWeek = (short) dayOfWeekChanged;
                    }
                }
                sleep(1000);
            }
        } catch(InterruptedException e) {}
    }
}
