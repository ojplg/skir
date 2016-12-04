package ojplg.skir.play.orders;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import ojplg.skir.state.Player;

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

    public Adjutant forConstrainedOrderTypes(ConstrainedOrderType... constrained){
        return new Adjutant(this._activePlayer, this.hasConqueredCountry(),constrained);
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

    // This constructor is public only for testing ... do not use
    public Adjutant(Player activePlayer, boolean conqueredCountry, OrderType allowableType){
        this._activePlayer = activePlayer;
        this._allowableOrders = Collections.singletonList(ConstrainedOrderType.unconstrainedOrder(allowableType));
        this._hasConqueredCountry = conqueredCountry;
    }

    public List<OrderType> allowableOrders() {
        List<OrderType> orderTypes = new ArrayList<OrderType>();
        for(ConstrainedOrderType constrainedOrderType : _allowableOrders) {
            orderTypes.add(constrainedOrderType.getOrderType());
        }
        return Collections.unmodifiableList(orderTypes);
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

    public JSONObject toPossibleOrdersJson(){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type","possible_order_types");
        jObject.put("color", getActivePlayer().getColor());
        JSONObject orderTypes = new JSONObject();
        for(OrderType type : allowableOrders()){
            OrderConstraints orderConstraints = findConstraintsForOrderType(type);
            JSONObject constraintJson = orderConstraints.toJsonObject();
            orderTypes.put(type.toString(), constraintJson);
        }
        jObject.put("order_types", orderTypes);

        return jObject;
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
