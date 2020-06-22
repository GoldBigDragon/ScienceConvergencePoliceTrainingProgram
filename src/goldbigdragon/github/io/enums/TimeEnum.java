package goldbigdragon.github.io.enums;
public enum TimeEnum {
    ALL(null, -1, -1, -1),
    YEAR("%Y", -1, -1, -1),
    MONTH("%m", 31536000, 1, 12),
    DAY("%d", 2592000, 1, 31),
    WEEK("%w", 604800, 0, 6),
    HOUR("%H", 86400, 0, 24),
    MINUTE("%M", 3600, 0, 59),
    SECONDS("%S", 60, 0, 59);


    final public String symbol;
    final public int afterCount;
    final public int startTime;
    final public int endTime;

    TimeEnum(String symbol, int afterCount, int startTime, int endTime) {
        this.symbol = symbol;
        this.afterCount = afterCount;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
