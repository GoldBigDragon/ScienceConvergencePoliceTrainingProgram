package goldbigdragon.github.io.function.menu.main;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import goldbigdragon.github.io.util.WebUtil;

public class MainAPI extends WebUtil {

    public MainAPI(boolean serverCheck) {
        super(serverCheck);
    }

    public String requestInstanceUrl() {
        String returnUrl = null;
        String url = "/keyword/extraction/instance";
        returnUrl = sendGetString(url, true);
        return returnUrl;
    }

    public String requestBroadcastMessage()
    {
        String broadcastMessage = null;
        String url = "/broadcast/extraction";
        JsonArray result = sendGet(url, false);
        if(result != null) {
            for(int count = 0; count < result.size(); count++) {
                JsonObject jo = (JsonObject) result.get(count);
                broadcastMessage = jo.get("message").getAsString();
            }
        }
        return broadcastMessage;
    }

    public void updateLastLoginDate()
    {
        String url = "/user/login";
        JsonObject json = new JsonObject();
        json.addProperty("id", "ii");
        sendPost(url, json, false);
    }
}
