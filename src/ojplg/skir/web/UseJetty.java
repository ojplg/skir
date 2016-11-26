package ojplg.skir.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.jetlang.fibers.Fiber;
import ojplg.skir.play.Channels;

import javax.websocket.server.ServerContainer;
import java.util.concurrent.CountDownLatch;

public class UseJetty  {

    private static final Logger _log = LogManager.getLogger(UseJetty.class);

    private final int _httpPort;
    private final Channels _channels;
    private final Fiber _webFiber;
    private Server _server;

    public UseJetty(int httpPort, Channels channels, Fiber webFiber){
        _httpPort = httpPort;
        _channels = channels;
        _webFiber = webFiber;
        WebSocketInitializer.Channels = _channels;
        _log.info("Web Socket Channels initialized");
    }

    public void StartJettyServer(CountDownLatch latch) throws Exception {

        _server = new Server();

        ServerConnector httpConnector = new ServerConnector(_server);
        httpConnector.setPort(_httpPort);
        _server.addConnector(httpConnector);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"html/index.html"});
        resourceHandler.setResourceBase("../out/production/skir/");

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ContextHandler contextHandler = new ContextHandler();
        contextHandler.setContextPath("/");
        contextHandler.setHandler( new JoinGameHandler(_channels));

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { contextHandler, resourceHandler, context });
        _server.setHandler(handlers);

        ServerContainer wsContainer = WebSocketServerContainerInitializer.configureContext(context);
        wsContainer.addEndpoint(LocalWebSocket.class);

        _log.info("Started up Jetty Web Server");
        _server.start();
        _log.info("Counted down latch");
        latch.countDown();
        _server.join();
        _log.info("Joined");
    }

}
