package web;

import map.Country;
import org.eclipse.jetty.websocket.WebSocket;
import state.MapEventListener;
import state.Player;

import java.io.IOException;

public class LocalWebSocket implements WebSocket.OnTextMessage, MapEventListener {

    private Connection _connection;

    @Override
    public void onMessage(String s) {
        System.out.println("onMessage called local web socket " + s);
    }

    @Override
    public void onOpen(Connection connection) {
        System.out.println("onOpen called on LocalWebSocket");
        _connection = connection;
        // must signal here that connection was created, so that countries can be painted!
    }

    @Override
    public void onClose(int i, String s) {
        System.out.println("onClose called on LocalWebSocket " + s);
    }

    @Override
    public void mapChanged(Country country, Player player, int armyCount) {
        try {
            if( _connection != null && _connection.isOpen()) {
                StringBuilder buf = new StringBuilder();
                buf.append("{\"country\":\"");
                buf.append(country.getName());
                buf.append("\",\"color\":\"");
                buf.append(player.getColor());
                buf.append("\",\"count\":");
                buf.append(armyCount);
                buf.append("}");

                System.out.println("Sending message " + buf.toString());
                _connection.sendMessage(buf.toString());
            } else {
                System.out.println("WARN SENDING ON CLOSED (or null) WEB SOCKET");
            }
        } catch (IOException ioe){
            System.out.println("Troubles");
            ioe.printStackTrace();
        }

    }
}
