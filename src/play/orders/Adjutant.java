package play.orders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import state.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Adjutant {

    private static final Logger _log = LogManager.getLogger(Adjutant.class);

    private final Player _activePlayer;
    private final List<OrderType> _allowableOrders;
    private final OrderConstraints _orderConstraints;
    private final boolean _hasConqueredCountry;

    public static Adjutant nextPlayer(Player player){
        return new Adjutant(player, false, OrderType.ClaimArmies);
    }

    public Adjutant forOrderTypes(OrderType ... allowableOrders){
        return new Adjutant(this._activePlayer, this.hasConqueredCountry(), allowableOrders);
    }

    public Adjutant forOrderType(OrderType allowableType){
        return new Adjutant(this._activePlayer, this.hasConqueredCountry(), allowableType);
    }

    public Adjutant forOrderType(OrderType allowableType, OrderConstraints constraints){
        return new Adjutant(this._activePlayer, this.hasConqueredCountry(), allowableType, constraints);
    }

    public Adjutant afterConquest(Attack attack){
        OccupationConstraints constraints = new OccupationConstraints(attack);
        return new Adjutant(this._activePlayer, true, OrderType.Occupy, constraints);
    }

    private Adjutant(Player activePlayer, boolean conqueredCountry, OrderType allowableType){
        this._activePlayer = activePlayer;
        this._allowableOrders = Collections.singletonList(allowableType);
        this._orderConstraints = null;
        this._hasConqueredCountry = conqueredCountry;
    }

    private Adjutant(Player activePlayer, boolean conqueredCountry, OrderType allowableType, OrderConstraints constraints){
        this._activePlayer = activePlayer;
        this._allowableOrders = Collections.singletonList(allowableType);
        this._orderConstraints = constraints;
        this._hasConqueredCountry = conqueredCountry;
    }


    private Adjutant(Player activePlayer, boolean conqueredCountry, OrderType ... allowableOrders){
        this._activePlayer = activePlayer;
        this._allowableOrders = Arrays.asList(allowableOrders);
        this._orderConstraints = null;
        this._hasConqueredCountry = conqueredCountry;
    }

    public List<OrderType> allowableOrders() {
        return Collections.unmodifiableList(_allowableOrders);
    }

    public boolean mustChooseOrderType(){
        return allowableOrders().size() > 1;
    }

    public boolean hasOrderConstraints() {
        return _orderConstraints != null;
    }

    public OccupationConstraints getOccupationConstraints(){
        return (OccupationConstraints) _orderConstraints;
    }

    public boolean hasConqueredCountry(){
        return _hasConqueredCountry;
    }

    public Player getActivePlayer(){
        return _activePlayer;
    }
}
