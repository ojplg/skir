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
import ojplg.skir.utils.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * An AI that implements a slow growth strategy.
 */
public class Pokey implements AutomatedPlayer {

    private final static Logger _log = LogManager.getLogger(Pokey.class);

    private final Player _me;
    private boolean _hasConqueredCountry = false;

    public Pokey(Player player){
        this(player, "Pokey");
    }

    public Pokey(Player player, String name){
        _me = player;
        player.setDisplayName(name);
    }

    @Override
    public void initialize(Game game) {
        // do nothing
    }

    @Override
    public Order generateOrder(Adjutant adjutant, Game game) {

        List<OrderType> possibleOrderTypes = adjutant.allowableOrders();

        if ( possibleOrderTypes.contains(OrderType.ExchangeCardSet)){
            CardSet set = CardSet.findTradeableSet(game.getPlayerHoldings(getPlayer()).getCards());
            return new ExchangeCardSet(adjutant, set);
        }
        if( possibleOrderTypes.contains(OrderType.PlaceArmy)){
            return placeArmy(adjutant, game);
        }
        if( possibleOrderTypes.contains(OrderType.Attack) && shouldMakeAttack(game) ){
            return findBestAttack(game, adjutant);
        } else if (possibleOrderTypes.contains(OrderType.Attack)){
            return new EndAttacks(adjutant);
        }
        if( possibleOrderTypes.contains(OrderType.DrawCard) ){
            return new DrawCard(adjutant);
        }
        if( possibleOrderTypes.contains(OrderType.ClaimArmies)){
            return new ClaimArmies(adjutant);
        }
        if( possibleOrderTypes.contains(OrderType.Fortify)){
            return new EndTurn(adjutant);
        }
        if ( possibleOrderTypes.contains(OrderType.Occupy)){
            return generateOccupationOrder(adjutant, game);
        }
        if( possibleOrderTypes.contains(OrderType.EndTurn)){
            return new EndTurn(adjutant);
        }
        throw new RuntimeException("Don't know what to do with these options " + possibleOrderTypes);
    }

    public Player getPlayer(){
        return _me;
    }

    private PlaceArmy placeArmy(Adjutant adjutant, Game game){
        _hasConqueredCountry = false;
        int reserveCount = game.getPlayerHoldings(_me).reserveCount();
        List<Country> unorderedBorders = game.findBorderCountries(_me);
        List<Country> orderedBorders = AiUtils.orderByOccupationForce(unorderedBorders, game);
        for (int idx=0; idx<orderedBorders.size(); idx++){
            Country country = orderedBorders.get(idx);
            if( idx + 1 == orderedBorders.size()){
                return new PlaceArmy(adjutant, country, reserveCount);
            }
            int myForce = game.getOccupationForce(country);
            List<Country> enemyNeighbors = game.findEnemyNeighbors(country);
            for(Country neighbor : enemyNeighbors){
                int enemyForce = game.getOccupationForce(neighbor);
                if( enemyForce < myForce + reserveCount){
                    return new PlaceArmy(adjutant, country, reserveCount);
                }
            }
        }
        throw new RuntimeException("Something went wrong");
    }

    private Occupy generateOccupationOrder(Adjutant adjutant, Game game){
        _hasConqueredCountry = true;
        OccupationConstraints constraints = adjutant.getOccupationConstraints();
        int occupationForce = Math.max(constraints.minimumOccupationForce(),
                game.getOccupationForce(constraints.attacker())/2);
        return new Occupy(adjutant, constraints.attacker(), constraints.conquered(),occupationForce);
    }

    private Order findBestAttack(Game game, Adjutant adjutant){
        PossibleAttack chosen = findBestPossibleAttack(game);
        if ( game.getOccupationForce(chosen.getAttacker()) - 1 >= Constants.MAXIMUM_ATTACKER_DICE){
            return new Attack(adjutant, chosen.getAttacker(), chosen.getDefender(),Constants.MAXIMUM_ATTACKER_DICE);

        }
        return new EndAttacks(adjutant);
    }

    private boolean shouldMakeAttack(Game game){
        return  AiUtils.findAdvantageousAttacks(_me, game).size() > 0
                && ! _hasConqueredCountry;
    }

    private PossibleAttack findBestPossibleAttack(Game game){
        List<PossibleAttack> advantages = AiUtils.findAdvantageousAttacks(_me, game);
        return ListUtils.findMax(advantages).get();
    }

}
