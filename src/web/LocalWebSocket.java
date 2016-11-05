package web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket;
import org.jetlang.channels.Channel;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import play.Channels;
import play.orders.Adjutant;
import play.orders.OrderType;
import state.Player;
import state.event.ClientConnectedEvent;
import state.event.MapChangedEvent;
import state.event.PlayerChangedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalWebSocket implements WebSocket.OnTextMessage {

    private static final Logger _log = LogManager.getLogger(LocalWebSocket.class);

    private static volatile int _counter = 0;
    private String _id;
    private Connection _connection;

    private final Channel<ClientConnectedEvent> _clientConnectedEventChannel;

    public LocalWebSocket(Channels channels, Fiber fiber){
        _counter++ ;
        _id = String.valueOf(_counter);

        _clientConnectedEventChannel = channels.ClientConnectedEventChannel;

        channels.MapChangedEventChannel.subscribe(fiber,
                new Callback<MapChangedEvent>() {
                    @Override
                    public void onMessage(MapChangedEvent mapChangedEvent) {
                        sendJson(mapChangedEvent.toJson());
                    }
                }
        );
        channels.PlayerChangedEventChannel.subscribe(fiber,
                new Callback<PlayerChangedEvent>() {
                    @Override
                    public void onMessage(PlayerChangedEvent playerChangedEvent) {
                        sendJson(playerChangedEvent.toJson());
                    }
                }
        );

        channels.AdjutantChannel.subscribe(fiber,
                new Callback<Adjutant>() {
                    @Override
                    public void onMessage(Adjutant adjutant) {
                        handleNewAdjutant(adjutant);
                    }
                }
        );
    }

    @Override
    public void onMessage(String s) {
        _log.info("onMessage called local web socket " + s);
        JSONParser parser = new JSONParser();
        try {
            JSONObject jObject = (JSONObject) parser.parse(s);
            _log.info("PARSED a message from the client " + jObject + " of type " + jObject.getClass());
        } catch (ParseException pe){
            _log.error("Could not parse json from client " + s, pe);
        }
    }

    @Override
    public void onOpen(Connection connection) {
        _log.info("onOpen called on LocalWebSocket");
        _connection = connection;
        _clientConnectedEventChannel.publish(new ClientConnectedEvent(_id));
    }

    @Override
    public void onClose(int i, String s) {
        _log.info("onClose called on LocalWebSocket " + s);
    }


    private void handleNewAdjutant(Adjutant adjutant){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type","possible_order_types");
        jObject.put("color", adjutant.getActivePlayer().getColor());
        JSONArray array = new JSONArray();
        for(String type : orderTypesToStrings(adjutant.allowableOrders())){
            array.add(type);
        }
        jObject.put("order_types", array);

        sendJson(jObject);
    }

    private List<String> orderTypesToStrings(List<OrderType> types){
        List<String> strings = new ArrayList<String>();
        for(OrderType type : types){
            strings.add(type.toString());
        }
        return strings;
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
