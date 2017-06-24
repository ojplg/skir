package ojplg.skir.web;

import ojplg.skir.play.Channels;
import ojplg.skir.play.NewGameRequest;
import ojplg.skir.state.Constants;
import ojplg.skir.state.GameId;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SkirWebHandler extends AbstractHandler {

    private static final Logger _log = LogManager.getLogger(SkirWebHandler.class);

    private final Channels _channels;
    private final WebRunner _webRunner;

    public SkirWebHandler(Channels channels, WebRunner webRunner){
        _channels = channels;
        _webRunner = webRunner;
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws IOException, ServletException {
        _log.info("'Handling a request to URL: '" + httpServletRequest.getRequestURL() +
                "' from '" + request.getRemoteAddr() + "'");

        if( request.getPathInfo().equals("/")) {
            String switchKey = request.getParameter("switch-key");
            if( "chooser".equals(switchKey)){
                String userName = request.getParameter("user-name");
                renderChooserPage(userName, httpServletResponse.getWriter());
            } else if( "new-game".equals(switchKey)){
                String userName = request.getParameter("user-name");
                String remoteAddress = request.getRemoteAddr();
                String[] ais = request.getParameterValues("ai");
                boolean demoFlag = Boolean.parseBoolean(request.getParameter("demo"));
                NewGameRequest gameRequest = NewGameRequest.withDelay(userName, remoteAddress, ais);
                GameId gameId = _webRunner.newGame(gameRequest);
                renderGamePage(gameId, userName, remoteAddress, demoFlag, httpServletResponse.getWriter());
            } else if( "join-game".equals(switchKey)){
                String remoteAddress = request.getRemoteAddr();
                String gameIdString = request.getParameter("game");
                GameId gameId = GameId.fromString(gameIdString);
                String userName = request.getParameter("user-name");
                renderGamePage(gameId, userName, remoteAddress, false, httpServletResponse.getWriter());
            } else {
                renderIndexPage(httpServletResponse.getWriter());
            }
            httpServletResponse.setContentType("text/html");
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);

            request.setHandled(true);
        }
    }

    private void renderChooserPage(String userName, Writer writer){
        _log.info("Rendering chooser page");
        VelocityContext vc = new VelocityContext();
        vc.put("user_name", userName);
        vc.put("ai_names", Constants.AI_NAMES);
        Map<GameId, NewGameRequest> gameRequests = _webRunner.getGameRequests();
        List<GameId> ids = new ArrayList<>(gameRequests.keySet());
        Collections.sort(ids);
        vc.put("game_ids", ids);
        vc.put("game_requests", gameRequests);
        renderVelocityTemplate("/template/choose.vtl", vc, writer);
    }

    private void renderGamePage(GameId gameId, String name, String address, boolean demoFlag, Writer writer){
        VelocityContext vc = new VelocityContext();
        vc.put("name", name);
        vc.put("address", address);
        vc.put("colors", GuiColor.ALL_COLORS);
        vc.put("game_id", gameId.getId());
        vc.put("demo", demoFlag);

        String webSocketProtocol = System.getenv("WEB_SOCKET_PROTOCOL");
        if ( webSocketProtocol == null){
            webSocketProtocol = "ws";
        }
        vc.put("web_socket_protocol", webSocketProtocol);

        renderVelocityTemplate("/template/game.vtl", vc, writer);
    }

    private void renderIndexPage(Writer writer){
        VelocityContext vc = new VelocityContext();
        renderVelocityTemplate("/template/index.vtl", vc, writer);
    }

    private void renderVelocityTemplate(String templatePath, VelocityContext vc, Writer writer){
        Velocity.init();

        InputStream in = this.getClass().getResourceAsStream(templatePath);

        Velocity.evaluate(vc, writer , "", new InputStreamReader(in));
    }

}
