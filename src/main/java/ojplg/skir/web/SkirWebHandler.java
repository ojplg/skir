package ojplg.skir.web;

import ojplg.skir.play.Channels;
import ojplg.skir.state.Constants;
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

public class SkirWebHandler extends AbstractHandler {

    private static final Logger _log = LogManager.getLogger(SkirWebHandler.class);

    private final Channels _channels;

    public SkirWebHandler(Channels channels){
        _channels = channels;
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws IOException, ServletException {
        _log.info("'Handling a request to URL: '" + httpServletRequest.getRequestURL() +
                "' from '" + request.getRemoteAddr() + "'");

        if( request.getPathInfo().equals("/")) {
            String switchKey = request.getParameter("switch-key");
            if ("start-game".equals(switchKey)) {
                String remoteAddress = request.getRemoteAddr();
                String name = request.getParameter("name-input");
                _log.info("Starting game for " + name);
                String[] ais = request.getParameterValues("ai");
                _channels.AiNamesChannel.publish(ais);

                renderGamePage(name, remoteAddress, httpServletResponse.getWriter());

            } else if( "chooser".equals(request.getParameter("switch-key"))){
                renderChooserPage(httpServletResponse.getWriter());
            } else if( "new-game".equals(request.getParameter("switch-key"))){
                String remoteAddress = request.getRemoteAddr();
                renderGamePage("?", remoteAddress, httpServletResponse.getWriter());
            } else {
                renderIndexPage(httpServletResponse.getWriter());
            }
            httpServletResponse.setContentType("text/html");
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);

            request.setHandled(true);
        }
    }

    private void renderChooserPage(Writer writer){
        _log.info("Rendering chooser page");
        VelocityContext vc = new VelocityContext();
        renderVelocityTemplate("/template/choose.vtl", vc, writer);
    }

    private void renderGamePage(String name, String address, Writer writer){
        VelocityContext vc = new VelocityContext();
        vc.put("name", name);
        vc.put("address", address);
        vc.put("colors", GuiColor.ALL_COLORS);

        String webSocketProtocol = System.getenv("WEB_SOCKET_PROTOCOL");
        if ( webSocketProtocol == null){
            webSocketProtocol = "ws";
        }
        vc.put("web_socket_protocol", webSocketProtocol);

        renderVelocityTemplate("/template/game.vtl", vc, writer);
    }

    private void renderIndexPage(Writer writer){
        VelocityContext vc = new VelocityContext();
        vc.put("ai_names", Constants.AI_NAMES);

        renderVelocityTemplate("/template/index.vtl", vc, writer);
    }

    private void renderVelocityTemplate(String templatePath, VelocityContext vc, Writer writer){
        Velocity.init();

        InputStream in = this.getClass().getResourceAsStream(templatePath);

        Velocity.evaluate(vc, writer , "", new InputStreamReader(in));
    }

}
