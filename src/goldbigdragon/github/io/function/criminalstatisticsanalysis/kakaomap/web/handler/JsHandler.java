package goldbigdragon.github.io.function.criminalstatisticsanalysis.kakaomap.web.handler;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import goldbigdragon.github.io.util.FileUtil;

import java.io.*;

public class JsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        FileUtil fu = new FileUtil();
        String response = fu.readResourceFile("web/criminalstatisticsanalysis/view/map.js");
        Headers h = exchange.getResponseHeaders();
        h.add("Content-Type", "text/javascript");
        exchange.sendResponseHeaders(200, 0);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();

    }
}
