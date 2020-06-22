package goldbigdragon.github.io.util;

import goldbigdragon.github.io.Main;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ConfigUtil {

    public final String CONFIG_PATH = "c:/YSU/SCPTP/config.txt";

    public void reload() throws IOException {
        File folder = new File(Main.dbDirectory);
        folder.mkdirs();
        File config = new File(CONFIG_PATH);
        if(!config.exists()) {
            save();
        } else {
            FileUtil fu = new FileUtil();
            List<String> prop = fu.readFile(config, "UTF8");
            Map<String, String> propMap = new HashMap<>();
            String[] splitted;
            for(String line : prop) {
                if(line.indexOf("=") != -1) {
                    splitted = line.split("=");
                    propMap.put(splitted[0], splitted[1]);
                }
            }
            if(propMap.containsKey("sound")) {
                if(propMap.get("sound").equals("false"))
                    Main.mainVariables.playSound = false;
            }
            if(propMap.containsKey("language")) {
                Main.mainVariables.language = propMap.get("language").charAt(0);
            }
        }

    }

    public void save() {
        FileUtil fu = new FileUtil();
        File config = new File(CONFIG_PATH);
        try {
            List<String> options = new ArrayList<>();
            options.add("sound=" + Main.mainVariables.playSound);
            options.add("language=" + Main.mainVariables.language);
            fu.writeFile(config, options, "UTF8");
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
}
