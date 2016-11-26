package ojplg.skir.state.event;

import ojplg.skir.map.Country;
import ojplg.skir.play.orders.OrderType;
import ojplg.skir.state.Player;
import org.json.simple.JSONObject;

public class OrderEvent {

    private final OrderType _orderType;
    private final Player _player;
    private final Country _fromCountry;
    private final Country _toCountry;

    private OrderEvent(OrderType orderType, Player player){
        this(orderType, player, null, null);
    }

    private OrderEvent(OrderType orderType, Player player, Country fromCountry, Country toCountry){
        this._orderType = orderType;
        this._player = player;
        this._fromCountry = fromCountry;
        this._toCountry = toCountry;
    }

    public static OrderEvent forAttack(Player player, Country fromCountry, Country toCountry){
        return new OrderEvent(OrderType.Attack, player, fromCountry, toCountry);
    }

    public static OrderEvent forOccupy(Player player, Country fromCountry, Country toCountry){
        return new OrderEvent(OrderType.Occupy, player, fromCountry, toCountry);
    }

    public static OrderEvent forFortify(Player player, Country fromCountry, Country toCountry){
        return new OrderEvent(OrderType.Fortify, player, fromCountry, toCountry);
    }

    public static OrderEvent forCardExchange(Player player){
        return new OrderEvent(OrderType.ExchangeCardSet, player);
    }

    public JSONObject toJson(){
        JSONObject jObject = new JSONObject();
        jObject.put("message_type", "order_event");
        jObject.put("player", _player.getColor());
        jObject.put("order_type", _orderType.toString());
        if( _fromCountry != null ) {
            jObject.put("from_country", _fromCountry.getName());
        }
        if ( _toCountry != null) {
            jObject.put("to_country", _toCountry.getName());
        }
        jObject.put("simple_text", simpleText());
        return jObject;
    }

    private String simpleText(){
        StringBuilder bldr = new StringBuilder();
        bldr.append(_player.getColor());
        if( _orderType == OrderType.Attack){
            bldr.append(" attacks ");
        }
        if ( _orderType == OrderType.Fortify){
            bldr.append(" fortifies ");
        }
        if ( _orderType == OrderType.Occupy ){
            bldr.append(" conquers ");
        }
        if ( _orderType == OrderType.Attack
                || _orderType == OrderType.Fortify
                || _orderType == OrderType.Occupy){
            bldr.append(_toCountry.getName());
            bldr.append(" from ");
            bldr.append(_fromCountry.getName());
        }
        if ( _orderType == OrderType.ExchangeCardSet ){
            bldr.append(" exchanges cards");
        }
        return bldr.toString();
    }
}
