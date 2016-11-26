package ojplg.skir.web;

import ojplg.skir.card.Card;
import ojplg.skir.card.CardSet;
import ojplg.skir.card.Cards;
import ojplg.skir.map.Country;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Attack;
import ojplg.skir.play.orders.AttackUntilVictoryOrDeath;
import ojplg.skir.play.orders.ClaimArmies;
import ojplg.skir.play.orders.DrawCard;
import ojplg.skir.play.orders.EndAttacks;
import ojplg.skir.play.orders.EndTurn;
import ojplg.skir.play.orders.ExchangeCardSet;
import ojplg.skir.play.orders.Fortify;
import ojplg.skir.play.orders.OccupationConstraints;
import ojplg.skir.play.orders.Occupy;
import ojplg.skir.play.orders.Order;
import ojplg.skir.play.orders.PlaceArmy;

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
        CardSet set = CardSet.findTradeableSet(_adjutant.getActivePlayer().getCards());
        return new ExchangeCardSet(_adjutant, set.getOne(), set.getTwo(), set.getThree());
    }
}
