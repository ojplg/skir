package ojplg.skir.state;

public class GameId {

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
        return new GameId(value);
    }

    public static GameId fromLong(long i){
        return new GameId((int) i);
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
}
