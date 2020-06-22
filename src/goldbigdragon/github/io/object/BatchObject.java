package goldbigdragon.github.io.object;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BatchObject {

    public PreparedStatement pstmt;
    private int batchCount = 0;
    private int autoExcuteStack;
    private String insertTarget;
    private long startTime;

    public BatchObject(Connection conn, String query, int autoExcuteStack, String insertTarget) throws SQLException {
        pstmt = conn.prepareStatement(query);
        conn.setAutoCommit(false);
        this.autoExcuteStack = autoExcuteStack;
        this.insertTarget = insertTarget;
        this.startTime = System.currentTimeMillis();
    }

    public boolean addBatch() throws SQLException {
        pstmt.addBatch();
        batchCount++;
        if(batchCount > autoExcuteStack) {
            executeBatch();
            return true;
        }
        return false;
    }

    public int executeBatch() throws SQLException {
        int returnInt = pstmt.executeBatch().length;
        batchCount = 0;
        pstmt.clearBatch();
        long milli = System.currentTimeMillis()-startTime;
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
            System.out.println("[SQLITE] : " + returnInt + "개의 " + insertTarget + "가 추가됨. [연산 시작 " + day + "일 " + hour + "시간 " + min + "분 " + sec + "초 이후]");
        } else if(hour > 0) {
            System.out.println("[SQLITE] : " + returnInt + "개의 " + insertTarget + "가 추가됨. [연산 시작 " + hour + "시간 " + min + "분 " + sec + "초 이후]");
        } else if(min > 0) {
            System.out.println("[SQLITE] : " + returnInt + "개의 " + insertTarget + "가 추가됨. [연산 시작 " + min + "분 " + sec + "초 이후]");
        } else if(sec > 0) {
            System.out.println("[SQLITE] : " + returnInt + "개의 " + insertTarget + "가 추가됨. [연산 시작 " + sec + "초 " + milli + "㎧ 이후]");
        } else {
            System.out.println("[SQLITE] : " + returnInt + "개의 " + insertTarget + "가 추가됨. [연산 시작 " + milli + "㎧ 이후]");
        }
        return returnInt;
    }

}
