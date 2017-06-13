package ojplg.skir.web;

import ojplg.skir.play.Channels;
import ojplg.skir.play.Constants;
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
import java.util.Map;

public class JoinGameHandler extends AbstractHandler {

    private static final Logger _log = LogManager.getLogger(JoinGameHandler.class);

    private final Channels _channels;

    public JoinGameHandler(Channels channels){
        _channels = channels;
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws IOException, ServletException {
        _log.info("Handling a request with context path: '" + request.getContextPath() +
                "', request URL: '" + httpServletRequest.getRequestURL() +
                "', path info '" + httpServletRequest.getPathInfo() + "'");



        String remoteAddress = request.getRemoteAddr();
        String switchKey = request.getParameter("switch-key");

        if( "start-game".equals(switchKey)) {
            String name = request.getParameter("name-input");
            String[] ais = request.getParameterValues("ai");

            _log.info("Start game for " + name);

//            Map<String, String[]> parameterMap = request.getParameterMap();
//            _log.info("Parameter map is " + parameterMap);

            _channels.AiNamesChannel.publish(ais);

            httpServletResponse.setContentType("text/html");
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);

            doVelocityTemplating(name, remoteAddress, httpServletResponse.getWriter());

            request.setHandled(true);
        } else if ( request.getPathInfo().equals("/") ) {
            _log.info("Generic index page request");
            httpServletResponse.setContentType("text/html");
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);

            doVelocityTemplating(httpServletResponse.getWriter());
            request.setHandled(true);
        }
    }

    private void doVelocityTemplating(String name, String address, Writer writer){
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

    private void doVelocityTemplating(Writer writer){
        Velocity.init();

        VelocityContext vc = new VelocityContext();
        vc.put("ai_names", Constants.AI_NAMES);

        InputStream in = this.getClass().getResourceAsStream("/template/index.vtl");

        Velocity.evaluate(vc, writer , "", new InputStreamReader(in));

    }

}
