package goldbigdragon.github.io.function.menu.main;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import goldbigdragon.github.io.Main;
import goldbigdragon.github.io.function.BaseView;
import goldbigdragon.github.io.function.alarm.AlarmAPI;
import goldbigdragon.github.io.util.WebUtil;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainView extends BaseView {
    public void view(){
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        super.view("/resources/fxml/menu/main.fxml", "YSU-SCPTP", 800, 500, true, false);
//        versionCheck();
    }

    public boolean versionCheck() {
        try
        {
            StringBuilder sb = new StringBuilder();
            URL url = new URL("http://goldbigdragon.github.io/version/ScienceConvergencePoliceTrainingProgram.json");
            URLConnection urlConnection = url.openConnection();
            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
            byte[] bufRead = new byte[10];
            int lenRead = 0;
            while ((lenRead = in.read(bufRead)) >0)
                sb.append(new String(bufRead, 0, lenRead));

            JsonParser parser = new JsonParser();
            JsonObject element = parser.parse(sb.toString()).getAsJsonObject();
            String lastVersion = element.get("lastVersion").getAsString();
            String lastUpdate = element.get("lastUpdate").getAsString();
            int server = element.get("server").getAsInt();

            WebUtil.serverIp = getConnection(server);

            if( ! Main.version.equals(lastVersion) || ! Main.lastUpdate.equals(lastUpdate)) {
                new AlarmAPI().newVersionNotice();
            }
            return true;
        }
        catch (IOException ioe) {
            new AlarmAPI().serverConnectionError();
        }
        return false;
    }

    private String getConnection(int server) {
        String returnValue = null;
        try
        {
            StringBuilder sb = new StringBuilder();
            URL url = new URL("http://goldbigdragon.github.io/view/connection");
            URLConnection urlConnection = url.openConnection();
            BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
            byte[] bufRead = new byte[10];
            int lenRead = 0;
            while ((lenRead = in.read(bufRead)) >0)
                sb.append(new String(bufRead, 0, lenRead));
            char s = (char) server;

            Pattern p = Pattern.compile(Character.toString(s) + ":([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3}):([0-9]{1,5})");
            Matcher m = p.matcher(sb.toString());
            while (m.find()) {
                String[] split = m.group().trim().split(":");
                returnValue = "http://" + split[1]+":"+split[2];
            }
        }
        catch (IOException ioe) {
            new AlarmAPI().serverConnectionError();
        }
        return returnValue;
    }
}