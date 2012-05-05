package web;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;


public class FirstHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        String req = readRequest( httpExchange.getRequestBody() );
        String headers = readHeaders(httpExchange.getRequestHeaders());
        String uri = readURI(httpExchange.getRequestURI());


        StringBuilder sbuf = new StringBuilder();
        sbuf.append("First response from the server\n\n");

        sbuf.append(req);
        sbuf.append(headers);
        sbuf.append(uri);

        respond(sbuf.toString(), httpExchange);
    }

    void respond(String response, HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream os = httpExchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    public static String readRequest(InputStream is) throws IOException {
        BufferedInputStream buf = new BufferedInputStream(is);
        StringBuffer sbuf = new StringBuffer();
        byte[] bytes = new byte[256];
        while (buf.read(bytes,0,255) != -1){
            sbuf.append(bytes);
        }
        return sbuf.toString();
    }

    public static String readHeaders(Headers headers){
        StringBuffer sbuf = new StringBuffer();
        for (String key : headers.keySet()){
            sbuf.append("Key: ");
            sbuf.append(key);
            sbuf.append(": \n");
            List<String> values = headers.get(key);
            for(String val : headers.get(key)){
                sbuf.append("  ");
                sbuf.append(val);
                sbuf.append(",\n");
            }
            sbuf.append("\n");
        }
        return sbuf.toString();
    }

    public static String readURI(URI uri){
        StringBuilder sbuf = new StringBuilder();

        sbuf.append("\nURI info:");
        sbuf.append("\n path: " + uri.getPath());
        sbuf.append("\n query: " + uri.getQuery());

        return sbuf.toString();
    }

}
