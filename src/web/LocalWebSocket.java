package web;

import map.Country;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.WebSocket;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import play.Channels;
import play.orders.Adjutant;
import play.orders.Attack;
import play.orders.AttackUntilVictoryOrDeath;
import play.orders.ClaimArmies;
import play.orders.ConstrainedOrderType;
import play.orders.DrawCard;
import play.orders.EndAttacks;
import play.orders.Fortify;
import play.orders.OccupationConstraints;
import play.orders.Occupy;
import play.orders.Order;
import play.orders.OrderConstraints;
import play.orders.OrderType;
import play.orders.PlaceArmy;
import state.event.ClientConnectedEvent;
import state.event.MapChangedEvent;
import state.event.PlayerChangedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                handleOrder(jObject);
            }
        } catch (ParseException pe){
            _log.error("Could not parse json from client " + s, pe);
        }
    }

    private void handleOrder(JSONObject orderJson){
        String orderType = (String) orderJson.get("orderType");
        if( "PlaceArmy".equals(orderType)){
            String countryName = (String) orderJson.get("country");
            Country country = new Country(countryName);
            PlaceArmy placeArmy = new PlaceArmy(_currentAdjutant, country);
            _channels.OrderEnteredChannel.publish(placeArmy);
        } else if ("Attack".equals(orderType) || "AttackUntilVictoryOrDeath".equals(orderType)){
            String attacker = (String) orderJson.get("from");
            String defender = (String) orderJson.get("to");
            Order attack;
            if("Attack".equals(orderType)) {
                attack = new Attack(_currentAdjutant, new Country(attacker), new Country(defender));
            } else {
                attack = new AttackUntilVictoryOrDeath(_currentAdjutant, new Country(attacker), new Country(defender));
            }
            _channels.OrderEnteredChannel.publish(attack);
        } else if ("DoOccupation".equals(orderType)){
            OccupationConstraints constraints = _currentAdjutant.getOccupationConstraints();
            _log.info("occupation constraints " + constraints);
            String occupationForce = (String) orderJson.get("occupationForce"); //successfulAttack.getAttackersDiceCount();
            int armiesToMove = Integer.parseInt(occupationForce);
            Occupy occupy = new Occupy(_currentAdjutant, constraints.attacker(),
                    constraints.conquered(), armiesToMove);
            _channels.OrderEnteredChannel.publish(occupy);
        } else if ("EndAttacks".equals(orderType) ) {
            EndAttacks endAttacks = new EndAttacks(_currentAdjutant);
            _channels.OrderEnteredChannel.publish(endAttacks);
        } else if ("DrawCard".equals(orderType)){
            DrawCard drawCard = new DrawCard(_currentAdjutant);
            _channels.OrderEnteredChannel.publish(drawCard);
        } else if ("ClaimArmies".equals(orderType) ){
            ClaimArmies claimArmies = new ClaimArmies(_currentAdjutant);
            _channels.OrderEnteredChannel.publish(claimArmies);
        } else if ("Fortify".equals(orderType)) {
            String from = (String) orderJson.get("from");
            String to = (String) orderJson.get("to");
            int numberArmies = (Integer) orderJson.get("number_armies");
            Fortify fortify = new Fortify(_currentAdjutant, new Country(from), new Country(to), numberArmies);
            _channels.OrderEnteredChannel.publish(fortify);
        } else {
            _log.error("Cannot handle " + orderJson);
        }
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
