package play.orders;

import ai.AutomatedPlayer;
import card.Card;
import map.Country;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import play.Roller;
import state.Game;
import state.OrderBroadcasterLocator;
import state.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Adjutant {

    private static final Logger _log = LogManager.getLogger(Adjutant.class);

    private final Roller _roller;
    private final Player _activePlayer;
    private final List<OrderType> _allowableOrders = new ArrayList<OrderType>();
    private boolean _conqueredCountry = false;
    private Attack _successfulAttack = null;
    private AutomatedPlayer _automatedPlayer;

    public Adjutant(Player activePlayer, Roller roller, AutomatedPlayer automatedPlayer){
        this._activePlayer = activePlayer;
        this._roller = roller;
        this._allowableOrders.add(OrderType.ClaimArmies);
        this._automatedPlayer = automatedPlayer;
        if( automatedPlayer != null ){
            _log.warn("Made an adjutant with an automated player " + activePlayer);
        } else {
            _log.warn("MADE an adjutant for a human player");
        }
    }

    public boolean isAutomatedPlayer(){
        return _automatedPlayer != null;
    }

    public OrderType chooseOrderType(Game game){
        return _automatedPlayer.pickOrder(_allowableOrders, game);
    }

    public Adjutant executeAutomatedOrder(OrderType orderType, Game game){
        return _automatedPlayer.executeOrder(orderType, this, game);
    }

    public List<OrderType> allowableOrders() {
        return Collections.unmodifiableList(_allowableOrders);
    }

    public boolean mustChooseOrderType(){
        return allowableOrders().size() > 1;
    }

    public void setAllowableOrders(OrderType ... types){
        _allowableOrders.clear();
        _allowableOrders.addAll(Arrays.asList(types));
        OrderBroadcasterLocator.BROADCASTER.possibleOrderTypes(_activePlayer, _allowableOrders);
    }

    public void successfulAttack(Attack attack){
        _conqueredCountry = true;
        _successfulAttack = attack;
    }

    public Attack getSuccessfulAttack(){
        return _successfulAttack;
    }

    public boolean hasConqueredCountry(){
        return _conqueredCountry;
    }

    public Order endAttacks(){
        checkAllowable(OrderType.EndAttacks);
        return new EndAttacks(this);
    }

    public Order placeArmy(Country country){
        checkAllowable(OrderType.PlaceArmy);
        return new PlaceArmy(this, country);
    }

    public Order claimArmies() {
        checkAllowable(OrderType.ClaimArmies);
        return new ClaimArmies(this);
    }

    public Order occupy(int armies){
        checkAllowable(OrderType.Occupy);
        int toMove = Math.min(armies, _successfulAttack.getAttackersDiceCount());
        return new Occupy(this, _successfulAttack.getInvader(), _successfulAttack.getTarget(), toMove);
    }

    public Order attack(Country from, Country to, int dice){
        checkAllowable(OrderType.Attack);
        return new Attack(this, _roller, from, to, dice);
    }

    public Order exchangeCardSet(Card one, Card two, Card three){
        checkAllowable(OrderType.ExchangeCardSet);
        return new ExchangeCardSet(this, one, two, three);
    }

    private void checkAllowable(OrderType played){
        if( _allowableOrders.contains(played) ){
            throw new RuntimeException("Cannot " + played + " at this point. Only " + _allowableOrders);
        }
    }

    public Player getActivePlayer(){
        return _activePlayer;
    }

}
