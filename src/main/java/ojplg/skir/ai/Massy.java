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
import ojplg.skir.play.orders.Fortify;
import ojplg.skir.play.orders.OccupationConstraints;
import ojplg.skir.play.orders.Occupy;
import ojplg.skir.play.orders.Order;
import ojplg.skir.play.orders.OrderType;
import ojplg.skir.play.orders.PlaceArmy;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

import java.util.List;

public class Massy implements AutomatedPlayer {

    private final Player _me;

    private boolean _conqueredOne = false;

    public Massy(Player player){
        _me = player;
        _me.setDisplayName("Massy");
    }

    public Massy(Player player, String name){
        _me = player;
        _me.setDisplayName(name);
    }

    @Override
    public void initialize(Game game) {
    }

    @Override
    public Order generateOrder(Adjutant adjutant, Game game) {
        List<OrderType> orderTypeList = adjutant.allowableOrders();
        if( orderTypeList.contains(OrderType.ExchangeCardSet)){
            return new ExchangeCardSet(adjutant, CardSet.findTradeableSet(game.getPlayerHoldings(_me).getCards()));
        }
        if( orderTypeList.contains(OrderType.ClaimArmies)){
            return new ClaimArmies(adjutant);
        }
        if( orderTypeList.contains(OrderType.PlaceArmy)){
            Country country = AiUtils.findStrongestPossession(_me,game);
            return new PlaceArmy(adjutant, country, game.getPlayerHoldings(_me).reserveCount());
        }
        if( orderTypeList.contains(OrderType.Attack)){
            if( _conqueredOne ){
                return new EndAttacks(adjutant);
            }
            PossibleAttack possibleAttack = AiUtils.findBestPossibleAttack(_me, game);
            if (possibleAttack == null){
                return new EndAttacks(adjutant);
            }
            return new Attack(adjutant, possibleAttack.getAttacker(),
                    possibleAttack.getDefender(),
                    game.getOccupationForce(possibleAttack.getAttacker()) - 1);
        }
        if( orderTypeList.contains(OrderType.Occupy)){
            _conqueredOne = true;
            OccupationConstraints oc = adjutant.getOccupationConstraints();
            return new Occupy(adjutant, oc.attacker(), oc.conquered(),
                    game.getOccupationForce(oc.attacker()) - 1);
        }
        if( shouldFortify(orderTypeList, game)){
            Country strongestCountry = AiUtils.findStrongestPossession(_me, game);
            int numberTroopsToMove = game.getOccupationForce(strongestCountry) - 1;
            List<Country> neighbors = game.findAlliedNeighbors(strongestCountry);
            Country destination = RandomUtils.pickRandomElement(neighbors);
            return new Fortify(adjutant, strongestCountry, destination, numberTroopsToMove);
        }
        if( orderTypeList.contains(OrderType.DrawCard)){
            _conqueredOne = false;
            return new DrawCard(adjutant);
        }
        _conqueredOne = false;
        return new EndTurn(adjutant);
    }

    private boolean shouldFortify(List<OrderType> orderTypeList, Game game){
        if( orderTypeList.contains(OrderType.Fortify)){
            Country strongestCountry = AiUtils.findStrongestPossession(_me, game);
            if( game.findEnemyNeighbors(strongestCountry).size() == 0 ){
                return game.getOccupationForce(strongestCountry) > 1;
            }
        }
        return false;
    }

    @Override
    public Player getPlayer() {
        return _me;
    }

}
