package web;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BlobHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        System.out.println("hit the blob handler");

        FileInputStream file = new FileInputStream("html/blobs.svg");

        httpExchange.sendResponseHeaders(200, 0);
        OutputStream os = httpExchange.getResponseBody();

        Headers headers = httpExchange.getResponseHeaders();
        headers.add("Content-Type","image/svg+xml");


        int b;
        while (( b=file.read() ) != -1 ){
            //System.out.print(b);
            os.write(b);
        }
        System.out.println("image served");
        os.close();
    }


}
