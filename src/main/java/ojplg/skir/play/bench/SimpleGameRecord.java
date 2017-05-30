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

    /**
     * Players get ten points for a win.
     *
     * Players get a bonus for late elimination:
     *  0 points for first eliminated
     *  1 point for second
     *  2 points for third
     *  3 points for fourth
     *  4 points for fifth
     *
     * In the event of a tie, players are each awarded 8 points
     * minus the number of surviving players.
     */
    public GameScores scoreGame(boolean includeLateEliminationBonus){
        Map<Object, Integer> participants = new HashMap<>();
        Map<Object, Integer> scores = new HashMap();
        _playerTypes.forEach( o -> {
            scores.put(o, 0);
            participants.computeIfPresent(o, (x,y) -> { return y + 1; });
            participants.putIfAbsent(o, 1);
        });
        if( includeLateEliminationBonus) {
            int lateEliminationBonus = 0;
            for (PlayerTurn pt : _playerEliminations) {
                int score = scores.get(pt._playerIdentifier);
                scores.put(pt._playerIdentifier, score + lateEliminationBonus);
                lateEliminationBonus++;
            }
        }
        if( _gameDrawn ){
            int drawBonus = 8 - _drawers.size();
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
        buf.append("Participants: ");
        _playerTypes.forEach( p ->
                {
                    buf.append(p);
                    buf.append(",");
                });
        buf.append(". Eliminations: ");
        _playerEliminations.forEach( pt ->
                {
                    buf.append(pt.getPlayerType());
                    buf.append(",");
                    buf.append(pt.getTurnNumber());
                    buf.append(";");
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
