package ojplg.skir.ai;

import ojplg.skir.card.CardSet;
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

    private OrderType pickOrderType(List<OrderType> possibleOrderTypes, Game game) {
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
    public Order generateOrder(Adjutant adjutant, Game game){

        OrderType orderType = pickOrderType(adjutant.allowableOrders(), game);

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
            CardSet set = CardSet.findTradeableSet(getPlayer().getCards());
            order = new ExchangeCardSet(adjutant, set.getOne(), set.getTwo(), set.getThree());
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
        Country country;
        PossibleAttack possibleAttack = findBestPossibleAttack(game);
        if( possibleAttack == null ) {
            country = game.countriesOccupied(_me).get(0);
        } else {
            country = possibleAttack.getAttacker();
        }
        return new PlaceArmy(adjutant, country);
    }

    private Attack findBestAttack(Game game, Adjutant adjutant){
        PossibleAttack chosen = findBestPossibleAttack(game);
        int dice = Math.min(Constants.MAXIMUM_ATTACKER_DICE, game.getOccupationForce(chosen.getAttacker()) - 1);
        return new Attack(adjutant, chosen.getAttacker(), chosen.getDefender(),dice);
    }

    private boolean hasAdvantageousAttack(Game game){
        return findAdvantageousAttacks(game).size() > 0;
    }

    private PossibleAttack findBestPossibleAttack(Game game){
        List<PossibleAttack> advantages = findAdvantageousAttacks(game);
        if( advantages.size() == 0 ){
            return null;
        }
        Collections.shuffle(advantages);
        Collections.sort(advantages);
        return advantages.get(0);
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
