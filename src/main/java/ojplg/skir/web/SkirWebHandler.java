package ojplg.skir.web;

import ojplg.skir.ai.AiFactory;
import ojplg.skir.play.NewGameRequest;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SkirWebHandler extends AbstractHandler {

    private static final Logger _log = LogManager.getLogger(SkirWebHandler.class);

    private final WebRunner _webRunner;

    SkirWebHandler(WebRunner webRunner){
        _webRunner = webRunner;
    }

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse) throws IOException, ServletException {
        _log.info("'Handling a request to URL: '" + httpServletRequest.getRequestURL() +
                "' from '" + request.getRemoteAddr() + "'"
                + " with path into " + request.getPathInfo()
                + " with context path " + request.getContextPath());

        switch(request.getPathInfo()){
            case "/chooser":
                renderChooserPage(request, httpServletResponse, "");
                break;
            case "/new-game":
                handleNewGameRequest(request, httpServletResponse);
                return;
            case "/join-game":
            case "/view-game":
                // TODO: should distinguish between join and view attempts
                handleJoinViewRequests(request, httpServletResponse);
                break;
            default:
                // TODO: this should lead to an error screen
                _log.warn("Request makes no sense: " + httpServletRequest.getRequestURL());
        }

        httpServletResponse.setContentType("text/html");
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        request.setHandled(true);
    }

    private void handleJoinViewRequests(Request request, HttpServletResponse httpServletResponse)
    throws IOException {
        _log.info("Join or view game being handled");
        String userName = request.getParameter("user-name");
        String gameIdString = request.getParameter("game");
        GameId gameId = GameId.fromString(gameIdString);
        if( isGameActive(gameId)){
            boolean joinAttempt = "/join-game".equals(request.getPathInfo());
            boolean demo = "true".equals(request.getParameter("demo"));
            String remoteAddress = request.getRemoteAddr();
            renderGamePage(gameId, userName, remoteAddress, demo, joinAttempt, httpServletResponse.getWriter());
        } else {
            renderChooserPage(request, httpServletResponse, "Unknown game");
        }
    }

    private void handleNewGameRequest(Request request, HttpServletResponse response)
    throws IOException {
        _log.info("New game being handled");
        String userName = request.getParameter("user-name");
        String remoteAddress = request.getRemoteAddr();
        List<String> ais;
        if ( request.getParameterMap().containsKey("ai")) {
             ais = Arrays.asList(request.getParameterValues("ai"));
            boolean demoFlag = Boolean.parseBoolean(request.getParameter("demo"));
            NewGameRequest gameRequest = demoFlag ? NewGameRequest.webDemo(userName, remoteAddress, ais) :
                    NewGameRequest.webPlay(userName, remoteAddress, ais);
            GameId gameId = _webRunner.newGame(gameRequest);

            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
            response.setHeader("Location", "/app/join-game?user-name=" + userName
                    + "&demo=" + demoFlag + "&game=" + gameId.getId());
        } else {
            renderChooserPage(request, response, "Need to select at least one AI");
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
        }
        request.setHandled(true);
    }

    private void renderChooserPage(Request request, HttpServletResponse response, String error)
    throws IOException {
        String userName = request.getParameter("user-name");
        _log.info("Rendering chooser page");
        VelocityContext vc = new VelocityContext();
        vc.put("user_name", userName);
        vc.put("ai_names", AiFactory.allPlayerNames());
        Map<GameId, GameMenuEntry> gameRequests = _webRunner.getGameEntries();
        List<GameId> ids = new ArrayList<>(gameRequests.keySet());
        Collections.sort(ids);
        vc.put("game_ids", ids);
        vc.put("game_requests", gameRequests);
        vc.put("error", error);
        renderVelocityTemplate("/template/choose.vtl", vc, response.getWriter());
        _log.info("rendered chooser page");
    }

    private boolean isGameActive(GameId gameId){
        return _webRunner.getGameEntries().containsKey(gameId);
    }

    private void renderGamePage(GameId gameId, String name, String address, boolean demoFlag, boolean joinAttempt, Writer writer){
        VelocityContext vc = new VelocityContext();
        vc.put("name", name);
        vc.put("address", address);
        vc.put("colors", GuiColor.ALL_COLORS);
        vc.put("game_id", gameId.getId());
        vc.put("demo", demoFlag);
        vc.put("join_attempt", joinAttempt);

        String webSocketProtocol = System.getenv("WEB_SOCKET_PROTOCOL");
        if ( webSocketProtocol == null){
            webSocketProtocol = "ws";
        }
        vc.put("web_socket_protocol", webSocketProtocol);

        renderVelocityTemplate("/template/game.vtl", vc, writer);
    }

    private void renderVelocityTemplate(String templatePath, VelocityContext vc, Writer writer){
        Velocity.init();

        InputStream in = this.getClass().getResourceAsStream(templatePath);

        Velocity.evaluate(vc, writer , "", new InputStreamReader(in));
    }

}
