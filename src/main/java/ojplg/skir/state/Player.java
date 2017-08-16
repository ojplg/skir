package ojplg.skir.state;

import ojplg.skir.play.Rolls;

public class Player implements Comparable<Player> {

    private final String _color;
    private final int _number;

    private final BattleStats _stats = new BattleStats();

    //TODO: This should be a ClientKey object, not a String
    private String _clientKey;
    private String _displayName;

    public Player(String color, int number){
        _color = color;
        _number = number;
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

    public void updateAttackStatistics(Rolls rolls){
        _stats.updateAttackStats(rolls);
    }

    public void updateDefenseStatistics(Rolls rolls){
        _stats.updateDefenseStats(rolls);
    }

    public double attackLuckFactor(){
        return _stats.getAttackLuckFactor();
    }

    public double defenseLuckFactor(){
        return _stats.getDefenseLuckFactor();
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

    public boolean equals(Object that){
        if ( that == null ){
            return false;
        }
        if ( ! (that instanceof Player) ) {
            return false;
        }
        return this._color.equals(((Player)that)._color);
    }

    public int hashCode(){
        return _color.hashCode();
    }

    @Override
    public int compareTo(Player o) {
        return this.getNumber() - o.getNumber();
    }
}
