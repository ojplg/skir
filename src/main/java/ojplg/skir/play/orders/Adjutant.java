package ojplg.skir.play.orders;

import ojplg.skir.state.GameId;
import ojplg.skir.state.event.GameSpecifiable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import ojplg.skir.state.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Adjutant implements GameSpecifiable {

    private static final Logger _log = LogManager.getLogger(Adjutant.class);

    private final Player _activePlayer;
    private final List<ConstrainedOrderType> _allowableOrders;
    private final boolean _hasConqueredCountry;
    private final int _turnNumber;
    private final GameId _gameId;

    public static Adjutant newGameAdjutant(GameId gameId, Player player){
        return new Adjutant(gameId, player, false, OrderType.ClaimArmies, 1);
    }

    public Adjutant nextPlayer(Player player, int turnNumber){
        return new Adjutant(this._gameId, player, false, OrderType.ClaimArmies, turnNumber);
    }

    public Adjutant forConstrainedOrderTypes(ConstrainedOrderType... constrained){
        return new Adjutant(this._gameId, this._activePlayer, this.hasConqueredCountry(), this._turnNumber,constrained);
    }

    public Adjutant afterConquest(Attack attack, int maximumAvailableToMove){
        return new Adjutant(this._gameId, this._activePlayer, true, this._turnNumber,
                ConstrainedOrderType.occupation(attack, maximumAvailableToMove));
    }

    private Adjutant(GameId gameId, Player activePlayer, boolean conqueredCountry, int turnNumber,
                     ConstrainedOrderType constrainedOrderType){
        this._activePlayer = activePlayer;
        this._hasConqueredCountry = conqueredCountry;
        this._allowableOrders = Collections.singletonList(constrainedOrderType);
        this._turnNumber = turnNumber;
        this._gameId = gameId;
    }

    private Adjutant(GameId gameId, Player activePlayer, boolean conqueredCountry, int turnNumber,
                     ConstrainedOrderType... constrainedOrderTypes){
        this._activePlayer = activePlayer;
        this._hasConqueredCountry = conqueredCountry;
        this._allowableOrders = Collections.unmodifiableList(
                Arrays.asList(constrainedOrderTypes));
        this._turnNumber = turnNumber;
        this._gameId = gameId;
    }

    // This constructor is public only for testing ... do not use
    public Adjutant(GameId gameId, Player activePlayer, boolean conqueredCountry, OrderType allowableType, int turnNumber){
        this._activePlayer = activePlayer;
        this._allowableOrders = Collections.singletonList(ConstrainedOrderType.unconstrainedOrder(allowableType));
        this._hasConqueredCountry = conqueredCountry;
        this._turnNumber = turnNumber;
        this._gameId = gameId;
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

    public int getTurnNumber() {
        return _turnNumber;
    }

    @Override
    public GameId getGameId() {
        return _gameId;
    }

    public JSONObject toPossibleOrdersJson(){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type","possible_order_types");
        jObject.put("color", getActivePlayer().getColor());
        jObject.put("turn_number", _turnNumber);
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
