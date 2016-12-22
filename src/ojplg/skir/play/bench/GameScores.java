package ojplg.skir.play.bench;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameScores {

    private final Map<Object, Integer> _participations = new HashMap<>();
    private final Map<Object, Integer> _score = new HashMap<>();

    public GameScores(){}

    public GameScores(Map<Object,Integer> participations, Map<Object,Integer> score){
        _participations.putAll(participations);
        _score.putAll(score);
    }

    public GameScores accumulate(GameScores gameScores){
        for(Object key : gameScores.keySet()){
            int participationUpdate = gameScores.getParticipationCount(key);
            updateMapValue(key, participationUpdate, _participations);
            int scoreUpdate = gameScores.getScore(key);
            updateMapValue(key, scoreUpdate, _score);
        }
        return this;
    }

    private void updateMapValue(Object key, int adjustment, Map<Object,Integer> table){
        if( table.containsKey(key)){
            int old = table.get(key);
            table.put(key, old+adjustment);
        } else {
            table.put(key, adjustment);
        }
    }

    public int getParticipationCount(Object playerId){
        return _participations.get(playerId);
    }

    public int getScore(Object playerId) {
        return _score.get(playerId);
    }

    public Set<Object> keySet(){
        return _participations.keySet();
    }

    public String toString(){
        StringBuilder buf = new StringBuilder();

        for(Object key : keySet()){
            int participationCount = getParticipationCount(key);
            int totalScore = getScore(key);
            float averageScore = (float) totalScore/participationCount;

            buf.append(key);
            buf.append(": ");
            buf.append(participationCount);
            buf.append(", ");
            buf.append(totalScore);
            buf.append("  (");
            buf.append(averageScore);
            buf.append(")");
            buf.append("\n");
        }

        return buf.toString();
    }
}
