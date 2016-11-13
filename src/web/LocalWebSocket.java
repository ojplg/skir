package web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import play.Channels;
import play.orders.Adjutant;
import play.orders.Order;
import play.orders.OrderConstraints;
import play.orders.OrderType;
import state.event.ClientConnectedEvent;
import state.event.MapChangedEvent;
import state.event.PlayerChangedEvent;

import java.io.IOException;

class LocalWebSocket implements WebSocket.OnTextMessage {

    private static final Logger _log = LogManager.getLogger(LocalWebSocket.class);

    private static volatile int _counter = 0;
    private String _id;
    private Connection _connection;
    private final Channels _channels;

    private Adjutant _currentAdjutant;

    LocalWebSocket(Channels channels, Fiber fiber){
        _counter++ ;
        _id = String.valueOf(_counter);

        _channels = channels;

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
            String messageType = (String) jObject.get("messageType");
            _log.info("Message type was " + messageType);
            if( "Order".equals(messageType)){
                try {
                    handleOrder(jObject);
                } catch (Exception ex){
                    _log.error("Could not handle order ", ex);
                }
            }
        } catch (ParseException pe){
            _log.error("Could not parse json from client " + s, pe);
        }
    }

    private void handleOrder(JSONObject orderJson){
        OrderJsonParser orderJsonParser = new OrderJsonParser(_currentAdjutant);
        Order order = orderJsonParser.parseOrder(orderJson);
        _channels.OrderEnteredChannel.publish(order);
    }

    @Override
    public void onOpen(Connection connection) {
        _log.info("onOpen called on LocalWebSocket");
        _connection = connection;
        _channels.ClientConnectedEventChannel.publish(new ClientConnectedEvent(_id));
    }

    @Override
    public void onClose(int i, String s) {
        _log.info("onClose called on LocalWebSocket " + s);
    }

    private void handleNewAdjutant(Adjutant adjutant){
        _currentAdjutant = adjutant;
        JSONObject jObject = new JSONObject();
        jObject.put("message_type","possible_order_types");
        jObject.put("color", adjutant.getActivePlayer().getColor());
        JSONObject orderTypes = new JSONObject();
        for(OrderType type : adjutant.allowableOrders()){
            OrderConstraints orderConstraints = adjutant.findConstraintsForOrderType(type);
            JSONObject constraintJson = orderConstraints.toJsonObject();
            orderTypes.put(type.toString(), constraintJson);
        }
        jObject.put("order_types", orderTypes);

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
