package web;

import card.Card;
import card.Cards;
import map.Country;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import play.orders.Adjutant;
import play.orders.Attack;
import play.orders.AttackUntilVictoryOrDeath;
import play.orders.ClaimArmies;
import play.orders.DrawCard;
import play.orders.EndAttacks;
import play.orders.EndTurn;
import play.orders.ExchangeCardSet;
import play.orders.Fortify;
import play.orders.OccupationConstraints;
import play.orders.Occupy;
import play.orders.Order;
import play.orders.PlaceArmy;

import java.util.List;

public class OrderJsonParser {

    private static final Logger _log = LogManager.getLogger(OrderJsonParser.class);

    private final Adjutant _adjutant;

    public OrderJsonParser(Adjutant adjutant){
        this._adjutant = adjutant;
    }

    public Order parseOrder(JSONObject orderJson){
        String orderType = (String) orderJson.get("orderType");
        if( "PlaceArmy".equals(orderType)){
            String countryName = (String) orderJson.get("country");
            Country country = new Country(countryName);
            PlaceArmy placeArmy = new PlaceArmy(_adjutant, country);
            return placeArmy;
        } else if ("Attack".equals(orderType) || "AttackUntilVictoryOrDeath".equals(orderType)){
            String attacker = (String) orderJson.get("from");
            String defender = (String) orderJson.get("to");
            Order attack;
            Country attackFrom = new Country(attacker);
            Country attackTo = new Country(defender);
            String numberArmiesString = (String) orderJson.get("army_count");
            int numberArmies = Integer.parseInt(numberArmiesString);
            if("Attack".equals(orderType)) {
                attack = new Attack(_adjutant, attackFrom, attackTo, numberArmies);
            } else {
                attack = new AttackUntilVictoryOrDeath(_adjutant, attackFrom, attackTo);
            }
            return attack;
        } else if ("DoOccupation".equals(orderType)){
            OccupationConstraints constraints = _adjutant.getOccupationConstraints();
            _log.info("occupation constraints " + constraints);
            String occupationForce = (String) orderJson.get("occupationForce"); //successfulAttack.getAttackersDiceCount();
            int armiesToMove = Integer.parseInt(occupationForce);
            Occupy occupy = new Occupy(_adjutant, constraints.attacker(),
                    constraints.conquered(), armiesToMove);
            return occupy;
        } else if ("EndAttacks".equals(orderType) ) {
            EndAttacks endAttacks = new EndAttacks(_adjutant);
            return endAttacks;
        } else if ("DrawCard".equals(orderType)){
            DrawCard drawCard = new DrawCard(_adjutant);
            return drawCard;
        } else if ("ClaimArmies".equals(orderType) ){
            ClaimArmies claimArmies = new ClaimArmies(_adjutant);
            return claimArmies;
        } else if ("Fortify".equals(orderType)) {
            String from = (String) orderJson.get("from");
            String to = (String) orderJson.get("to");
            String numberArmiesString = (String) orderJson.get("army_count");
            int numberArmies = Integer.parseInt(numberArmiesString);
            Fortify fortify = new Fortify(_adjutant, new Country(from), new Country(to), numberArmies);
            return fortify;
        } else if("ExchangeCardSet".equals(orderType)) {
            return newExchangeCardSet();
        } else if("EndTurn".equals(orderType)) {
            return new EndTurn(_adjutant);
        } else {
            _log.error("Cannot handle " + orderJson);
            return null;
        }
    }

    private ExchangeCardSet newExchangeCardSet(){
        // TODO: Allow user to select set?
        List<Card> toExchange = Cards.findTradeableSet(_adjutant.getActivePlayer().getCards());
        Card one = toExchange.get(0);
        Card two = toExchange.get(1);
        Card three = toExchange.get(2);
        return new ExchangeCardSet(_adjutant, one, two, three);
    }
}
