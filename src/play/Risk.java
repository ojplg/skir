package play;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.core.RunnableExecutorImpl;
import org.jetlang.fibers.ThreadFiber;
import web.UseJetty;

import java.util.concurrent.CountDownLatch;

public class Risk {

    private static final Logger _log = LogManager.getLogger(Risk.class);

    private GameRunner _gameRunner;
    private UseJetty _jettyServer;

    private final CountDownLatch _latch = new CountDownLatch(1);

    public static void main(String[] args) {
        _log.info("Starting");

        final Risk risk = new Risk();
        final Channels channels = new Channels();

        ThreadFiber webFiber = new ThreadFiber(new RunnableExecutorImpl(), "WebFiber", true);
        risk._jettyServer = new UseJetty(8080, channels, webFiber);

        Thread webThread = new Thread(new Runnable(){
            @Override
            public void run(){
                risk.runWebServer();
            }
        },"WebThread");

        webThread.start();
        webFiber.start();

        try {
            risk._latch.await();
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }

        ThreadFiber gameRunnerFiber = new ThreadFiber(new RunnableExecutorImpl(), "GameRunnerFiber", true);
        risk._gameRunner = new GameRunner(channels, gameRunnerFiber);

        gameRunnerFiber.start();

        risk._gameRunner.startGame();
    }

    private void runWebServer(){
        try {
            _jettyServer.StartJettyServer(_latch);
        } catch (Exception e){
            _log.error("Could not start jetty", e);
        }
    }

}
