package ojplg.skir.ai;

import ojplg.skir.card.CardSet;
import ojplg.skir.map.Continent;
import ojplg.skir.map.Country;
import ojplg.skir.map.MapUtils;
import ojplg.skir.play.orders.*;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;
import ojplg.skir.utils.ListUtils;
import ojplg.skir.utils.RatioDistributor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Tuney implements AutomatedPlayer {

    private static final Logger _log = LogManager.getLogger(Tuney.class);

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
    private static final String BorderCountryAndContinentBorderAndOwnedPlacementKey = "BorderCountryAndContinentBorderAndOwnedPlacementKey";
    private static final String BordersEnemyOwnedContinentPlacementKey = "BordersEnemyOwnedContinentPlacementKey";
    private static final String InStrongestUnownedContinentPlacementKey = "InStrongestUnownedContinentPlacementKey";
    private static final String BordersGoalCountryPlacementKey = "BordersGoalCountryPlacementKey";

    private static final String GoalContinentArmyPercentage = "GoalContinentArmyPercentage";
    private static final String GoalContinentCountryPercentage = "GoalContinentCountryPercentage";

    private static final String TargetInGoalContinentAttackKey = "TargetInBestGoalContinentAttackKey";
    private static final String TargetInBestGoalContinentAttackKey = "TargetInGoalContinentAttackKey";
    private static final String AttackerArmyPercentageTestAttackKey = "AttackerArmyPercentageTestAttackKey";
    private static final String AttackerArmyPercentageApplicationAttackKey = "AttackerArmyPercentageApplicationAttackKey";
    private static final String MinimumAttackScoreAttackKey = "MinimumAttackScoreAttackKey";
    private static final String PostCardMinimumAttackScoreAttackKey = "PostCardMinimumAttackScoreAttackKey";
    private static final String BustEnemyContinentAttackKey = "BustEnemyContinentAttackKey";


    private final Map<String,Double> _tunings;
    private final Player _me;

    private Map<Country, Integer> _placementsToMake = null;

    public static List<String> tuningKeys(){
        return Collections.unmodifiableList(Arrays.asList(
                BorderCountryPlacementKey,
                ContinentalBorderPlacementKey,
                ContinentOwnedPlacementKey,
                ContinentBorderAndOwnedPlacementKey,
                LargestEnemyRatioApplicationPlacementKey,
                LargestEnemyRatioTestPlacementKey,
                TotalEnemyRatioApplicationPlacementKey,
                TotalEnemyRatioTestPlacementKey,
                BordersEnemyOwnedContinentPlacementKey,
                NumberEnemyCountriesRatioApplicationPlacementKey,
                NumberEnemyCountriesRatioTestPlacementKey,
                GoalCountryNeighborPlacementKey,
                BorderCountryAndContinentBorderAndOwnedPlacementKey,
                TargetInBestGoalContinentAttackKey,
                AttackerArmyPercentageTestAttackKey,
                AttackerArmyPercentageApplicationAttackKey,
                MinimumAttackScoreAttackKey,
                PostCardMinimumAttackScoreAttackKey));
    }

    public static Map<String, Double> presetTunings(){

        Map<String, Double> map = new HashMap<>();

        // placement tunings
        map.put(BorderCountryPlacementKey, 0.9999);
        map.put(ContinentalBorderPlacementKey, 0.55);
        map.put(ContinentOwnedPlacementKey, 0.4);
        map.put(ContinentBorderAndOwnedPlacementKey, 0.4);
        map.put(LargestEnemyRatioApplicationPlacementKey, 0.6);
        map.put(LargestEnemyRatioTestPlacementKey, 0.6);
        map.put(TotalEnemyRatioApplicationPlacementKey, 0.6);
        map.put(TotalEnemyRatioTestPlacementKey, 0.6);
        map.put(NumberEnemyCountriesRatioApplicationPlacementKey, 0.5);
        map.put(NumberEnemyCountriesRatioTestPlacementKey, 0.5);
        map.put(GoalCountryNeighborPlacementKey, 0.999);
        map.put(BorderCountryAndContinentBorderAndOwnedPlacementKey, 0.9);
        map.put(BordersEnemyOwnedContinentPlacementKey, 0.8);
        map.put(InStrongestUnownedContinentPlacementKey, 0.8);
        map.put(BordersGoalCountryPlacementKey, 0.75);

        map.put(TargetInBestGoalContinentAttackKey, 0.8);
        map.put(TargetInGoalContinentAttackKey, 0.7);
        map.put(AttackerArmyPercentageTestAttackKey, 0.95);
        map.put(AttackerArmyPercentageApplicationAttackKey, 0.67);
        map.put(MinimumAttackScoreAttackKey, 0.05);
        map.put(PostCardMinimumAttackScoreAttackKey, 0.19);
        map.put(BustEnemyContinentAttackKey, 0.85);

        map.put(GoalContinentArmyPercentage, 0.65);
        map.put(GoalContinentCountryPercentage, 0.65);

        return map;
    }

    public Tuney(Player player, Map<String,Double> tunings, String name){
        player.setDisplayName(name);
        _me = player;
        Map<String,Double> tuningsCopy = new HashMap<>();
        tuningsCopy.putAll(tunings);
        _tunings = Collections.unmodifiableMap(tunings);
    }

    @Override
    public void initialize(Game game) {
    }

    @Override
    public Order generateOrder(Adjutant adjutant, Game game) {
        List<OrderType> allowableOrders = adjutant.allowableOrders();
        if( allowableOrders.contains(OrderType.ExchangeCardSet)){
            return new ExchangeCardSet(adjutant, CardSet.findTradeableSet(_me.getCards()));
        }
        if( allowableOrders.contains(OrderType.ClaimArmies)){
            return new ClaimArmies(adjutant);
        }
        if( allowableOrders.contains(OrderType.PlaceArmy) ){
            return generatePlaceArmyOrder(adjutant, game);
        }
        if( allowableOrders.contains(OrderType.Attack)){
            return generateAttackOrder(adjutant, game);
        }
        if( allowableOrders.contains(OrderType.Occupy)){
            return doOccupy(adjutant, game);
        }
        if (allowableOrders.contains(OrderType.Fortify)){
            Fortify fortify = maybeFortify(adjutant, game);
            if (fortify != null){
                return fortify;
            }
        }
        if (allowableOrders.contains(OrderType.DrawCard)){
            return new DrawCard(adjutant);
        }
        return new EndTurn(adjutant);
    }

    @Override
    public Player getPlayer() {
        return _me;
    }

    @Override
    public Object getIdentification() {
        return _me.getDisplayName();
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

    private Occupy doOccupy(Adjutant adjutant, Game game){
        OccupationConstraints constraints = adjutant.getOccupationConstraints();

        int existingForce = game.getOccupationForce(constraints.attacker());
        List<Country> enemies = game.findEnemyNeighbors(constraints.attacker());
        if( enemies.size() > 1 ){
            return new Occupy(adjutant, constraints.attacker(), constraints.conquered(), existingForce / 2);
        } else {
            return new Occupy(adjutant, constraints.attacker(), constraints.conquered(), existingForce - 1);
        }
    }

    private Fortify maybeFortify(Adjutant adjutant, Game game){
        List<Country> interiors = game.findInteriorCountries(_me);
        int numberToMove = 0;
        Country sourceCountry = null;
        for(Country interior : interiors){
            int excessForces = game.getOccupationForce(interior);
            if (excessForces> numberToMove){
                numberToMove = excessForces -1;
                sourceCountry = interior;
            }
        }
        Country destinationCountry = null;
        int numberInDestination = 1;
        if( numberToMove > 0 && sourceCountry != null){
            List<Country> allies = game.findAlliedNeighbors(sourceCountry);
            for(Country ally : allies){
                if( game.findEnemyNeighbors(ally).size() > 0){
                    destinationCountry = ally;
                }
            }
            if (destinationCountry == null ){
                for(Country ally : allies ){
                    int allyArmyCount = game.getOccupationForce(ally);
                    if ( allyArmyCount > numberInDestination){
                        numberInDestination = allyArmyCount;
                        destinationCountry = ally;
                    }
                }
            }
        }
        if ( destinationCountry != null){
            return new Fortify(adjutant, sourceCountry, destinationCountry, numberToMove);
        }
        return null;
    }

    private Map<Country,Integer> computePlacements(Game game){
        Map<Country, Double> ratios = new HashMap<>();
        int countryCount = game.findOccupiedCountries(_me).size();
        if( _me.reserveCount() <= 5 ){
            countryCount = 1;
        } else if ( _me.reserveCount() <= 10 ){
            countryCount = 2;
        }
        game.findOccupiedCountries(_me).forEach( c ->
        {
            ratios.put(c, computePlacementScore(c, game));
        });
        return RatioDistributor.distribute(ratios,_me.reserveCount(), countryCount);
    }

    public double computePlacementScore(Country country, Game game){

        Continent continent = Continent.find(country);
        Continent strongestUnownedContinent = AiUtils.findStrongestUnownedContinent(_me, game);
        boolean isBorderCountry = AiUtils.isBorderCountry(_me, game, country);
        boolean isContinentalBorder = game.isContinentalBorder(country);
        boolean isOwnedContinent = game.isContinentOwner(_me, continent);
        boolean inStrongestUnownedContinent = continent.equals(strongestUnownedContinent);

        List<Country> enemyNeighbors = game.findEnemyNeighbors(country);
        List<Country> allNeighbors = game.findAllNeighbors(country);
        List<Country> goalCountries = chooseGoalCountries(game);

        boolean bordersGoalCountry = ListUtils.hasIntersection(game.findAllNeighbors(country), goalCountries);

        List<Continent> enemyContinents = AiUtils.enemyOwnedContinents(_me, game);
        boolean bordersEnemyOwnedContinent = MapUtils.bordersContinent(game.getMap(), country, enemyContinents);

        int currentOccupationStrength = game.getOccupationForce(country);
        int totalNeighbors = allNeighbors.size();
        int totalEnemyForces = AiUtils.computeTotalOccupyingForces(enemyNeighbors, game);
        int largestEnemyForce = AiUtils.highestOccupyingForce(enemyNeighbors, game);

        double score = 1;

        score = booleanAdjust(score, isBorderCountry, BorderCountryPlacementKey);
        score = booleanAdjust(score, isContinentalBorder,  ContinentalBorderPlacementKey);
        score = booleanAdjust(score, isOwnedContinent, ContinentOwnedPlacementKey);
        score = booleanAdjust(score, bordersEnemyOwnedContinent, BordersEnemyOwnedContinentPlacementKey);
        score = booleanAdjust(score, inStrongestUnownedContinent, InStrongestUnownedContinentPlacementKey);
        score = booleanAdjust(score, bordersGoalCountry, BordersGoalCountryPlacementKey);

        /*
        score = booleanAdjust(score, CollectionUtils.containsAny(goalCountries, enemyNeighbors),
                GoalCountryNeighborPlacementKey);
        score = booleanAdjust(score, isContinentalBorder && isOwnedContinent, ContinentBorderAndOwnedPlacementKey);
        score = booleanAdjust(score, isContinentalBorder && isOwnedContinent && isBorderCountry,
                BorderCountryAndContinentBorderAndOwnedPlacementKey);
        score = ratioAdjust(score, currentOccupationStrength, totalEnemyForces,
                TotalEnemyRatioTestPlacementKey, TotalEnemyRatioApplicationPlacementKey);
        score = ratioAdjust(score, currentOccupationStrength, largestEnemyForce,
                LargestEnemyRatioTestPlacementKey, LargestEnemyRatioApplicationPlacementKey);
        score = ratioAdjust(score, enemyNeighbors.size(), totalNeighbors,
                NumberEnemyCountriesRatioTestPlacementKey, NumberEnemyCountriesRatioApplicationPlacementKey);
        */

        return score;
    }

    public double computePossibleAttackScore(PossibleAttack attack, Game game){
        double score = 1;

        Country target = attack.getDefender();

        List<Continent> goalContinents = AiUtils.possibleGoalContinents(_me, game,
                tunedValue(GoalContinentArmyPercentage), tunedValue(GoalContinentCountryPercentage));

        Continent bestGoalContinent = AiUtils.findStrongestUnownedContinent(_me, game);
        if( bestGoalContinent != null ) {
            boolean targetInBestGoalContinent = bestGoalContinent.contains(target);
            score = booleanAdjust(score, targetInBestGoalContinent, TargetInBestGoalContinentAttackKey);
        } else {
            score = booleanAdjust(score, goalContinents.contains(Continent.find(target)), TargetInGoalContinentAttackKey);
        }

        boolean targetInEnemyOwnedContinent = false;
        for(Continent continent : AiUtils.enemyOwnedContinents(_me, game)){
            if ( Continent.find(target).equals(continent) ){
                targetInEnemyOwnedContinent = true;
            }
        }

        score = booleanAdjust(score, targetInEnemyOwnedContinent, BustEnemyContinentAttackKey);

        double attackerArmyPercentage = attack.getAttackerArmyPercentage();
        score = score * attackerArmyPercentage;

        return score;
    }

    private Order generateAttackOrder(Adjutant adjutant, Game game){
        List<PossibleAttack> majorAdvantages = AiUtils.findAdvantageousAttacks(_me, game, 15);
        if( majorAdvantages.size() > 0){
            PossibleAttack possibleAttack = majorAdvantages.get(0);
            return new Attack(adjutant, possibleAttack.getAttacker(), possibleAttack.getDefender(),
                    possibleAttack.maximumAttackingDice());
        }

        List<PossibleAttack> possibleAttacks = AiUtils.findAllPossibleAttacks(_me, game);

        double bestAttackScore = Double.MIN_VALUE;
        PossibleAttack bestPossibleAttack = null;


        for(PossibleAttack possibleAttack : possibleAttacks){
            double score = computePossibleAttackScore(possibleAttack, game);
            String minimumAttackKey = adjutant.hasConqueredCountry() ?
                    PostCardMinimumAttackScoreAttackKey : MinimumAttackScoreAttackKey;
            if( score > bestAttackScore && aboveTuningValue(score, minimumAttackKey)){
                bestAttackScore = score;
                bestPossibleAttack = possibleAttack;
            }
        }
        if( bestPossibleAttack != null ){
            _log.warn(_me + " Attacking! from " + bestPossibleAttack.getAttacker() + " to " + bestPossibleAttack.getDefender());
            return new Attack(adjutant, bestPossibleAttack.getAttacker(),
                    bestPossibleAttack.getDefender(), bestPossibleAttack.maximumAttackingDice());
        } else {
            return new EndAttacks(adjutant);
        }
    }

    private List<Country> chooseGoalCountries(Game game){

        Continent goalContinent = AiUtils.findStrongestUnownedContinent(_me, game);

        Set<Country> goals = new HashSet<>();
        if( goalContinent != null) {
            List continentalGoals = ListUtils.filter(goalContinent.getCountries(),
                    c -> !game.getOccupier(c).equals(_me));
            goals.addAll(continentalGoals);
        }

        List<Continent> enemyOwnedContinents = AiUtils.enemyOwnedContinents(_me, game);
        List<Country> enemyContinentBorders = enemyOwnedContinents.stream()
                .map(c -> game.findContinentalBorders(c))
                .reduce(new ArrayList<>(), (l1,l2) -> ListUtils.concat(l1, l2));
        List<Country> enemyBorders = AiUtils.findEnemyBorders(_me, game);
        Collection<Country> borderingEnemyContinentsCountries =
                CollectionUtils.intersection(enemyContinentBorders, enemyBorders);

        goals.addAll(borderingEnemyContinentsCountries);
        return new ArrayList<>(goals);
    }

    /**
     * Adjusts by scaling by amount if ratio is better than scale key or 1-amount otherwise.
     */
    private double ratioAdjust(double current, double ratio, String testKey, String scaleKey){
        double scale = _tunings.get(scaleKey);
        double test = _tunings.get(testKey);
        return test < ratio ? scale * current : (1-scale) * current;
    }

    private double ratioAdjust(double current, int numerator, int denominator, String testKey, String scaleKey){
        if( numerator == 0 && denominator == 0){
            return current;
        }
        double ratio = numerator/(denominator + numerator);
        return ratioAdjust(current, ratio, testKey, scaleKey);
    }

    /**
     * Adjusts downward by amount if test is true, or by 1 - amount if false.
     */
    private double booleanAdjust(double current, boolean test, String key){
        double scale = _tunings.get(key);
        return test ? scale * current : (1-scale) * current;
    }

    private double tunedValue(String key){
        return _tunings.get(key);
    }

    private boolean aboveTuningValue(double score, String key){
        return score > _tunings.get(key);
    }
}
