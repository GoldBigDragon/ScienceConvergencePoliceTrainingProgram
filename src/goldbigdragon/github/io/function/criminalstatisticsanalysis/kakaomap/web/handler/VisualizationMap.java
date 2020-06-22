package goldbigdragon.github.io.function.criminalstatisticsanalysis.kakaomap.web.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import goldbigdragon.github.io.util.FileUtil;

import java.io.*;

public class VisualizationMap implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        FileUtil fu = new FileUtil();
        String response = fu.readResourceFile("web/criminalstatisticsanalysis/view/visualizationMap.html");
        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
