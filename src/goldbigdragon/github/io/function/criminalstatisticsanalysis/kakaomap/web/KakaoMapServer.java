package goldbigdragon.github.io.function.criminalstatisticsanalysis.kakaomap.web;

import com.sun.net.httpserver.HttpHandler;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.kakaomap.web.handler.VisualizationMap;
import goldbigdragon.github.io.function.criminalstatisticsanalysis.kakaomap.web.handler.MapHandler;
import goldbigdragon.github.io.function.web.Server;

import java.util.HashMap;
import java.util.Map;

public class KakaoMapServer extends Server {

    public KakaoMapServer(int port){
        Map<String, HttpHandler> handler = new HashMap<>();
        handler.put("map", new MapHandler());
        handler.put("visualizationMap", new VisualizationMap());
//        handler.put("mapJS", new JsHandler());
        super.start(port, handler);
    }
}
