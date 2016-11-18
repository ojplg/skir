package web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;
import org.jetlang.fibers.Fiber;
import play.Channels;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CountDownLatch;

public class UseJetty {

    private static final Logger _log = LogManager.getLogger(UseJetty.class);

    private final int _httpPort;
    private final Channels _channels;
    private final Fiber _webFiber;
    private Server _server;

    public UseJetty(int httpPort, Channels channels, Fiber webFiber){
        _httpPort = httpPort;
        _channels = channels;
        _webFiber = webFiber;
    }

    public void StartJettyServer(CountDownLatch latch) throws Exception {

        _server = new Server();

        SelectChannelConnector httpConnector = new SelectChannelConnector();
        httpConnector.setPort(_httpPort);
        _server.addConnector(httpConnector);

        WebSocketHandler socketHandler = new WebSocketHandler() {
            @Override
            public WebSocket doWebSocketConnect(HttpServletRequest httpServletRequest, String key) {
                _log.info("doWebSocket called " + key);
                String remoteAddress = httpServletRequest.getRemoteAddr();
                LocalWebSocket webSocket = new LocalWebSocket(_channels, _webFiber, remoteAddress, key);
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
