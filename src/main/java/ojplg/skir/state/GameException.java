package ojplg.skir.state;

public class GameException extends RuntimeException {

    private final GameId _gameId;

    public GameException(GameId gameId, String message){
        super(message);
        _gameId = gameId;
    }

    @Override
    public String getMessage(){
        return "Game: " + _gameId + ". " + super.getMessage();
    }

}
