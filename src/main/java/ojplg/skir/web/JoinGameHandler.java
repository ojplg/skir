package ojplg.skir.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

public class JoinGameHandler extends AbstractHandler {

    private static final Logger _log = LogManager.getLogger(JoinGameHandler.class);

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws IOException, ServletException {
        _log.info("Handling a request " + request.getContextPath() + ", " + httpServletRequest.getRequestURL());

        String query = request.getQueryString();
        String remoteAddress = request.getRemoteAddr();
        String name = request.getParameter("name-input");

        if( name != null ) {
            _log.info("Got the queries " + query + " from " + remoteAddress + " has name " + name);

            httpServletResponse.setContentType("text/html");
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);

            DoVelocityTemplating(name, remoteAddress, httpServletResponse.getWriter());

            request.setHandled(true);
        }
    }

    private void DoVelocityTemplating(String name, String address, Writer writer){
        Velocity.init();

        VelocityContext vc = new VelocityContext();
        vc.put("name", name);
        vc.put("address", address);
        vc.put("colors", GuiColor.ALL_COLORS);
        
        String webSocketProtocol = System.getenv("WEB_SOCKET_PROTOCOL");
        if ( webSocketProtocol == null){
            webSocketProtocol = "ws";
        }
        vc.put("web_socket_protocol", webSocketProtocol);

        InputStream in = this.getClass().getResourceAsStream("/template/game.vtl");

        Velocity.evaluate(vc, writer , "", new InputStreamReader(in));
    }

}
