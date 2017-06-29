package ojplg.skir.ai;

import ojplg.skir.card.CardSet;
import ojplg.skir.map.Continent;
import ojplg.skir.map.Country;
import ojplg.skir.map.MapUtils;
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
import ojplg.skir.utils.ListUtils;
import ojplg.skir.utils.RatioDistributor;
import ojplg.skir.utils.Tuple;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Tuney implements AutomatedPlayer {

    private static final Logger _log = LogManager.getLogger(Tuney.class);

    private static final String BorderCountryPlacementKey = "BorderCountryPlacementKey";
    private static final String ContinentalBorderPlacementKey = "ContinentalBorderPlacementKey";
    private static final String ContinentOwnedPlacementKey = "ContinentOwnedPlacementKey";
    private static final String BordersEnemyOwnedContinentPlacementKey = "BordersEnemyOwnedContinentPlacementKey";
    private static final String InStrongestUnownedContinentPlacementKey = "InStrongestUnownedContinentPlacementKey";
    private static final String BordersGoalCountryPlacementKey = "BordersGoalCountryPlacementKey";
    private static final String FirstCountryMinimumPlacementKey = "FirstCountryMinimumPlacementKey";
    private static final String SecondCountryMinimumPlacementKey = "SecondCountryMinimumPlacementKey";
    private static final String ThirdCountryMinimumPlacementKey = "ThirdCountryMinimumPlacementKey";

    private static final String GoalContinentArmyPercentage = "GoalContinentArmyPercentage";
    private static final String GoalContinentCountryPercentage = "GoalContinentCountryPercentage";

    private static final String TargetInGoalContinentAttackKey = "TargetInBestGoalContinentAttackKey";
    private static final String TargetInBestGoalContinentAttackKey = "TargetInGoalContinentAttackKey";
    private static final String MinimumAttackScoreAttackKey = "MinimumAttackScoreAttackKey";
    private static final String PostCardMinimumAttackScoreAttackKey = "PostCardMinimumAttackScoreAttackKey";
    private static final String BustEnemyContinentAttackKey = "BustEnemyContinentAttackKey";
    private static final String MajorAdvantageAttackKey = "MajorAdvantageAttackKey";
    private static final String WeakOpponentAttackKey = "WeakOpponentAttackKey";
    private static final String ContinentOwnershipCloseAttackKey = "ContinentOwnershipCloseAttackKey";
    private static final String ContinentCloseCountryPercentAttackKey = "ContinentCloseCountryPercentAttackKey";
    private static final String ContinentCloseArmyPercentAttackKey = "ContinentCloseArmyPercentAttackKey";

    private static final String WeakBorderFortifyKey = "WeakBorderFortifyKey";

    private final Map<String,Double> _tunings;
    private final Player _me;
    private final List<Integer> _placementMinimums;
    private final boolean _useAdditiveAttackScoring;

    private Map<Country, Integer> _placementsToMake = null;

    public static Map<String, Double> presetTunings(){

        Map<String, Double> map = new HashMap<>();

        // placement tunings
        map.put(BorderCountryPlacementKey, 0.9999);
        map.put(ContinentalBorderPlacementKey, 0.55);
        map.put(ContinentOwnedPlacementKey, 0.4);
        map.put(BordersEnemyOwnedContinentPlacementKey, 0.8);
        map.put(InStrongestUnownedContinentPlacementKey, 0.8);
        map.put(BordersGoalCountryPlacementKey, 0.75);

        map.put(FirstCountryMinimumPlacementKey, 0.5);
        map.put(SecondCountryMinimumPlacementKey, 0.2);
        map.put(ThirdCountryMinimumPlacementKey, 0.1);

        map.put(TargetInBestGoalContinentAttackKey, 0.8);
        map.put(TargetInGoalContinentAttackKey, 0.7);
        map.put(BustEnemyContinentAttackKey, 0.85);
        map.put(MajorAdvantageAttackKey, 0.10);
        map.put(WeakOpponentAttackKey, 0.9);
        map.put(ContinentOwnershipCloseAttackKey, 0.8);
        map.put(ContinentCloseArmyPercentAttackKey, 0.9);
        map.put(ContinentCloseCountryPercentAttackKey, 0.9);

        map.put(MinimumAttackScoreAttackKey, 0.04);
        map.put(PostCardMinimumAttackScoreAttackKey, 0.15);

        map.put(GoalContinentArmyPercentage, 0.65);
        map.put(GoalContinentCountryPercentage, 0.65);

        map.put(WeakBorderFortifyKey, 0.9);

        return map;
    }

    public Tuney(Player player, Map<String,Double> tunings, String name, boolean useAdditiveAttackScoring){
        player.setDisplayName(name);
        _me = player;
        _tunings = Collections.unmodifiableMap(tunings);
        _placementMinimums = Arrays.asList(
                scaledTunedValue(FirstCountryMinimumPlacementKey, 10),
                scaledTunedValue(SecondCountryMinimumPlacementKey, 10),
                scaledTunedValue(ThirdCountryMinimumPlacementKey, 10));
        _useAdditiveAttackScoring = useAdditiveAttackScoring;
    }

    public Tuney(Player player, Map<String,Double> tunings, String name){
        player.setDisplayName(name);
        _me = player;
        _tunings = Collections.unmodifiableMap(tunings);
        _placementMinimums = Arrays.asList(
                scaledTunedValue(FirstCountryMinimumPlacementKey, 10),
                scaledTunedValue(SecondCountryMinimumPlacementKey, 10),
                scaledTunedValue(ThirdCountryMinimumPlacementKey, 10));
        _useAdditiveAttackScoring = false;
    }

    @Override
    public void initialize(Game game) {
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
        // TODO: this should have some configuration or better smarts

        OccupationConstraints constraints = adjutant.getOccupationConstraints();

        int existingForce = game.getOccupationForce(constraints.attacker());
        List<Country> enemies = game.findEnemyNeighbors(constraints.attacker());
        if( enemies.size() > 1 ){
            double attackerPlacementScore = computePlacementScore(constraints.attacker(), game);
            double conqueredPlacementScore = computePlacementScore(constraints.conquered(), game);
            int armiesToDivide = constraints.maximumOccupationForce();
            double toConqueredCountry = armiesToDivide *
                    (conqueredPlacementScore/(attackerPlacementScore+conqueredPlacementScore));
            int occupationForce = Math.min((int) Math.floor(toConqueredCountry), constraints.maximumOccupationForce());
            occupationForce = Math.max(occupationForce, constraints.minimumOccupationForce());

            //return new Occupy(adjutant, constraints.attacker(), constraints.conquered(), existingForce / 2);
            return new Occupy(adjutant, constraints.attacker(), constraints.conquered(), occupationForce);
        } else {
            return new Occupy(adjutant, constraints.attacker(), constraints.conquered(), existingForce - 1);
        }
    }

    private Map<Country, Double> findWeakBorders(Game game){
        List<Country> borders = game.findBorderCountries(_me);
        Map<Country,Double> destinations = new HashMap<>();
        Country weakest = borders.get(0);
        Double worstRatio = Double.MAX_VALUE;
        for(Country country : borders){
            Double ratio = computeOpposingStrengthRatio(game, country);
            if ( ratio < worstRatio){
                weakest = country;
                worstRatio = ratio;
            }
            if ( ratio < tunedValue(WeakBorderFortifyKey) ){
                destinations.put(country, ratio);
            }
        }
        if( destinations.size() == 0){
            destinations.put(weakest, worstRatio);
        }
        return destinations;
    }

    /**
     * My strength over opposition strength ... lower is worse for me.
     */
    private Double computeOpposingStrengthRatio(Game game, Country country){
        List<Country> enemyNeighbors = game.findEnemyNeighbors(country);
        int opposingStrength = AiUtils.findStrengthOfCountries(game, enemyNeighbors);
        int strength = game.getOccupationForce(country);
        return (double) strength / (double) opposingStrength;
    }

    private Tuple<Country, Double> computeFortificationScore(Game game, Country source,
                                                             Country destination, double enemyRatio){
        List<Country> path = MapUtils.findShortestPath(game.getMap(), source, destination);
        // we must only allow legal fortifications!
        for(Country country : path){
            if( ! game.getOccupier(country).equals(_me)){
                return null;
            }
        }
        int movesAway = path.size() - 1;
        Country moveTo = path.get(1);
        // we want more desirable fortications to be LARGER
        // so, the inverse of the enemy ratio divided by the number
        // of moves away
        double score = (1.0/enemyRatio)/movesAway;
        return new Tuple<>(moveTo, score);
    }

    private Fortify maybeFortify(Adjutant adjutant, Game game){
        List<Country> possibleSources = AiUtils.findInteriorCountriesWithExcessArmies(game, _me);
        if( possibleSources.size() == 0){
            return null;
        }
        Map<Country,Double> possibleDestinations = findWeakBorders(game);
        double bestFortifyScore = 0.0;
        Fortify fortify = null;
        for(Country source : possibleSources){
            for(Map.Entry<Country, Double> entry : possibleDestinations.entrySet()) {
                Country destination = entry.getKey();
                Double enemyRatio = entry.getValue();
                Tuple<Country, Double> scoreTuple = computeFortificationScore(game, source, destination, enemyRatio);
                if( scoreTuple == null){
                    continue;
                }
                if(scoreTuple.getSecond() > bestFortifyScore){
                    bestFortifyScore = scoreTuple.getSecond();
                    int armyCount = game.getOccupationForce(source) - 1;
                    fortify = new Fortify(adjutant, source, scoreTuple.getFirst(), armyCount);
                }
            }
        }
        return fortify;
    }

    private Map<Country,Integer> computePlacements(Game game){
        Map<Country, Double> ratios = new HashMap<>();
        game.findOccupiedCountries(_me).forEach( c ->
                ratios.put(c, computePlacementScore(c, game)));
        return RatioDistributor.distribute(ratios,game.getPlayerHoldings(_me).reserveCount(), _placementMinimums);
    }

    public double computePlacementScore(Country country, Game game){

        Continent continent = Continent.find(country);
        Continent strongestUnownedContinent = AiUtils.findStrongestUnownedContinent(_me, game);
        boolean isBorderCountry = AiUtils.isBorderCountry(_me, game, country);
        boolean isContinentalBorder = game.isContinentalBorder(country);
        boolean isOwnedContinent = game.isContinentOwner(_me, continent);
        boolean inStrongestUnownedContinent = continent.equals(strongestUnownedContinent);

        List<Country> goalCountries = chooseGoalCountries(game);

        boolean bordersGoalCountry = ListUtils.hasIntersection(game.findAllNeighbors(country), goalCountries);

        List<Continent> enemyContinents = AiUtils.enemyOwnedContinents(_me, game);
        boolean bordersEnemyOwnedContinent = MapUtils.bordersContinent(game.getMap(), country, enemyContinents);

        double score = 1;

        score = booleanAdjust(score, isBorderCountry, BorderCountryPlacementKey);
        score = booleanAdjust(score, isContinentalBorder,  ContinentalBorderPlacementKey);
        score = booleanAdjust(score, isOwnedContinent, ContinentOwnedPlacementKey);
        score = booleanAdjust(score, bordersEnemyOwnedContinent, BordersEnemyOwnedContinentPlacementKey);
        score = booleanAdjust(score, inStrongestUnownedContinent, InStrongestUnownedContinentPlacementKey);
        score = booleanAdjust(score, bordersGoalCountry, BordersGoalCountryPlacementKey);

        return score;
    }

    public double computeAdditiveAttackScore(PossibleAttack attack, Game game){

        Country attacker = attack.getAttacker();
        int attackerStrength = game.getOccupationForce(attacker);

        Country target = attack.getDefender();
        Continent targetContinent = Continent.find(target);
        int targetStrength = game.getOccupationForce(target);

        Player opponent = game.getOccupier(target);
        int totalOpponentStrength = AiUtils.findAllPlayerArmies(game, opponent);
        int opponentCardCount = game.getPlayerHoldings(opponent).getCards().size();

        Set<Country> bloc = AiUtils.findContiguousOwnedCountries(game, target);
        int blocStrength = AiUtils.findStrengthOfCountries(game, bloc);

        List<Continent> enemyOwnedContinents = AiUtils.enemyOwnedContinents(_me, game);
        Continent bestGoalContinent = AiUtils.findStrongestUnownedContinent(_me, game);
        List<Continent> goalContinents = AiUtils.possibleGoalContinents(_me, game,
                tunedValue(GoalContinentArmyPercentage), tunedValue(GoalContinentCountryPercentage));
        boolean targetInBestGoalContinent = bestGoalContinent != null && bestGoalContinent.contains(target);
        List<Country> myOtherCountriesBorderingTarget = new ArrayList<>();
        int myOtherBorderingForces = 0;
        for(Country country : game.findEnemyNeighbors(target)){
            if( _me.equals(game.getOccupier(country))){
                myOtherBorderingForces += game.getOccupationForce(country);
                myOtherCountriesBorderingTarget.add(country);
            }
        }

        // In best continent - check
        // In goal continent - check
        // In opponent owned continent (modified by continent size) - check
        // Close to owning continent - check
        // Close to eliminating player (modified by number of cards) - check
        // Good ratio surrounding country - check
        // Good ratio attacker to defender - check

        double score = 0;
        if ( targetInBestGoalContinent ){
            score += tunedValue(TargetInGoalContinentAttackKey);
        }
        if ( goalContinents.contains(targetContinent)){
            score += tunedValue(TargetInGoalContinentAttackKey);
        }
        if( enemyOwnedContinents.contains(targetContinent)){
            double continentWorth = targetContinent.getBonus() / Continent.MAXIMUM_CONTINENT_VALUE;
            score += tunedValue(BustEnemyContinentAttackKey) * continentWorth;
        }
        if( closeToOwning(targetContinent, game)){
            score += tunedValue(ContinentOwnershipCloseAttackKey);
        }
        if( blocStrength == totalOpponentStrength &&
               blocStrength <= attackerStrength){
            double cardModifier = opponentCardCount / 5;
            score += tunedValue(WeakOpponentAttackKey) * cardModifier;
        }
        if( myOtherBorderingForces > 0 ){
            score += targetStrength / (targetStrength + myOtherBorderingForces);
        }

        boolean weakOpponent = blocStrength < attack.getAttackerForce();
        score = booleanAdjust(score, weakOpponent, WeakOpponentAttackKey);

        return score;
    }

    public boolean closeToOwning(Continent continent, Game game){
        int unownedCountryCount = AiUtils.unownedCountryCount(_me, game, continent);
        if ( unownedCountryCount < 2){
            return true;
        }
        double ownedCountriesPercentage = AiUtils.continentalCountryPercentage(_me, game, continent);
        if (ownedCountriesPercentage > tunedValue(ContinentCloseCountryPercentAttackKey)){
            return true;
        }
        double ownedArmyPercentage = AiUtils.continentalArmyPercentage(_me, game, continent);
        if (ownedArmyPercentage > tunedValue(ContinentCloseArmyPercentAttackKey)){
            return true;
        }
        return false;
    }

    public double computePossibleAttackScore(PossibleAttack attack, Game game){

        Country target = attack.getDefender();
        Set<Country> bloc = AiUtils.findContiguousOwnedCountries(game, target);
        int blocStrength = AiUtils.findStrengthOfCountries(game, bloc);

        Continent bestGoalContinent = AiUtils.findStrongestUnownedContinent(_me, game);
        List<Continent> goalContinents = AiUtils.possibleGoalContinents(_me, game,
                tunedValue(GoalContinentArmyPercentage), tunedValue(GoalContinentCountryPercentage));

        double score = 1;
        // 1
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

        // 2
        score = booleanAdjust(score, targetInEnemyOwnedContinent, BustEnemyContinentAttackKey);

        // 3
        double attackerArmyPercentage = attack.getAttackerArmyPercentage();
        score = score * attackerArmyPercentage;

        // 4
        boolean weakOpponent = blocStrength < attack.getAttackerForce();
        score = booleanAdjust(score, weakOpponent, WeakOpponentAttackKey);

        return score;
    }

    private int scaledTunedValue(String tuning, int scale){
        return (int) Math.round(scale * tunedValue(tuning));
    }

    private Order generateAttackOrder(Adjutant adjutant, Game game){
        int majorAdvantageCutoff = scaledTunedValue(MajorAdvantageAttackKey, 100);
        List<PossibleAttack> majorAdvantages = AiUtils.findAdvantageousAttacks(_me, game, majorAdvantageCutoff);
        if( majorAdvantages.size() > 0){
            PossibleAttack possibleAttack = majorAdvantages.get(0);
            return new Attack(adjutant, possibleAttack.getAttacker(), possibleAttack.getDefender(),
                    possibleAttack.maximumAttackingDice());
        }

        List<PossibleAttack> possibleAttacks = AiUtils.findAllPossibleAttacks(_me, game);

        double bestAttackScore = Double.MIN_VALUE;
        PossibleAttack bestPossibleAttack = null;

        for(PossibleAttack possibleAttack : possibleAttacks){
            String minimumAttackKey = adjutant.hasConqueredCountry() ?
                    PostCardMinimumAttackScoreAttackKey : MinimumAttackScoreAttackKey;
            if( _useAdditiveAttackScoring ){
                double score = computeAdditiveAttackScore(possibleAttack, game);
                double minimumScore = scaledTunedValue(minimumAttackKey, 10);
                if ( score > bestAttackScore && score > minimumScore){
                    bestAttackScore = score;
                    bestPossibleAttack = possibleAttack;
                }
            } else {
                double score = computePossibleAttackScore(possibleAttack, game);
                if( score > bestAttackScore && aboveTuningValue(score, minimumAttackKey)){
                    bestAttackScore = score;
                    bestPossibleAttack = possibleAttack;
                }
            }
        }
        if( bestPossibleAttack != null ){
            _log.debug(_me + " Attacking! from " + bestPossibleAttack.getAttacker() + " to " + bestPossibleAttack.getDefender());
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
                .map(game::findContinentalBorders)
                .reduce(new ArrayList<>(), ListUtils::concat);
        List<Country> enemyBorders = AiUtils.findEnemyBorders(_me, game);
        Collection<Country> borderingEnemyContinentsCountries =
                CollectionUtils.intersection(enemyContinentBorders, enemyBorders);

        goals.addAll(borderingEnemyContinentsCountries);
        return new ArrayList<>(goals);
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
