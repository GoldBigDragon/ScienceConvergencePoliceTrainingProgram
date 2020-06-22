package goldbigdragon.github.io.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private long startMilli;
    private String calcTarget;

    public long stringToEpoch(String time){
        ZoneId zoneId = ZoneId.systemDefault();
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(DATE_FORMAT)).atZone(zoneId).toEpochSecond();
    }

    public String epochToString(long epoch){
        ZoneId zoneId = ZoneId.systemDefault();
        ZoneOffset zoneOffSet = zoneId.getRules().getOffset(LocalDateTime.now());
        return LocalDateTime.ofEpochSecond(epoch, 0, zoneOffSet).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public void calcStart(String target){
        startMilli = System.currentTimeMillis();
        calcTarget = target;
        System.out.println("[" + calcTarget + "] : 계산 시작");
    }

    public void calcEnd(){
        long milli = System.currentTimeMillis()-startMilli;
        int sec = 0;
        int min = 0;
        int hour = 0;
        int day = 0;
        if(milli > 1000) {
            sec = (int) (milli*0.001);
            milli -= (sec*1000);
        }
        if(sec > 60) {
            min = sec/60;
            sec -= (min*60);
        }
        if(min > 60) {
            hour = min/60;
            min -= (hour*60);
        }
        if(hour > 24) {
            day = hour/60;
            hour -= (day*60);
        }
        if(day > 0) {
            System.out.println("[" + calcTarget + "] : 계산 완료 [연산 시작 " + day + "일 " + hour + "시간 " + min + "분 " + sec + "초 이후]");
        } else if(hour > 0) {
            System.out.println("[" + calcTarget + "] : 계산 완료 [연산 시작 " + hour + "시간 " + min + "분 " + sec + "초 이후]");
        } else if(min > 0) {
            System.out.println("[" + calcTarget + "] : 계산 완료 [연산 시작 " + min + "분 " + sec + "초 이후]");
        } else if(sec > 0) {
            System.out.println("[" + calcTarget + "] : 계산 완료 [연산 시작 " + sec + "초 " + milli + "㎧ 이후]");
        } else {
            System.out.println("[" + calcTarget + "] : 계산 완료 [연산 시작 " + milli + "㎧ 이후]");
        }
    }
}
