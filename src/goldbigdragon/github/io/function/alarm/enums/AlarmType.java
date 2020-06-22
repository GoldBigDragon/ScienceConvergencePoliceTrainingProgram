package goldbigdragon.github.io.function.alarm.enums;

public enum AlarmType {
    WARN("경고"),
    NOTICE("알림"),
    CONFIRM("확인"),
    PROCESSING("처리중")
    ;

    final public String title;

    AlarmType(String title) {
        this.title = title;
    }
}
