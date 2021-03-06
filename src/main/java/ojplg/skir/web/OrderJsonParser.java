package ojplg.skir.web;

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

public class OrderJsonParser {

    private static final Logger _log = LogManager.getLogger(OrderJsonParser.class);

    private final Adjutant _adjutant;

    public OrderJsonParser(Adjutant adjutant){
        this._adjutant = adjutant;
    }

    public Order parseOrder(JSONObject orderJson){
        String orderType = (String) orderJson.get("orderType");
        if( "PlaceArmy".equals(orderType)){
            return newPlaceArmy(orderJson);
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
        } else if ("Occupy".equals(orderType)){
            OccupationConstraints constraints = _adjutant.getOccupationConstraints();
            _log.info("occupation constraints " + constraints);
            String occupationForce = (String) orderJson.get("occupationForce"); //successfulAttack.getAttackersDiceCount();
            int armiesToMove = Integer.parseInt(occupationForce);
            Occupy occupy = new Occupy(_adjutant, constraints.attacker(),
                    constraints.conquered(), armiesToMove);
            return occupy;
        } else if ("EndAttacks".equals(orderType) ) {
            return new EndAttacks(_adjutant);
        } else if ("DrawCard".equals(orderType)){
            return new DrawCard(_adjutant);
        } else if ("ClaimArmies".equals(orderType) ){
            return new ClaimArmies(_adjutant);
        } else if ("Fortify".equals(orderType)) {
            String from = (String) orderJson.get("from");
            String to = (String) orderJson.get("to");
            String numberArmiesString = (String) orderJson.get("army_count");
            int numberArmies = Integer.parseInt(numberArmiesString);
            return new Fortify(_adjutant, new Country(from), new Country(to), numberArmies);
        } else if("ExchangeCardSet".equals(orderType)) {
            return newExchangeCardSet();
        } else if("EndTurn".equals(orderType)) {
            return new EndTurn(_adjutant);
        } else {
            _log.error("Cannot handle " + orderJson);
            return null;
        }
    }

    private PlaceArmy newPlaceArmy(JSONObject orderJson){
        String countryName = (String) orderJson.get("country");
        int numberArmies = Integer.parseInt((String) orderJson.get("number_armies"));
        Country country = new Country(countryName);
        PlaceArmy placeArmy = new PlaceArmy(_adjutant, country, numberArmies);
        return placeArmy;
    }

    private ExchangeCardSet newExchangeCardSet(){
        // TODO: Allow user to select set?
        return new ExchangeCardSet(_adjutant);
    }
}
