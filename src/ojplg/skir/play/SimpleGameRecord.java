package ojplg.skir.play;

import java.util.ArrayList;
import java.util.List;

public class SimpleGameRecord {

    private List<String> _playerTypes = new ArrayList<>();
    private List<PlayerTurn> _playerEliminations = new ArrayList<>();

    private int _gameLength;
    private boolean _gameDrawn;
    private String _winner;

    public void playerJoined(String playerType){
        _playerTypes.add(playerType);
    }

    public void playerEliminated(String playerType, int turnNumber){
        _playerEliminations.add(new PlayerTurn(playerType, turnNumber));
    }

    public void draw(int gameLength){
        _gameLength = gameLength;
        _gameDrawn = true;
    }

    public void win(String playerType, int gameLength){
        _gameLength = gameLength;
        _gameDrawn = false;
        _winner = playerType;
    }

    public String produceLogRecord(){
        StringBuilder buf = new StringBuilder();
        _playerTypes.forEach( p ->
                {
                    buf.append("J:");
                    buf.append(p);
                    buf.append(",");
                });
        _playerEliminations.forEach( pt ->
                {
                    buf.append("E:");
                    buf.append(pt.getPlayerType());
                    buf.append(",");
                    buf.append(pt.getTurnNumber());
                    buf.append(",");
                });
        if(_gameDrawn){
            buf.append("draw,");
        } else {
            buf.append("victory for " + _winner + ",");
        }
        buf.append(_gameLength);
        return buf.toString();
    }

    private static class PlayerTurn {
        private final String _playerType;
        private final int _turnNumber;

        public PlayerTurn(String playerType, int turnNumber){
            _playerType = playerType;
            _turnNumber = turnNumber;
        }

        public String getPlayerType() {
            return _playerType;
        }

        public int getTurnNumber() {
            return _turnNumber;
        }
    }

}
