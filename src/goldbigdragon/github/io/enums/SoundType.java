package goldbigdragon.github.io.enums;

public enum SoundType {
    SEARCH_START("resources/sound/searchStart.mp3"),
    SEARCH_STOP("resources/sound/searchStop.mp3"),
    CHECK("resources/sound/check.mp3"),
    ENABLE("resources/sound/enable.mp3"),
    DISABLE("resources/sound/disable.mp3"),
    SCREENSHOT("resources/sound/screenshot.mp3")
    ;

    public final String filePath;
    SoundType(String filePath) {
        this.filePath = filePath;
    }
}
