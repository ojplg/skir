package ojplg.skir.state;

import ojplg.skir.state.event.GameSpecifiable;

public class Player implements Comparable<Player>, GameSpecifiable {

    private final GameId _gameId;
    private final String _color;
    private final int _number;

    //TODO: This should be a ClientKey object, not a String
    private String _clientKey;
    private String _displayName;


    public Player(GameId gameId, String color, int number){
        _color = color;
        _number = number;
        _gameId = gameId;
    }

    public String getClientKey() {
        return _clientKey;
    }

    public void setClientKey(String _clientKey) {
        this._clientKey = _clientKey;
    }

    public String getDisplayName() {
        return _displayName;
    }

    public void setDisplayName(String displayName) {
        this._displayName = displayName;
    }

    public int getNumber(){
        return _number;
    }

    @Override
    public String toString() {
        return "Player{" +
                "_color='" + _color + '\'' +
                ", _displayName=" + _displayName +
                '}';
    }

    public String getColor(){
        return _color;
    }

    public boolean equals(Object that) {
        if (that == null) {
            return false;
        }
        return that instanceof Player
                && this._color.equals(((Player) that)._color)
                && this._gameId.equals(((Player) that)._gameId);
    }

    public int hashCode(){
        return _color.hashCode();
    }

    @Override
    public int compareTo(Player o) {
        return this.getNumber() - o.getNumber();
    }

    @Override
    public GameId getGameId() {
        return _gameId;
    }

    @Override
    public boolean matches(GameSpecifiable other) {
        return _gameId.equals(other.getGameId());
    }

}
