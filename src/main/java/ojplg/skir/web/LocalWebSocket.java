package ojplg.skir.web;

import ojplg.skir.play.Channels;
import ojplg.skir.play.Skir;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.state.GameId;
import ojplg.skir.state.event.ClientConnectedEvent;
import ojplg.skir.state.event.GameJoinedEvent;
import ojplg.skir.state.event.GameEvent;
import ojplg.skir.state.event.GameSpecifiable;
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
public class LocalWebSocket /* implements WebSocket.OnTextMessage */ implements GameSpecifiable {

    private static final Logger _log = LogManager.getLogger(LocalWebSocket.class);

    private static volatile int _counter=0;

    private final Channels _channels;
    private String _clientKey;
    private Fiber _fiber;

    private Session _session;
    private Adjutant _currentAdjutant;
    private GameId _gameId;

    public LocalWebSocket(){
        _log.info("Constructing");
        _channels = WebSocketInitializer.Channels;
        _fiber = Skir.createThreadFiber("WebSocketFiber-" + _counter );
        _counter++;
        _fiber.start();
        _log.info("Constructed");
    }

    private void doSubscriptions(){
        _channels.subscribeToMapChangedEvent(this, _fiber,
                mapChangedEvent -> sendJson(mapChangedEvent.toJson())
        );
        _channels.subscribeToPlayerChangedEvent(this, _fiber, this::handlePlayerChangedEvent);
        _channels.subscribeToAdjutant(this, _fiber, this::handleNewAdjutant);
        _channels.subscribeToGameJoinedEvent(this, _fiber, this::handleGameJoinedEvent);
        _channels.subscribeToGameEvent(this, _fiber, this::handleOrderEvent);
        _log.info("Channel subscriptions made");
    }

    @OnOpen
    public void onSessionOpened(Session session){
        _log.info("OPENED A SESSION " + session);
        _session = session;
    }

    @OnMessage
    public void onMessageReceived(String message, Session session){
        _log.info("Received a message " + message + " from " + session.getId());

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
                long gameIdInt = (long) jObject.get("gameId");
                _gameId = GameId.fromLong(gameIdInt);
                doSubscriptions();
                boolean demo = (boolean) jObject.get("demo");
                boolean joinAttempt = (boolean) jObject.get("joinAttempt");
                ClientConnectedEvent cce = new ClientConnectedEvent(_clientKey, displayName, address, demo, _gameId, joinAttempt);
                _log.info("Publishing client connected event " + cce);
                _channels.publishClientConnectedEvent(cce);
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

    public GameId getGameId(){ return _gameId; }

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
        _channels.publishOrder(order);
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
