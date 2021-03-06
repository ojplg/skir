package ojplg.skir.state;

import ojplg.skir.state.event.GameSpecifiable;

import java.io.Serializable;

public class GameId implements GameSpecifiable, Comparable<GameId>, Serializable {

    private static volatile int _counter = 0;
    private final int _id;

    public static GameId next(){
        synchronized (GameId.class) {
            _counter++;
            return new GameId(_counter);
        }
    }

    public static GameId fromString(String s){
        int value = Integer.valueOf(s);
        guard(value);
        return new GameId(value);
    }

    public static GameId fromLong(long i){
        int number = (int) i;
        guard(number);
        return new GameId(number);
    }

    private static void guard(int number){
        if( number >= _counter ){
            _counter = number + 1;
        }
    }

    private GameId(int number){
        _id = number;
    }

    @Override
    public int hashCode() {
        return _id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GameId){
            GameId other = (GameId) obj;
            return this._id == other._id;
        }
        return false;
    }

    public int getId(){
        return _id;
    }

    @Override
    public String toString() {
        return Integer.toString(_id);
    }

    @Override
    public GameId getGameId() {
        return this;
    }

    @Override
    public int compareTo(GameId o) {
        return this._id - o._id;
    }
}
