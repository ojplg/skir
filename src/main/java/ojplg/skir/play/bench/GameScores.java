package ojplg.skir.play.bench;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameScores {

    private final Map<String, Integer> _participations = new HashMap<>();
    private final Map<String, Integer> _score = new HashMap<>();

    public GameScores(){}

    public GameScores(Map<String,Integer> participations, Map<String,Integer> score){
        _participations.putAll(participations);
        _score.putAll(score);
    }

    public GameScores accumulate(GameScores gameScores){
        for(String key : gameScores.keySet()){
            int participationUpdate = gameScores.getParticipationCount(key);
            updateMapValue(key, participationUpdate, _participations);
            int scoreUpdate = gameScores.getScore(key);
            updateMapValue(key, scoreUpdate, _score);
        }
        return this;
    }

    private void updateMapValue(String key, int adjustment, Map<String,Integer> table){
        if( table.containsKey(key)){
            int old = table.get(key);
            table.put(key, old+adjustment);
        } else {
            table.put(key, adjustment);
        }
    }

    public int getParticipationCount(String playerId){
        return _participations.get(playerId);
    }

    public int getScore(String playerId) {
        return _score.get(playerId);
    }

    public Set<String> keySet(){
        return _participations.keySet();
    }

    public String toString(){
        StringBuilder buf = new StringBuilder();

        buf.append("Name: games played, total, average\n");

        List<String> names = new ArrayList<>();
        names.addAll(keySet());
        Collections.sort(names);

        for(String key : names){
            int participationCount = getParticipationCount(key);
            int totalScore = getScore(key);
            float averageScore = (float) totalScore/participationCount;

            buf.append(key);
            buf.append(": ");
            buf.append(participationCount);
            buf.append(", ");
            buf.append(totalScore);
            buf.append(", ");
            buf.append(averageScore);
            buf.append("\n");
        }

        return buf.toString();
    }
}
