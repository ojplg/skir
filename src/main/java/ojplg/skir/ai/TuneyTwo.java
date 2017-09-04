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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class TuneyTwo implements AutomatedPlayer {

    private static final Logger _log = LogManager.getLogger(TuneyTwo.class);

    private static final String BorderCountryPlacementKey = "BorderCountryPlacementKey";
    private static final String ContinentalBorderPlacementKey = "ContinentalBorderPlacementKey";
    private static final String ContinentOwnedPlacementKey = "ContinentOwnedPlacementKey";
    private static final String BordersEnemyOwnedContinentPlacementKey = "BordersEnemyOwnedContinentPlacementKey";
    private static final String InStrongestUnownedContinentPlacementKey = "InStrongestUnownedContinentPlacementKey";
    private static final String BordersGoalCountryPlacementKey = "BordersGoalCountryPlacementKey";
    private static final String FirstCountryMinimumPlacementKey = "FirstCountryMinimumPlacementKey";
    private static final String SecondCountryMinimumPlacementKey = "SecondCountryMinimumPlacementKey";
    private static final String ThirdCountryMinimumPlacementKey = "ThirdCountryMinimumPlacementKey";

    private static final String ContinentScoreAfrica = "ContinentScoreAfrica";
    private static final String ContinentScoreAsia = "ContinentScoreAsia";
    private static final String ContinentScoreAustralia = "ContinentScoreAustralia";
    private static final String ContinentScoreEurope = "ContinentScoreEurope";
    private static final String ContinentScoreNorthAmerica = "ContinentScoreNorthAmerica";
    private static final String ContinentScoreSouthAmerica = "ContinentScoreSouthAmerica";

    private static final String EnemyContinentScoreAfrica = "EnemyContinentScoreAfrica";
    private static final String EnemyContinentScoreAsia = "EnemyContinentScoreAsia";
    private static final String EnemyContinentScoreAustralia = "EnemyContinentScoreAustralia";
    private static final String EnemyContinentScoreEurope = "EnemyContinentScoreEurope";
    private static final String EnemyContinentScoreNorthAmerica = "EnemyContinentScoreNorthAmerica";
    private static final String EnemyContinentScoreSouthAmerica = "EnemyContinentScoreSouthAmerica";

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

    private Map<Country, Integer> _placementsToMake = null;

    public static Map<String, Double> presetTunings(){

        Map<String, Double> map = new HashMap<>();

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

        map.put(ContinentScoreAfrica, 0.7);
        map.put(ContinentScoreAsia, 0.1);
        map.put(ContinentScoreEurope, 0.3);
        map.put(ContinentScoreNorthAmerica, 0.35);
        map.put(ContinentScoreSouthAmerica, 0.8);
        map.put(ContinentScoreAustralia, 0.99);

        map.put(EnemyContinentScoreAfrica, 0.3);
        map.put(EnemyContinentScoreAsia, 0.9);
        map.put(EnemyContinentScoreEurope, 0.7);
        map.put(EnemyContinentScoreNorthAmerica, 0.65);
        map.put(EnemyContinentScoreSouthAmerica, 0.2);
        map.put(EnemyContinentScoreAustralia, 0.1);


        return map;
    }

    public TuneyTwo(Player player, Map<String, Double> tunings){
        _me = player;
        _tunings = Collections.unmodifiableMap(tunings);
        _placementMinimums = Arrays.asList(
                scaledTunedValue(FirstCountryMinimumPlacementKey, 10),
                scaledTunedValue(SecondCountryMinimumPlacementKey, 10),
                scaledTunedValue(ThirdCountryMinimumPlacementKey, 10));
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
            _placementsToMake = null;
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
        return order;
    }

    private Occupy doOccupy(Adjutant adjutant, Game game){
        OccupationConstraints constraints = adjutant.getOccupationConstraints();

        int existingForce = game.getOccupationForce(constraints.attacker());
        List<Country> enemies = game.findEnemyNeighbors(constraints.attacker());
        if( enemies.size() > 1 ){
            Map<Country, Double> desirabilityScore = computeGoalCountryDesirabilityScores(game);
            double attackerPlacementScore = computePlacementScore(constraints.attacker(), game, desirabilityScore);
            double conqueredPlacementScore = computePlacementScore(constraints.conquered(), game, desirabilityScore);
            int armiesToDivide = constraints.maximumOccupationForce();
            double toConqueredCountry = armiesToDivide *
                    (conqueredPlacementScore/(attackerPlacementScore+conqueredPlacementScore));
            int occupationForce = Math.min((int) Math.floor(toConqueredCountry), constraints.maximumOccupationForce());
            occupationForce = Math.max(occupationForce, constraints.minimumOccupationForce());

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
        Map<Country, Double> goalCountryScores = computeGoalCountryDesirabilityScores(game);
        Map<Country, Double> ratios = ListUtils.mapify(game.findOccupiedCountries(_me),
                 c -> computePlacementScore(c, game, goalCountryScores));
        return RatioDistributor.distribute(ratios, game.getPlayerHoldings(_me).reserveCount(), _placementMinimums);
    }

    private double computePlacementScore(Country country, Game game, Map<Country, Double> goalCountryScores){

        // placement should be based on
        // 1. desirability of neighbor
        // 2. relative strength versus neighbor (place where attacks are more likely)
        // 3. relative weakness IF on an important (continental) border

        if( ! AiUtils.isBorderCountry(_me, game, country) ){
            return 0;
        }

        return ListUtils.sumAll(game.findEnemyNeighbors(country), goalCountryScores::get);
    }

    /*
     * The higher the score the better the attack is considered to be.
     */
    public double computeAttackScore(PossibleAttack attack, Game game){

        Country attacker = attack.getAttacker();
        int attackerStrength = game.getOccupationForce(attacker);

        Country target = attack.getDefender();
        int targetStrength = game.getOccupationForce(target);

        Continent targetContinent = Continent.find(target);

        Player opponent = game.getOccupier(target);
        int totalOpponentStrength = AiUtils.findAllPlayerArmies(game, opponent);
        int opponentCardCount = game.getPlayerHoldings(opponent).getCards().size();

        Set<Country> targetBloc = AiUtils.findContiguousOwnedCountries(game, target);
        int targetBlocStrength = AiUtils.findStrengthOfCountries(game, targetBloc);

        List<Continent> enemyOwnedContinents = AiUtils.enemyOwnedContinents(_me, game);
        Continent bestGoalContinent = AiUtils.findStrongestUnownedContinent(_me, game);
        List<Continent> goalContinents = AiUtils.possibleGoalContinents(_me, game,
                tunedValue(GoalContinentArmyPercentage), tunedValue(GoalContinentCountryPercentage));
        boolean targetInBestGoalContinent = bestGoalContinent != null && bestGoalContinent.contains(target);
        int myOtherBorderingForces = 0;
        for(Country country : game.findEnemyNeighbors(target)){
            if( _me.equals(game.getOccupier(country))){
                myOtherBorderingForces += game.getOccupationForce(country) - 1;
            }
        }

        // In best continent - check
        // In goal continent - check
        // In opponent owned continent (modified by continent size) - check
        // Close to owning continent - check
        // Close to eliminating player (modified by number of cards) - check
        // Good ratio surrounding country - check
        // Good ratio attacker to defender - check

        double score = continentScore(targetContinent);
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
        if( targetBlocStrength == totalOpponentStrength &&
               targetBlocStrength <= attackerStrength + myOtherBorderingForces){
            double cardModifier = 1 + opponentCardCount;
            score += scaledTunedValue(WeakOpponentAttackKey, 10) * cardModifier;
        }

        score += myOtherBorderingForces / targetStrength;
        score += attackerStrength / targetStrength;

        boolean weakOpponent = targetBlocStrength < attack.getAttackerForce();
        score = booleanAdjust(score, weakOpponent, WeakOpponentAttackKey);

        return score;
    }

    private boolean closeToOwning(Continent continent, Game game){
        int unownedCountryCount = AiUtils.unownedCountryCount(_me, game, continent);
        if ( unownedCountryCount == 1 ){
            return true;
        }
        double ownedCountriesPercentage = AiUtils.continentalCountryPercentage(_me, game, continent);
        if (ownedCountriesPercentage > tunedValue(ContinentCloseCountryPercentAttackKey)){
            return true;
        }
        double ownedArmyPercentage = AiUtils.continentalArmyPercentage(_me, game, continent);
        return ownedArmyPercentage > tunedValue(ContinentCloseArmyPercentAttackKey);
    }

    private int scaledTunedValue(String tuning, int scale){
        return (int) Math.round(scale * tunedValue(tuning));
    }

    private Order generateAttackOrder(Adjutant adjutant, Game game){
        int majorAdvantageCutoff = scaledTunedValue(MajorAdvantageAttackKey, 100);
        List<PossibleAttack> majorAdvantages = AiUtils.findAdvantageousAttacks(_me, game, majorAdvantageCutoff);
        Optional<PossibleAttack> possibleMajorAdvantageAttack = ListUtils.findMax(majorAdvantages);
        if(possibleMajorAdvantageAttack.isPresent()){
            return new Attack(adjutant, possibleMajorAdvantageAttack.get());
        }

        List<PossibleAttack> possibleAttacks = AiUtils.findAllPossibleAttacks(_me, game);
        List<ScoredPossibleAttack> scoredPossibleAttacks = possibleAttacks.stream().map(
                pa -> new ScoredPossibleAttack(pa, computeAttackScore(pa, game))
        ).collect(Collectors.toList());

        String minimumAttackKey = adjutant.hasConqueredCountry() ?
                PostCardMinimumAttackScoreAttackKey : MinimumAttackScoreAttackKey;
        double minimumScore = scaledTunedValue(minimumAttackKey, 10);
        Optional<ScoredPossibleAttack> bestPossibleAttack = ListUtils.findMax(scoredPossibleAttacks,
                pa -> pa.getScore()>minimumScore);
        if( bestPossibleAttack.isPresent()){
            return new Attack(adjutant, bestPossibleAttack.get().getPossibleAttack());
        }
        return new EndAttacks(adjutant);
    }

    private Map<Country, Double> computeGoalCountryDesirabilityScores(Game game){
        List<Country> enemyBorderCountries = AiUtils.findEnemyBordersAsList(_me, game);
        return ListUtils.mapify(enemyBorderCountries, c -> computeDesirabilityScore(c, game));
    }

    private Double computeDesirabilityScore(Country country, Game game) {
        // desirability is based on:
        // 1. strength within continent
        // 2. in enemy owned continent
        // 3. desirability of continent
        Continent continent = Continent.find(country);
        double armyPercentage = AiUtils.continentalArmyPercentage(_me, game, continent);
        double countryPercentage = AiUtils.continentalCountryPercentage(_me, game, continent);
        boolean enemyOwnedContinent = AiUtils.isEnemyOwnedContinent(_me, game, continent);

        double score = 0.0;
        score += armyPercentage;
        score += countryPercentage;
        score += continentScore(continent);

        if (enemyOwnedContinent){
            score += enemyContinentScore(continent);
        }

        if( closeToOwning(continent, game)){
            score += 2 * continentScore(continent);
        }

        return score;
    }

    private double continentScore(Continent continent){
        String key = "ContinentScore" + continent.getNameNoSpaces();
        return tunedValue(key);
    }

    private double enemyContinentScore(Continent continent){
        String key = "EnemyContinentScore" + continent.getNameNoSpaces();
        return tunedValue(key);
    }


    private double booleanAdjust(double current, boolean test, String key){
        double amount = _tunings.get(key);
        return test ? current + amount : current;
    }

    private double tunedValue(String key){
        try {
            return _tunings.get(key);
        } catch (Exception ex){
            System.out.println("Could not find key " + key);
            throw ex;
        }
    }
}
