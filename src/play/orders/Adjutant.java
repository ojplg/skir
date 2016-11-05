package play.orders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import play.Roller;
import state.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Adjutant {

    private static final Logger _log = LogManager.getLogger(Adjutant.class);

    private final Player _activePlayer;
    private final List<OrderType> _allowableOrders = new ArrayList<OrderType>();
    private boolean _conqueredCountry = false;
    private Attack _successfulAttack = null;

    public Adjutant(Player activePlayer){
        this._activePlayer = activePlayer;
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

    public Attack getSuccessfulAttack(){
        return _successfulAttack;
    }

    public boolean hasConqueredCountry(){
        return _conqueredCountry;
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
