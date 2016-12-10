package ojplg.skir.state;

public class BattleStats {
    private int _numberBattles = 0;
    private int _numberArmyBattles = 0;
    private double _expectationsDifference = 0.0;

    public void updateStats(double expectationDifference, int numberArmyBattles){
        _numberBattles++;
        _numberArmyBattles+=numberArmyBattles;
        _expectationsDifference+=expectationDifference;
    }

    public double getExpectationsDifference() {
        return _expectationsDifference;
    }
}
