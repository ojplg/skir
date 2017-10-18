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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * An AI that only fights when it has more forces
 * than its opponent.
 */
public class Bully implements AutomatedPlayer {

    private final static Logger _log = LogManager.getLogger(Bully.class);

    private final Player _me;

    public Bully(Player player){
        this(player, "Bully");
    }

    public Bully(Player player, String name){
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
            return new ExchangeCardSet(adjutant, set.getOne(), set.getTwo(), set.getThree());
        }
        if( possibleOrderTypes.contains(OrderType.PlaceArmy)){
            return placeArmy(adjutant, game);
        }
        if( possibleOrderTypes.contains(OrderType.Attack) && hasAdvantageousAttack(game) ){
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

    @Override
    public Player getPlayer() {
        return _me;
    }

    private Occupy generateOccupationOrder(Adjutant adjutant, Game game){
        OccupationConstraints constraints = adjutant.getOccupationConstraints();
        int occupationForce = Math.max(constraints.minimumOccupationForce(),
                game.getOccupationForce(constraints.attacker())/2);
        return new Occupy(adjutant, constraints.attacker(), constraints.conquered(),occupationForce);
    }

    private PlaceArmy placeArmy(Adjutant adjutant, Game game){
        Country country;
        Optional<PossibleAttack> possibleAttack = findBestPossibleAttack(game);
        if( possibleAttack.isPresent() ) {
            country = possibleAttack.get().getAttacker();
        } else {
            country = RandomUtils.pickRandomElement(game.findBorderCountries(_me));
        }
        return new PlaceArmy(adjutant, country);
    }

    private Attack findBestAttack(Game game, Adjutant adjutant){
        Optional<PossibleAttack> chosen = findBestPossibleAttack(game);
        PossibleAttack attack = chosen.get();
        int dice = Math.min(Constants.MAXIMUM_ATTACKER_DICE, game.getOccupationForce(attack.getAttacker()) - 1);
        return new Attack(adjutant, attack.getAttacker(), attack.getDefender(), dice);
    }

    private boolean hasAdvantageousAttack(Game game){
        return findAdvantageousAttacks(game).size() > 0;
    }

    private Optional<PossibleAttack> findBestPossibleAttack(Game game){
        List<PossibleAttack> advantages = findAdvantageousAttacks(game);
        Collections.shuffle(advantages);
        return ListUtils.findMax(advantages);
    }

    private List<PossibleAttack> findAdvantageousAttacks(Game game){
        return AiUtils.findAdvantageousAttacks(_me, game);
    }

}
