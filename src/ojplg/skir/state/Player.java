package ojplg.skir.state;

import ojplg.skir.ai.AutomatedPlayer;
import ojplg.skir.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {

    private final String _color;
    private final int _number;

    private final List<Card> _cards = new ArrayList<>();
    private final BattleStats _attackStats = new BattleStats();
    private final BattleStats _defenseStats = new BattleStats();

    private int _reserveArmies;
    //TODO: This should be a ClientKey object, not a String
    private String _clientKey;
    private String _displayName;
    private AutomatedPlayer _automatedPlayer;

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

    public List<Card> getCards(){
        return Collections.unmodifiableList(new ArrayList(_cards));
    }

    public void removeCards(List<Card> cards){
        _cards.removeAll(cards);
    }

    public void addCards(List<Card> cards) {
        _cards.addAll(cards);
    }

    public void addCard(Card card){
        _cards.add(card);
    }

    public void grantReserves(int cnt){
        _reserveArmies += cnt;
    }

    public int reserveCount(){
        return _reserveArmies;
    }

    public void drawReserves(int cnt){
        if (cnt > _reserveArmies){
            throw new RuntimeException("Cannot draw " + cnt + " armies from " + _reserveArmies + " reserves");
        }
        _reserveArmies -= cnt;
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

    public boolean hasReserves(){
        return _reserveArmies > 0;
    }

    public boolean hasMaximumCards(){
        return _cards.size() >= Constants.MAXIMUM_CARD_HOLDINGS;
    }

    public boolean hasTooManyCards(){
        return _cards.size() > Constants.MAXIMUM_CARD_HOLDINGS;
    }

    public AutomatedPlayer getAutomatedPlayer() {
        return _automatedPlayer;
    }

    public void setAutomatedPlayer(AutomatedPlayer _automatedPlayer) {
        this._automatedPlayer = _automatedPlayer;
    }

    public Object getAutomatedPlayerIdentification(){
        if( _automatedPlayer != null ){
            return _automatedPlayer.getIdentification();
        }
        return null;
    }

    public int getNumber(){
        return _number;
    }

    @Override
    public String toString() {
        return "Player{" +
                "_color='" + _color + '\'' +
                ", _cards=" + _cards +
                ", _reserveArmies=" + _reserveArmies +
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
