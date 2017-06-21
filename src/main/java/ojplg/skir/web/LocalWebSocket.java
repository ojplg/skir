package ojplg.skir.web;

import ojplg.skir.play.Channels;
import ojplg.skir.play.Skir;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.state.GameId;
import ojplg.skir.state.event.ClientConnectedEvent;
import ojplg.skir.state.event.GameJoinedEvent;
import ojplg.skir.state.event.GameEvent;
import ojplg.skir.state.event.PlayerChangedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.fibers.Fiber;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/sockets/")
public class LocalWebSocket /* implements WebSocket.OnTextMessage */ {

    private static final Logger _log = LogManager.getLogger(LocalWebSocket.class);

    private volatile int _counter=0;

    private final Channels _channels;
    private String _clientKey;
    private Fiber _fiber;

    private Session _session;
    private Adjutant _currentAdjutant;

    public LocalWebSocket(){
        _log.info("instantiated with no argument constructor");
        _channels = WebSocketInitializer.Channels;

        _fiber = Skir.createThreadFiber("WebSocketFiber-" + _counter );
        _counter++;

        _channels.MapChangedEventChannel.subscribe(_fiber,
                mapChangedEvent -> sendJson(mapChangedEvent.toJson())
        );
        _channels.PlayerChangedEventChannel.subscribe(_fiber,
                playerChangedEvent -> handlePlayerChangedEvent(playerChangedEvent)
        );
        _channels.AdjutantChannel.subscribe(_fiber,
                adjutant -> handleNewAdjutant(adjutant)
        );
        _channels.GameJoinedEventChannel.subscribe(_fiber,
                gameJoinedEvent -> handleGameJoinedEvent(gameJoinedEvent)
        );
        _channels.GameEventChannel.subscribe(_fiber,
                gameEvent -> handleOrderEvent(gameEvent));
        _log.info("Channel subscriptions made");

        _fiber.start();
    }

    @OnOpen
    public void onSessionOpened(Session session){
        _log.info("OPENED A SESSION " + session);
        _session = session;
    }

    @OnMessage
    public void onMessageReceived(String message, Session session){
        _log.info("Received a message " + message + " from " + session);

        JSONParser parser = new JSONParser();
        try {
            JSONObject jObject = (JSONObject) parser.parse(message);
            String messageType = (String) jObject.get("messageType");
            _log.info("Message type was " + messageType);
            if( "Order".equals(messageType)){
                try {
                    handleOrder(jObject);
                } catch (Exception ex){
                    _log.error("Could not handle order ", ex);
                }
            } else if ("ClientJoined".equals(messageType)){
                _log.info("Client Joined " + message);
                _clientKey = (String) jObject.get("uniqueKey");
                String displayName = (String) jObject.get("displayName");
                String address = (String) jObject.get("address");
                String demoString = (String) jObject.get("demo");
                long gameIdInt = (long) jObject.get("gameId");
                GameId gameId = GameId.fromLong(gameIdInt);
                _log.info("Demo string was set to " + demoString);
                boolean demo = Boolean.parseBoolean(demoString);
                _channels.ClientConnectedEventChannel.publish(
                        new ClientConnectedEvent(_clientKey, displayName, address, demo, gameId));
            } else if ("StartGame".equals(messageType)){
                _channels.StartGameChannel.publish("Start");
            }
        } catch (ParseException pe){
            _log.error("Could not parse json from client " + message, pe);
        }
    }

    @OnClose
    public void onWebSocketClose(CloseReason closeReason){
        _log.info("Web socket closed " + closeReason);
        _fiber.dispose();
        _log.info("Fiber disposed");
    }

    @OnError
    public void onWebSocketError(Throwable throwable){
        _log.error("Web socket error", throwable);
    }

    private void handlePlayerChangedEvent(PlayerChangedEvent playerChangedEvent){
        _log.debug("player event " + playerChangedEvent);
        JSONObject jObject;
        if (_clientKey != null && _clientKey.equals(playerChangedEvent.getClientKey())){
            jObject = playerChangedEvent.fullDetailsJson();
        } else {
            jObject = playerChangedEvent.toJson();
        }
        sendJson(jObject);
    }

    private void handleOrderEvent(GameEvent gameEvent){
        JSONObject jObject = gameEvent.toJson();
        _log.debug("Event: " + jObject.get("simple_text"));
        sendJson(jObject);
    }

    private void handleGameJoinedEvent(GameJoinedEvent gameJoinedEvent){
        _log.info("game joined event " + gameJoinedEvent);
        JSONObject jObject = gameJoinedEvent.toJson();
        sendJson(jObject);
    }

    private void handleOrder(JSONObject orderJson){
        _log.debug("Order " + orderJson);
        OrderJsonParser orderJsonParser = new OrderJsonParser(_currentAdjutant);
        Order order = orderJsonParser.parseOrder(orderJson);
        _channels.OrderEnteredChannel.publish(order);
    }

    private void handleNewAdjutant(Adjutant adjutant){
        _log.debug("adjutant " + adjutant);
        _currentAdjutant = adjutant;
        JSONObject jObject = adjutant.toPossibleOrdersJson();
        sendJson(jObject);
    }

    private void sendJson(JSONObject jObject){
        try {
            RemoteEndpoint.Basic endpoint = _session.getBasicRemote();
            String msg = jObject.toJSONString();
            _log.debug("Sending message " + msg);
            endpoint.sendText(msg);
        } catch (IOException io){
            _log.error("Could not send message ", io);
        }
    }
}
