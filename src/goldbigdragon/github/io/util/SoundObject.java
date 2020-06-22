package goldbigdragon.github.io.util;

import goldbigdragon.github.io.enums.SoundType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundObject {
    public SoundType soundType;
    public MediaPlayer mediaPlayer;

    public SoundObject(SoundType soundType) {
        this.soundType = soundType;
        this.mediaPlayer = new MediaPlayer(new Media(getClass().getClassLoader().getResource(soundType.filePath).toExternalForm()));
    }

    public void playSound() {
        mediaPlayer.play();
        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer = new MediaPlayer(new Media(getClass().getClassLoader().getResource(soundType.filePath).toExternalForm()));
        });
    }
}
