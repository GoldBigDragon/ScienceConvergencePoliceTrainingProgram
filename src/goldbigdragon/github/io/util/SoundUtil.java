package goldbigdragon.github.io.util;

import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.enums.SoundType;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Random;

public class SoundUtil {

    public static Map<String, List<SoundObject>> musicMap = new HashMap<>();

    public SoundUtil(){
        for(SoundType st : SoundType.values()) {
            List<SoundObject> soundList = new ArrayList<>();
            for(int count = 0; count < 5; count++)
                soundList.add(new SoundObject(st));
            musicMap.put(st.name(), soundList);
        }
    }

    public static void playSound(SoundType sound){
        if(Main.mainVariables.playSound)
            musicMap.get(sound.name()).get(new Random().nextInt(5)).playSound();
    }
}
