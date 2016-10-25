package web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;
import state.Game;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CountDownLatch;

public class UseJetty {

    private final int _httpPort;
    private final Game _game;
    private Server _server;

    public UseJetty(int httpPort, Game game){
        _httpPort = httpPort;
        _game = game;
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
                LocalWebSocket webSocket = new LocalWebSocket(_game);
                _game.addMapEventListener(webSocket);
                return webSocket;
            }
        };

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "html/index.html" });
        resource_handler.setResourceBase("../out/production/risk/");

        socketHandler.setHandler(resource_handler);
        _server.setHandler(socketHandler);

        System.out.println("Started up Jetty Web Server");
        _server.start();
        System.out.println("Counted down latch");
        latch.countDown();
        _server.join();
        System.out.println("Joined");
    }

    public void stop(){
        try {
            _server.stop();
        } catch (Exception ex){
            System.out.println("error stopping jetty");
            ex.printStackTrace();
        }
    }

}
