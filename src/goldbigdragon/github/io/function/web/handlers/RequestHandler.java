package goldbigdragon.github.io.function.web.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHandler {

    public byte[] getOutput(HttpExchange arg, boolean isGet) throws IOException {
        Map<String, Object> parameters = new HashMap<>();
        String query;
        String responseParse;
        arg.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        if(isGet) {
            responseParse = "\n";
            URI requestedUri = arg.getRequestURI();
            query = requestedUri.getRawQuery();
        } else {
            responseParse = ",   ";
            InputStreamReader isr = new InputStreamReader(arg.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            query = br.readLine();
        }
        parseQuery(query, parameters);
        StringBuilder sb = new StringBuilder();
        for (String key : parameters.keySet()) {
            sb.append(key);
            sb.append("=");
            sb.append(parameters.get(key));
            sb.append(responseParse);
        }
        String response = sb.toString();
        if(isGet) {
            System.out.println("GET  : " +response);
        }
        else {
            System.out.println("POST : " +response);
        }
        return response.getBytes();
    }

    private void parseQuery (String query, Map<String, Object> parameters) throws UnsupportedEncodingException {
        if (query != null) {
            String[] pairs = query.split("[&]");
            String[] param;
            String key;
            String value;
            List<String> values;
            Object obj;
            for (String pair : pairs) {
                param = pair.split("=");
                key = null;
                value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
                }
                if (param.length > 1) {
                    value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
                }
                if (parameters.containsKey(key)) {
                    obj = parameters.get(key);
                    values = new ArrayList<>();
                    if (obj instanceof List<?>) {
                        values.add(value);
                    } else if (obj instanceof String) {
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }
}
