package ojplg.skir.ai;

import ojplg.skir.card.CardSet;
import ojplg.skir.map.Country;
import ojplg.skir.map.MapUtils;
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
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;
import ojplg.skir.state.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This AI tries to hold on to countries already owned.
 */
public class Grumpy implements AutomatedPlayer {

    private final Player _me;
    private final Set<Country> _owned = new HashSet<>();

    public Grumpy(Player player){
        _me = player;
        player.setDisplayName("Grumpy");
    }

    @Override
    public Object getIdentification() {
        return "AI: Grumpy";
    }

    @Override
    public void initialize(Game game) {
        _owned.addAll(game.findOccupiedCountries(_me));
    }

    @Override
    public Order generateOrder(Adjutant adjutant, Game game) {
        List<OrderType> allowableOrders = adjutant.allowableOrders();
        if( allowableOrders.contains(OrderType.ExchangeCardSet)){
            return new ExchangeCardSet(adjutant, CardSet.findTradeableSet(game.getPlayerHoldings(_me).getCards()));
        }
        if( allowableOrders.contains(OrderType.ClaimArmies)){
            return new ClaimArmies(adjutant);
        }
        if( allowableOrders.contains(OrderType.PlaceArmy) ){
            return doPlacement(adjutant, game);
        }
        if( allowableOrders.contains(OrderType.Attack)){
            return doAttack(adjutant, game);
        }
        if( allowableOrders.contains(OrderType.Occupy)){
            return doOccupy(adjutant, game);
        }
        if (allowableOrders.contains(OrderType.DrawCard)){
            return new DrawCard(adjutant);
        }
        return new EndTurn(adjutant);
    }

    private Occupy doOccupy(Adjutant adjutant, Game game){
        OccupationConstraints constraints = adjutant.getOccupationConstraints();
        int existingForce = game.getOccupationForce(constraints.attacker());
        _owned.add(constraints.conquered());
        return new Occupy(adjutant, constraints.attacker(), constraints.conquered(), existingForce - 1);
    }

    private Order doAttack(Adjutant adjutant, Game game){
        List<Country> missingPossessions = findMissingCountries(game);
        for(Country missing : missingPossessions){
            List<Country> path = findShortestPathTo(missing, game);
            Country start = path.get(0);
            int force = game.getOccupationForce(start);
            if( force > 1 ){
                return new Attack(adjutant, start, path.get(1), Math.min(Constants.MAXIMUM_ATTACKER_DICE, force - 1));
            }
        }
        return new EndAttacks(adjutant);
    }

    private PlaceArmy doPlacement(Adjutant adjutant, Game game){
        List<Country> missingPossessions = findMissingCountries(game);
        if( missingPossessions.isEmpty()){
            Country weakestCountry = AiUtils.findWeakestPossession(_me, game);
            return new PlaceArmy(adjutant, weakestCountry);
        }
        List<List<Country>> paths = new ArrayList<>();
        for(Country missing : missingPossessions){
            paths.add(findShortestPathTo(missing, game));
        }
        List<Country> shortestPath = shortestPath(paths);
        return new PlaceArmy(adjutant, shortestPath.get(0), game.getPlayerHoldings(_me).reserveCount());
    }

    private List<Country> shortestPath(List<List<Country>> paths){
        return paths.stream().min((l1,l2) -> l1.size() - l2.size()).get();
    }

    private List<Country> findShortestPathTo(Country destination, Game game){
        List<List<Country>> paths = new ArrayList<>();
        for(Country country : game.findOccupiedCountries(_me)){
            List<Country> path = MapUtils.findShortestPath(game.getMap(), country, destination);
            paths.add(path);
        }
        return shortestPath(paths);
    }

    private List<Country> findMissingCountries(Game game){
        List<Country> currentPossessions = game.findOccupiedCountries(_me);
        return _owned.stream()
                .filter(c -> ! currentPossessions.contains(c))
                .collect(Collectors.toList());
    }

    @Override
    public Player getPlayer() {
        return _me;
    }
}
