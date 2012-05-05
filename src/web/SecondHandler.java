package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SecondHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        FileInputStream file = new FileInputStream("html/home.html");

        httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpExchange.getResponseBody();

        int b;
        while (( b=file.read() ) != -1 ){
            os.write(b);
        }
        os.close();
    }
}
