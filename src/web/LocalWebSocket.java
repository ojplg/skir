package web;

import org.eclipse.jetty.websocket.WebSocket;

import java.io.IOException;

public class LocalWebSocket implements WebSocket.OnTextMessage {

    private Connection _connection;

    @Override
    public void onMessage(String s) {
        System.out.println("onMessage called local web socket " + s);
        try {
            _connection.sendMessage(s);
        } catch (IOException ioe){
            System.out.println("Troubles");
            ioe.printStackTrace();
        }
    }

    @Override
    public void onOpen(Connection connection) {
        System.out.println("onOpen called on LocalWebSocket");
        _connection = connection;
    }

    @Override
    public void onClose(int i, String s) {
        System.out.println("onClose called on LocalWebSocket " + s);
    }
}
