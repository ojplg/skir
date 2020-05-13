package ojplg.skir.play;

import ojplg.skir.state.GameId;
import ojplg.skir.state.GameState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GameSaver {

    private static final Logger _log = LogManager.getLogger(GameSaver.class);
    private static String DIRECTORY = "/var/skir/";

    public static String fileName(GameId gameId){
        int gameNumber = gameId.getId();
        return DIRECTORY + "game_" + gameNumber;
    }

    public static void saveGameState(GameState game){
        String fileName = fileName(game.getGameId());
        try {
            FileOutputStream fileOut =
                    new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(game);
            out.close();
            fileOut.close();

        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static GameState loadGameState(GameId gameId){
        String fileName = fileName(gameId);
        try {
            FileInputStream fileInputStream =
                    new FileInputStream(fileName);
            ObjectInputStream inputStream =
                    new ObjectInputStream(fileInputStream);
            GameState game = (GameState) inputStream.readObject();

            inputStream.close();
            fileInputStream.close();
            return game;
        } catch (IOException ioe){
            throw new RuntimeException(ioe);
        } catch (ClassNotFoundException cnfe) {
            throw new RuntimeException(cnfe);
        }
    }

    public static List<GameId> availableGames(){
        List<GameId> games = new ArrayList<>();
        Path dir = Paths.get(DIRECTORY);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file: stream) {
                String fileName = file.getName(2).toString();
                _log.info(fileName);
                if( fileName.startsWith("game_")){
                    String number = fileName.substring(5);
                    GameId gameId = GameId.fromString(number);
                    games.add(gameId);
                }

            }
        } catch (IOException | DirectoryIteratorException ex) {
            // IOException can never be thrown by the iteration.
            // In this snippet, it can only be thrown by newDirectoryStream.
            _log.error("Could not load old games", ex);
        }
        return games;
    }

}
