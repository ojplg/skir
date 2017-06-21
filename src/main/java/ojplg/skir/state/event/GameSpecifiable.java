package ojplg.skir.state.event;

import ojplg.skir.state.GameId;

public interface GameSpecifiable {
    GameId getGameId();
    default boolean matches(GameSpecifiable other){
        return other.getGameId().equals(getGameId());
    }
}
