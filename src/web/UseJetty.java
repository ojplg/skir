package web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

import javax.servlet.http.HttpServletRequest;

public class UseJetty {

    private final int _httpPort;

    public UseJetty(int httpPort){
        _httpPort = httpPort;
    }

    public void StartJettyServer() throws Exception {

        Server server = new Server();

        SelectChannelConnector httpConnector = new SelectChannelConnector();
        httpConnector.setPort(_httpPort);
        server.addConnector(httpConnector);

        WebSocketHandler socketHandler = new WebSocketHandler() {
            @Override
            public WebSocket doWebSocketConnect(HttpServletRequest httpServletRequest, String s) {
                System.out.println("doWebSocket called " + s);
                return new LocalWebSocket();
            }
        };

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setWelcomeFiles(new String[]{ "index.html" });
        resource_handler.setResourceBase("/Users/ogugenheim/git_personal/risk/out/production/risk/html/");

        socketHandler.setHandler(resource_handler);
        server.setHandler(socketHandler);

        System.out.println("Started up Jetty Web Server");
        server.start();
        server.join();
        System.out.println("Joined");
    }

}
