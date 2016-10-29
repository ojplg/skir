package web;

import map.Country;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket;
import org.json.simple.JSONObject;
import state.GameEventListener;
import state.Player;
import state.SignalReady;

import java.io.IOException;

public class LocalWebSocket implements WebSocket.OnTextMessage, GameEventListener {

    private static final Logger _log = LogManager.getLogger(LocalWebSocket.class);

    private static volatile int _counter = 0;
    private String _id;
    private Connection _connection;
    private SignalReady _signalReady;

    public LocalWebSocket(SignalReady ready){
        _signalReady = ready;
        _counter++ ;
        _id = String.valueOf(_counter);
    }

    public String getId(){
        return _id;
    }

    @Override
    public void onMessage(String s) {
        _log.info("onMessage called local web socket " + s);
    }

    @Override
    public void onOpen(Connection connection) {
        _log.info("onOpen called on LocalWebSocket");
        _connection = connection;
        // must signal here that connection was created, so that countries can be painted!
        _signalReady.signal(_id);
    }

    @Override
    public void onClose(int i, String s) {
        _log.info("onClose called on LocalWebSocket " + s);
    }

    @Override
    public void mapChanged(Country country, Player player, int armyCount) {
        JSONObject jObject = new JSONObject();
        jObject.put("message_type","map_update");
        jObject.put("country", country.getName());
        jObject.put("color", player.getColor());
        jObject.put("count", armyCount);
        sendJson(jObject);
    }

    public void playerChanged(Player player, int armyCount, int countryCount){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type","player_update");
        jObject.put("color",player.getColor().toLowerCase());
        jObject.put("armies", armyCount);
        jObject.put("countries", countryCount);
        sendJson(jObject);
    }

    private void sendJson(JSONObject jObject){
        try {
            if( _connection != null && _connection.isOpen()) {
                String msg = jObject.toJSONString();
                _log.info("Sending message " + msg);
                _connection.sendMessage(msg);
            } else {
                _log.warn("WARN SENDING ON CLOSED (or null) WEB SOCKET");
            }
        } catch (IOException ioe){
            _log.error("Could not send a web socket message", ioe);
        }
    }
}
