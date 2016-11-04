package web;

import map.Country;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket;
import org.jetlang.channels.Channel;
import org.jetlang.core.Callback;
import org.jetlang.fibers.ThreadFiber;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import play.Channels;
import play.orders.OrderType;
import state.GameEventListener;
import state.OrderEventListener;
import state.Player;
import state.SignalReady;
import state.event.ClientConnectedEvent;
import state.event.MapChangedEvent;
import state.event.PlayerChangedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocalWebSocket implements WebSocket.OnTextMessage, GameEventListener, OrderEventListener {

    private static final Logger _log = LogManager.getLogger(LocalWebSocket.class);

    private static volatile int _counter = 0;
    private String _id;
    private Connection _connection;
    private SignalReady _signalReady;
    private ClientMessageReceiver _clientMessageReceiver;
    private final ThreadFiber fiber = new ThreadFiber();

    private final Channel<ClientConnectedEvent> _clientConnectedEventChannel;

    public LocalWebSocket(SignalReady ready, ClientMessageReceiver clientMessageReceiver, Channels channels){
        _clientMessageReceiver = clientMessageReceiver;
        _signalReady = ready;
        _counter++ ;
        _id = String.valueOf(_counter);

        _clientConnectedEventChannel = channels.ClientConnectedEventChannel;

        fiber.start();
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
    }

    public String getId(){
        return _id;
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

    @Override
    public void mapChanged(Country country, Player player, int armyCount) {
        JSONObject jObject = new JSONObject();
        jObject.put("message_type","map_update");
        jObject.put("country", country.getName());
        jObject.put("color", player.getColor());
        jObject.put("count", armyCount);
        //sendJson(jObject);
    }

    public void playerChanged(Player player, int armyCount, int countryCount){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type","player_update");
        jObject.put("color",player.getColor().toLowerCase());
        jObject.put("armies", armyCount);
        jObject.put("countries", countryCount);
        sendJson(jObject);
    }

    public void possibleOrders(Player player, List<OrderType> possibilities){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type","possible_order_types");
        jObject.put("color", player.getColor());
        JSONArray array = new JSONArray();
        for(String type : orderTypesToStrings(possibilities)){
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
