package ojplg.skir.play.bench;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleGameRecord {

    private List<Object> _playerTypes = new ArrayList<>();
    private List<PlayerTurn> _playerEliminations = new ArrayList<>();

    private int _gameLength;
    private boolean _gameDrawn;
    private Object _winner;
    private List<String> _drawers;

    public void playerJoined(Object playerIdentifier){
        _playerTypes.add(playerIdentifier);
    }

    public void playerEliminated(String playerType, int turnNumber){
        _playerEliminations.add(new PlayerTurn(playerType, turnNumber));
    }

    public void draw(List<String> drawers, int gameLength){
        _gameLength = gameLength;
        _gameDrawn = true;
        _drawers = drawers;
    }

    public void win(Object playerIdentifier, int gameLength){
        _gameLength = gameLength;
        _gameDrawn = false;
        _winner = playerIdentifier;
    }

    public GameScores scoreGame(){
        Map<Object, Integer> participants = new HashMap<>();
        Map<Object, Integer> scores = new HashMap();
        _playerTypes.forEach( o -> {
            scores.putIfAbsent(o, 0);
            participants.computeIfPresent(o, (x,y) -> { return y + 1; });
            participants.putIfAbsent(o, 1);
        });
        int lateEliminationBonus = 0;
        for(PlayerTurn pt : _playerEliminations){
            int score = scores.get(pt._playerIdentifier);
            scores.put(pt._playerIdentifier, score + lateEliminationBonus);
            lateEliminationBonus++;
        }
        if( _gameDrawn ){
            int drawBonus = 6 - _drawers.size();
            for(String drawer : _drawers){
                int score = scores.get(drawer);
                scores.put(drawer, score + drawBonus);
            }
        } else {
            int score = scores.get(_winner);
            scores.put(_winner, score + 10);
        }
        return new GameScores(participants, scores);
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
        private final Object _playerIdentifier;
        private final int _turnNumber;

        public PlayerTurn(Object playerIdentifier, int turnNumber){
            _playerIdentifier = playerIdentifier;
            _turnNumber = turnNumber;
        }

        public Object getPlayerType() {
            return _playerIdentifier;
        }

        public int getTurnNumber() {
            return _turnNumber;
        }
    }

}
