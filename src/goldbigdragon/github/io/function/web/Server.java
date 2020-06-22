package goldbigdragon.github.io.function.web;
import java.net.InetSocketAddress;
import java.util.Map;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import goldbigdragon.github.io.function.alarm.AlarmAPI;

public class Server {

    private HttpServer server = null;

    public void start(int port, Map<String, HttpHandler> handler) {
        try{
            server = HttpServer.create(new InetSocketAddress(port), 0);
            for(String path : handler.keySet()) {
                server.createContext("/" + path, handler.get(path));
            }
            server.setExecutor(null);
        } catch(Exception e) {
            new AlarmAPI().serverCreateError();
        }
        if(server != null) {
            server.start();
        } else {
            new AlarmAPI().serverCreateError();
        }
    }

    public void close() {
        server.stop(0);
        server = null;
    }
}