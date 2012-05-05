package web;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class SelectionHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String uri = FirstHandler.readURI(httpExchange.getRequestURI());

        System.out.print("selection handler: " + uri);

        httpExchange.sendResponseHeaders(200, -1);
        OutputStream os = httpExchange.getResponseBody();
        os.close();

    }
}
