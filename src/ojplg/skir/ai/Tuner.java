package ojplg.skir.ai;

import ojplg.skir.map.Continent;
import ojplg.skir.map.Country;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.play.orders.PlaceArmy;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;
import ojplg.skir.utils.ListUtils;
import ojplg.skir.utils.RatioDistributor;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Tuner implements AutomatedPlayer {

    private static final String BorderCountryPlacementKey = "BorderCountryPlacementKey";
    private static final String ContinentalBorderPlacementKey = "ContinentalBorderPlacementKey";
    private static final String ContinentOwnedPlacementKey = "ContinentOwnedPlacementKey";
    private static final String ContinentBorderAndOwnedPlacementKey = "ContinentBorderAndOwnedPlacementKey";
    private static final String LargestEnemyRatioTestPlacementKey = "LargestEnemyRatioTestPlacementKey";
    private static final String LargestEnemyRatioApplicationPlacementKey = "LargestEnemyRatioApplicationPlacementKey";
    private static final String TotalEnemyRatioTestPlacementKey = "TotalEnemyRatioTestPlacementKey";
    private static final String TotalEnemyRatioApplicationPlacementKey = "TotalEnemyRatioApplicationPlacementKey";
    private static final String NumberEnemyCountriesRatioTestPlacementKey = "NumberEnemyCountriesRatioTestPlacementKey";
    private static final String NumberEnemyCountriesRatioApplicationPlacementKey = "NumberEnemyCountriesRatioApplicationPlacementKey";
    private static final String GoalCountryNeighborPlacementKey = "GoalCountryNeighborPlacementKey";

    private final Map<String,Float> _tunings;
    private final Player _me;

    private Map<Country, Integer> _placementsToMake = null;

    public Tuner(Player player, Map<String,Float> tunings){
        player.setDisplayName("Tuner");
        _me = player;
        Map<String,Float> tuningsCopy = new HashMap<>();
        tuningsCopy.putAll(tunings);
        _tunings = Collections.unmodifiableMap(tunings);
    }

    @Override
    public void initialize(Game game) {
    }

    @Override
    public Order generateOrder(Adjutant adjutant, Game game) {
        return null;
    }

    @Override
    public Player getPlayer() {
        return _me;
    }

    @Override
    public Object getIdentification() {
        return "Tuner";
    }

    private PlaceArmy generatePlaceArmyOrder(Adjutant adjutant, Game game){
        if( _placementsToMake == null){
            _placementsToMake = computePlacements(game);
        }
        Map.Entry<Country,Integer> entry = new ArrayList<>(_placementsToMake.entrySet()).get(0);
        PlaceArmy order = new PlaceArmy(adjutant, entry.getKey(), entry.getValue());
        _placementsToMake.remove(entry.getKey());
        if( _placementsToMake.size() == 0){
            _placementsToMake = null;
        }
        return order;
    }

    private Map<Country,Integer> computePlacements(Game game){
        Map<Country, Double> ratios = new HashMap<>();
        game.findOccupiedCountries(_me).forEach( c ->
        {
            ratios.put(c, computePlacementScore(c, game));
        });
        return RatioDistributor.distribute(ratios,_me.reserveCount());
    }

    private double computePlacementScore(Country country, Game game){

        Continent continent = Continent.find(country);
        boolean isContinentalBorder = game.isContinentalBorder(country);
        boolean isOwnedContinent = game.isContinentOwner(_me, continent);

        List<Country> alliedNeighbors = game.findAlliedNeighbors(country);
        List<Country> enemyNeighbors = game.findEnemyNeighbors(country);
        List<Country> allNeighbors = game.findAllNeighbors(country);
        List<Country> goalCountries = chooseGoalCountries(game);

        int currentOccupationStrength = game.getOccupationForce(country);
        int totalNeighbors = allNeighbors.size();
        int totalEnemyForces = AiUtils.computeTotalOccupyingForces(enemyNeighbors, game);
        int largestEnemyForce = AiUtils.highestOccupyingForce(enemyNeighbors, game);

        float score = 1;

        score = booleanAdjust(score, AiUtils.isBorderCountry(_me, game, country), BorderCountryPlacementKey);
        score = booleanAdjust(score, isContinentalBorder,  ContinentalBorderPlacementKey);
        score = booleanAdjust(score, isOwnedContinent, ContinentOwnedPlacementKey);
        score = booleanAdjust(score, isContinentalBorder && isOwnedContinent, ContinentBorderAndOwnedPlacementKey);
        score = ratioAdjust(score, currentOccupationStrength, totalEnemyForces,
                TotalEnemyRatioTestPlacementKey, TotalEnemyRatioApplicationPlacementKey);
        score = ratioAdjust(score, currentOccupationStrength, largestEnemyForce,
                LargestEnemyRatioTestPlacementKey, LargestEnemyRatioApplicationPlacementKey);
        score = ratioAdjust(score, enemyNeighbors.size(), totalNeighbors,
                NumberEnemyCountriesRatioTestPlacementKey, NumberEnemyCountriesRatioApplicationPlacementKey);
        score = booleanAdjust(score, CollectionUtils.containsAny(goalCountries, enemyNeighbors),
                GoalCountryNeighborPlacementKey);

        return score;
    }

    private List<Country> chooseGoalCountries(Game game){
        Continent goalContinent = AiUtils.findStrongestUnownedContinent(_me, game);
        List continentalGoals = ListUtils.filter(goalContinent.getCountries(),
                        c -> ! game.getOccupier(c).equals(_me));

        List<Continent> enemyOwnedContinents = AiUtils.enemyOwnedContinents(_me, game);
        List<Country> enemyContinentBorders = enemyOwnedContinents.stream()
                .map(c -> game.findContinentalBorders(c))
                .reduce(new ArrayList<>(), (l1,l2) -> ListUtils.concat(l1, l2));
        List<Country> enemyBorders = AiUtils.findEnemyBorders(_me, game);
        Collection<Country> borderingEnemyContinentsCountries =
                CollectionUtils.intersection(enemyContinentBorders, enemyBorders);

        Set<Country> goals = new HashSet<>();
        goals.addAll(continentalGoals);
        goals.addAll(borderingEnemyContinentsCountries);
        return new ArrayList<>(goals);
    }

    private float ratioAdjust(float current, int numerator, int denominator, String testKey, String scaleKey){
        float ratio = numerator/denominator;
        float scale = _tunings.get(scaleKey);
        float test = _tunings.get(testKey);
        return test < ratio ? scale * current : (1-scale) * current;
    }

    private float booleanAdjust(float current, boolean test, String key){
        float scale = _tunings.get(key);
        return test ? scale * current : (1-scale) * current;
    }
}
