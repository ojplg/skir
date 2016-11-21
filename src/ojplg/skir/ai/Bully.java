package ojplg.skir.ai;

import ojplg.skir.card.Card;
import ojplg.skir.card.Cards;
import ojplg.skir.map.Country;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Attack;
import ojplg.skir.play.orders.ClaimArmies;
import ojplg.skir.play.orders.DrawCard;
import ojplg.skir.play.orders.EndAttacks;
import ojplg.skir.play.orders.EndTurn;
import ojplg.skir.play.orders.ExchangeCardSet;
import ojplg.skir.play.orders.OccupationConstraints;
import ojplg.skir.play.orders.Occupy;
import ojplg.skir.play.orders.Order;
import ojplg.skir.play.orders.OrderType;
import ojplg.skir.play.orders.PlaceArmy;
import ojplg.skir.state.Constants;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bully implements AutomatedPlayer {

    private final static Logger _log = LogManager.getLogger(Bully.class);

    private final Player _me;

    public Bully(Player player){
        _me = player;
    }

    @Override
    public OrderType pickOrderType(List<OrderType> possibleOrderTypes, Game game) {
        if ( possibleOrderTypes.contains(OrderType.ExchangeCardSet)){
            return OrderType.ExchangeCardSet;
        }
        if( possibleOrderTypes.contains(OrderType.PlaceArmy)){
            return OrderType.PlaceArmy;
        }
        if( possibleOrderTypes.contains(OrderType.Attack) && hasAdvantageousAttack(game) ){
            return OrderType.Attack;
        } else if (possibleOrderTypes.contains(OrderType.Attack)){
            return OrderType.EndAttacks;
        }
        if( possibleOrderTypes.contains(OrderType.DrawCard) ){
            return OrderType.DrawCard;
        }
        if( possibleOrderTypes.contains(OrderType.ClaimArmies)){
            return OrderType.ClaimArmies;
        }
        if( possibleOrderTypes.contains(OrderType.Fortify)){
            return OrderType.EndTurn;
        }
        if ( possibleOrderTypes.contains(OrderType.Occupy)){
            return OrderType.Occupy;
        }
        _log.warn("Don't know what to do with these options " + possibleOrderTypes);
        return null;
    }


    public Player getPlayer(){
        return _me;
    }

    @Override
    public Order generateOrder(OrderType orderType, Adjutant adjutant, Game game){
        Order order;
        if( orderType == OrderType.PlaceArmy){
            order = placeArmy(adjutant, game);
        } else if( orderType == OrderType.EndAttacks ){
            order = new EndAttacks(adjutant);
        } else if(orderType ==OrderType.DrawCard ){
            order = new DrawCard(adjutant);
        } else if( orderType == OrderType.ClaimArmies){
            order = new ClaimArmies(adjutant);
        } else if( orderType == OrderType.EndTurn){
            order = new EndTurn(adjutant);
        } else if( orderType == OrderType.Attack){
            order = findBestAttack(game, adjutant);
        } else if( orderType == OrderType.Occupy){
            order = generateOccupationOrder(adjutant, game);
        } else if( orderType == OrderType.ExchangeCardSet){
            List<Card> set = Cards.findTradeableSet(getPlayer().getCards());
            order = new ExchangeCardSet(adjutant, set.get(0), set.get(1), set.get(2));
        } else {
            _log.warn("Don't know what to do with this type " + orderType);
            return null;
        }
        return order;
    }

    private Occupy generateOccupationOrder(Adjutant adjutant, Game game){
        OccupationConstraints constraints = adjutant.getOccupationConstraints();
        int occupationForce = Math.max(constraints.minimumOccupationForce(),
                game.getOccupationForce(constraints.attacker())/2);
        return new Occupy(adjutant, constraints.attacker(), constraints.conquered(),occupationForce);
    }

    private PlaceArmy placeArmy(Adjutant adjutant, Game game){
        List<Country> countries = game.countriesOccupied(_me);
        int minArmies = game.getOccupationForce(countries.get(0));
        Country countryToFortify = countries.get(0);
        for(Country country : countries){
            if( game.getOccupationForce(country) < minArmies){
                minArmies = game.getOccupationForce(country);
                countryToFortify = country;
            }
        }
        return new PlaceArmy(adjutant,countryToFortify);
    }

    private Attack findBestAttack(Game game, Adjutant adjutant){
        List<PossibleAttack> advantages = findAdvantageousAttacks(game);
        Collections.shuffle(advantages);
        Collections.sort(advantages);
        PossibleAttack chosen = advantages.get(0);
        int dice = Math.min(Constants.MAXIMUM_ATTACKER_DICE, game.getOccupationForce(chosen.getAttacker()) - 1);
        return new Attack(adjutant, chosen.getAttacker(), chosen.getDefender(),dice);
    }

    private boolean hasAdvantageousAttack(Game game){
        return findAdvantageousAttacks(game).size() > 0;
    }

    private List<PossibleAttack> findAdvantageousAttacks(Game game){
        List<PossibleAttack> advantages = new ArrayList<PossibleAttack>();
        for(Country country : game.countriesToAttackFrom(_me)){
            int myForce = game.getOccupationForce(country);
            for(Country enemyNeighbor : game.enemyNeighbors(country)){
                int enemyForce = game.getOccupationForce(enemyNeighbor);
                if( myForce > enemyForce){
                    advantages.add(new PossibleAttack(country, enemyNeighbor, enemyForce - myForce));
                }
            }
        }
        return advantages;
    }
}
