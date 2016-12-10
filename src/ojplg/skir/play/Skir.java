package ojplg.skir.play;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.core.RunnableExecutorImpl;
import org.jetlang.fibers.ThreadFiber;
import ojplg.skir.web.UseJetty;

public class Skir {

    private static final Logger _log = LogManager.getLogger(Skir.class);

    private GameRunner _gameRunner;
    private UseJetty _jettyServer;

    public static void main(String[] args) {
        _log.info("Starting");

        final Skir skir = new Skir();
        final Channels channels = new Channels();

        skir._jettyServer = new UseJetty(8080, channels);

        Thread webThread = new Thread(new Runnable(){
            @Override
            public void run(){
                skir.runWebServer();
            }
        },"WebThread");

        webThread.start();

        ThreadFiber gameRunnerFiber = new ThreadFiber(new RunnableExecutorImpl(), "GameRunnerFiber", true);
        skir._gameRunner = new GameRunner(channels, gameRunnerFiber);

        gameRunnerFiber.start();
    }

    private void runWebServer(){
        try {
            _jettyServer.startJettyServer();
        } catch (Exception e){
            _log.error("Could not start jetty", e);
        }
    }

}
