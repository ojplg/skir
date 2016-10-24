package play.orders;

import card.Card;
import map.Country;
import play.Roller;
import state.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Adjutant {

    private final Roller _roller;
    private final Player _activePlayer;
    private final List<OrderType> _allowableOrders = new ArrayList<OrderType>();
    private boolean _conqueredCountry = false;
    private Attack _successfulAttack = null;

    public Adjutant(Player activePlayer, Roller roller){
        this._activePlayer = activePlayer;
        this._roller = roller;
        this._allowableOrders.add(OrderType.ClaimArmies);
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
    }

    public void successfulAttack(Attack attack){
        _conqueredCountry = true;
        _successfulAttack = attack;
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
