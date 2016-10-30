package web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;
import play.GameRunner;
import state.Game;
import state.OrderBroadcasterLocator;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CountDownLatch;

public class UseJetty {

    private static final Logger _log = LogManager.getLogger(UseJetty.class);

    private final int _httpPort;
    private final Game _game;
    private final MessageHandler _messageHandler;
    private Server _server;

    public UseJetty(int httpPort, Game game, GameRunner gameRunner){
        _httpPort = httpPort;
        _game = game;
        _messageHandler = new MessageHandler(gameRunner);
    }

    public void StartJettyServer(CountDownLatch latch) throws Exception {

        _server = new Server();

        SelectChannelConnector httpConnector = new SelectChannelConnector();
        httpConnector.setPort(_httpPort);
        _server.addConnector(httpConnector);

        WebSocketHandler socketHandler = new WebSocketHandler() {
            @Override
            public WebSocket doWebSocketConnect(HttpServletRequest httpServletRequest, String s) {
                System.out.println("doWebSocket called " + s);
                LocalWebSocket webSocket = new LocalWebSocket(_game, _messageHandler);
                _game.addMapEventListener(webSocket);
                OrderBroadcasterLocator.BROADCASTER.addListener(webSocket);
                return webSocket;
            }
        };

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "html/index.html" });
        resource_handler.setResourceBase("../out/production/risk/");

        socketHandler.setHandler(resource_handler);
        _server.setHandler(socketHandler);

        _log.info("Started up Jetty Web Server");
        _server.start();
        _log.info("Counted down latch");
        latch.countDown();
        _server.join();
        _log.info("Joined");
    }

    public void stop(){
        try {
            _server.stop();
        } catch (Exception ex){
            _log.error("Could not stop jetty", ex);
        }
    }

}
