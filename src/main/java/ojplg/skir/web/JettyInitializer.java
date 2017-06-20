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
import ojplg.skir.play.Channels;

import javax.websocket.server.ServerContainer;

public class JettyInitializer {

    private static final Logger _log = LogManager.getLogger(JettyInitializer.class);

    private final int _httpPort;
    private final Channels _channels;
    private Server _server;
    private final WebRunner _webRunner;

    public JettyInitializer(int httpPort, Channels channels, WebRunner webRunner){
        _httpPort = httpPort;
        _channels = channels;
        WebSocketInitializer.Channels = _channels;
        _webRunner = webRunner;
        _log.info("Web Socket Channels initialized");
    }

    public void startJettyServer() throws Exception {

        _log.info("Initializing web server");
        _server = new Server();

        ServerConnector httpConnector = new ServerConnector(_server);
        httpConnector.setPort(_httpPort);
        _server.addConnector(httpConnector);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{});
        resourceHandler.setResourceBase("target/classes");

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        ContextHandler contextHandler = new ContextHandler();
        contextHandler.setContextPath("/");
        contextHandler.setHandler( new SkirWebHandler(_channels, _webRunner));

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {  contextHandler, resourceHandler, context });
        _server.setHandler(handlers);

        ServerContainer wsContainer = WebSocketServerContainerInitializer.configureContext(context);
        wsContainer.addEndpoint(LocalWebSocket.class);

        _log.info("Starting web server");
        _server.start();
        _log.info("Started");
    }

}
