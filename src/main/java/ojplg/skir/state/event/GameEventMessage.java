package ojplg.skir.state.event;

import org.json.simple.JSONObject;

import java.util.List;

public interface GameEventMessage extends GameSpecifiable {
    int getTurnNumber();
    GameEventType getGameEventType();
    String getPlayerIdentifier();
    List<String> getPlayerIdentifiers();
    boolean isGameOver();
    JSONObject toJson();
}
