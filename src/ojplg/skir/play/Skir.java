package ojplg.skir.play;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.core.RunnableExecutorImpl;
import org.jetlang.fibers.ThreadFiber;
import ojplg.skir.web.UseJetty;

import java.util.concurrent.CountDownLatch;

public class Skir {

    private static final Logger _log = LogManager.getLogger(Skir.class);

    private GameRunner _gameRunner;
    private UseJetty _jettyServer;

    private final CountDownLatch _latch = new CountDownLatch(1);

    public static void main(String[] args) {
        _log.info("Starting");

        final Skir skir = new Skir();
        final Channels channels = new Channels();

        ThreadFiber webFiber = new ThreadFiber(new RunnableExecutorImpl(), "WebFiber", true);
        skir._jettyServer = new UseJetty(8080, channels, webFiber);

        Thread webThread = new Thread(new Runnable(){
            @Override
            public void run(){
                skir.runWebServer();
            }
        },"WebThread");

        webThread.start();
        webFiber.start();

        try {
            skir._latch.await();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }

        ThreadFiber gameRunnerFiber = new ThreadFiber(new RunnableExecutorImpl(), "GameRunnerFiber", true);
        skir._gameRunner = new GameRunner(channels, gameRunnerFiber);

        gameRunnerFiber.start();
    }

    private void runWebServer(){
        try {
            _jettyServer.StartJettyServer(_latch);
        } catch (Exception e){
            _log.error("Could not start jetty", e);
        }
    }

}
