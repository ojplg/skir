package ojplg.skir.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
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
        Server _server = new Server(_httpPort);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{ "html/index.html"});
        resourceHandler.setResourceBase("target/classes");

        MimeTypes mimeTypes = new MimeTypes();
        mimeTypes.addMimeMapping("js","application/javascript");
        resourceHandler.setMimeTypes(mimeTypes);

        ServletContextHandler webSocketContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        webSocketContextHandler.setContextPath("/skirwebsocket");

        ContextHandler appHandler = new ContextHandler();
        appHandler.setContextPath("/skir/app");
        appHandler.setHandler(new SkirWebHandler(_webRunner));

        ContextHandler skirResources = new ContextHandler();
        skirResources.setContextPath("/skir");
        skirResources.setHandler(resourceHandler);

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { appHandler, skirResources, webSocketContextHandler });
        _server.setHandler(handlers);

        ServerContainer wsContainer = WebSocketServerContainerInitializer.configureContext(webSocketContextHandler);
        wsContainer.addEndpoint(LocalWebSocket.class);

        _log.info("Starting web server");
        _server.start();
        _log.info("Started");
    }

}
