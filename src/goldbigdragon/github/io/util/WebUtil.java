package goldbigdragon.github.io.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import goldbigdragon.github.io.function.alarm.AlarmAPI;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebUtil {

    private static boolean localTest = false;
    private final String localIp = "http://localhost:3123";
    public static String serverIp = "http://localhost";
    public static boolean isServerOnline = false;
    public static boolean serverCheckMessage = false;

    public WebUtil(boolean serverChceck) {
        if(serverChceck &&  ! serverCheck() && ! localTest && !serverCheckMessage) {
            new AlarmAPI().serverConnectionError();
            serverCheckMessage = true;
        }
    }

    private boolean serverCheck() {
        try {
            URL url = new URL(serverIp);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(50000);
            conn.connect();
            isServerOnline = true;
        } catch (Exception e) {
            isServerOnline = false;
        }
        return isServerOnline;
    }

    public JsonArray sendPost(String sendUrl, JsonObject jsonValue, boolean sendMessage) throws IllegalStateException {
        StringBuilder output = new StringBuilder();
        getWebOutput(false, sendUrl, output, jsonValue, sendMessage);
        return parseJson(output.toString());
    }

    public JsonArray sendGet(String sendUrl, boolean sendMessage) throws IllegalStateException {
        StringBuilder output = new StringBuilder();
        getWebOutput(true, sendUrl, output, null, sendMessage);
        return parseJson(output.toString());
    }

    public String sendGetString(String sendUrl, boolean sendMessage) throws IllegalStateException {
        StringBuilder output = new StringBuilder();
        getWebOutput(true, sendUrl, output, null, sendMessage);
        return output.toString();
    }

    private void getWebOutput(boolean isGet, String sendUrl, StringBuilder output, JsonObject jsonValue, boolean sendMessage) {
        if(connectionCheck(sendMessage)) {
            if(localTest)
                sendUrl = localIp + sendUrl;
            else
                sendUrl = serverIp + sendUrl;
            try {
                URL url = new URL(sendUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Accept", "application/json; charset=utf-8;");
                conn.setRequestProperty("Content-Type", "application/json; charset=utf-8;");
                if(isGet) {
                    conn.setRequestMethod("GET");
                } else {
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setConnectTimeout(10000);
                    conn.connect();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8));
                    bw.write(jsonValue.toString());
                    bw.flush();
                    bw.close();
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    output.append(line);
                }
                conn.disconnect();
                return;
            } catch (IOException | IllegalStateException e) {
            }
        }
    }

    private boolean connectionCheck(boolean sendMessage) {
        if(isServerOnline || localTest) {
            return true;
        } else if(sendMessage){
            new AlarmAPI().serverConnectionError();
        }
        return false;
    }

    private JsonArray parseJson(String raw) {
        if(raw != null && ! raw.equals("[]") && raw.length() > 1) {
            JsonElement je = new JsonParser().parse(raw);
            if(je.isJsonArray()) {
                return je.getAsJsonArray();
            } else if(je.isJsonObject()) {
                JsonArray ja = new JsonArray();
                ja.add(je);
                return ja;
            }
        }
        return null;
    }
}