package play.orders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import state.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Adjutant {

    private static final Logger _log = LogManager.getLogger(Adjutant.class);

    private final Player _activePlayer;
    private final List<ConstrainedOrderType> _allowableOrders;
    private final boolean _hasConqueredCountry;

    public static Adjutant nextPlayer(Player player){
        return new Adjutant(player, false, OrderType.ClaimArmies);
    }

    public Adjutant forConstrainedOrderTypes(ConstrainedOrderType ... constrained){
        return new Adjutant(this._activePlayer, this.hasConqueredCountry(),constrained);
    }

    public Adjutant forOrderTypes(OrderType ... allowableOrders){
        return new Adjutant(this._activePlayer, this.hasConqueredCountry(), allowableOrders);
    }

    public Adjutant forOrderType(OrderType allowableType){
        return new Adjutant(this._activePlayer, this.hasConqueredCountry(), allowableType);
    }

    public Adjutant afterConquest(Attack attack, int maximumAvailableToMove){
        return new Adjutant(this._activePlayer, true, ConstrainedOrderType.occupation(attack, maximumAvailableToMove));
    }

    private Adjutant(Player activePlayer, boolean conqueredCountry, ConstrainedOrderType constrainedOrderType){
        this._activePlayer = activePlayer;
        this._hasConqueredCountry = conqueredCountry;
        this._allowableOrders = Collections.singletonList(constrainedOrderType);
    }

    private Adjutant(Player activePlayer, boolean conqueredCountry, ConstrainedOrderType... constrainedOrderTypes){
        this._activePlayer = activePlayer;
        this._hasConqueredCountry = conqueredCountry;
        this._allowableOrders = Collections.unmodifiableList(
                Arrays.asList(constrainedOrderTypes));
    }

    private Adjutant(Player activePlayer, boolean conqueredCountry, OrderType allowableType){
        this._activePlayer = activePlayer;
        this._allowableOrders = Collections.singletonList(ConstrainedOrderType.unconstrainedOrder(allowableType));
        this._hasConqueredCountry = conqueredCountry;
    }

    private Adjutant(Player activePlayer, boolean conqueredCountry, OrderType ... allowableOrders){
        this._activePlayer = activePlayer;
        List<ConstrainedOrderType> constrainedOrderTypes = new ArrayList<ConstrainedOrderType>();
        for(OrderType orderType : allowableOrders){
            constrainedOrderTypes.add(ConstrainedOrderType.unconstrainedOrder(orderType));
        }
        this._allowableOrders = Collections.unmodifiableList(constrainedOrderTypes);
        this._hasConqueredCountry = conqueredCountry;
    }

    public List<OrderType> allowableOrders() {
        List<OrderType> orderTypes = new ArrayList<OrderType>();
        for(ConstrainedOrderType constrainedOrderType : _allowableOrders) {
            orderTypes.add(constrainedOrderType.getOrderType());
        }
        return Collections.unmodifiableList(orderTypes);
    }

    public boolean mustChooseOrderType(){
        return allowableOrders().size() > 1;
    }

    public OccupationConstraints getOccupationConstraints(){
        return (OccupationConstraints) findConstraintsForOrderType(OrderType.Occupy);
    }

    public OrderConstraints findConstraintsForOrderType(OrderType orderType){
        for(ConstrainedOrderType constrainedOrderType : _allowableOrders){
            if(constrainedOrderType.getOrderType() == orderType){
                return constrainedOrderType.getConstraints();
            }
        }
        return new UnconstrainedOrder();
    }

    public boolean hasConqueredCountry(){
        return _hasConqueredCountry;
    }

    public Player getActivePlayer(){
        return _activePlayer;
    }

    @Override
    public String toString() {
        return "Adjutant{" +
                "_activePlayer=" + _activePlayer +
                ", _allowableOrders=" + _allowableOrders +
                ", _hasConqueredCountry=" + _hasConqueredCountry +
                '}';
    }
}
