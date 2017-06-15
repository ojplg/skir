package ojplg.skir.state;

public class Player {

    private final String _color;
    private final int _number;

    private final BattleStats _attackStats = new BattleStats();
    private final BattleStats _defenseStats = new BattleStats();

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


    public void updateAttackStatistics(double expectationsDifference, int numberArmyBattles){
        _attackStats.updateStats(expectationsDifference, numberArmyBattles);
    }

    public void updateDefenseStatistics(double expectationsDifference, int numberArmyBattles){
        _defenseStats.updateStats(expectationsDifference, numberArmyBattles);
    }

    public double attackLuckFactor(){
        return _attackStats.getExpectationsDifference();
    }

    public double defenseLuckFactor(){
        return _defenseStats.getExpectationsDifference();
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
}
