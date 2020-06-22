package goldbigdragon.github.io.function.criminalstatisticsanalysis.kakaomap.web.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import goldbigdragon.github.io.function.web.handlers.RequestHandler;
import goldbigdragon.github.io.util.FileUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapHandler extends RequestHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange arg) throws IOException {
        FileUtil fu = new FileUtil();
        String response = fu.readResourceFile("web/criminalstatisticsanalysis/view/map.html");
        arg.sendResponseHeaders(200, 0);
        OutputStream os = arg.getResponseBody();
        os.write(getOutput(arg, false));
        os.write(response.getBytes());
        os.close();
    }

    private void sendPost() throws Exception {
        String url = "http://localhost:1120/map";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());
    }
}

//http://theeye.pe.kr/archives/1295
//https://stackoverflow.com/questions/43822262/get-post-data-from-httpexchange