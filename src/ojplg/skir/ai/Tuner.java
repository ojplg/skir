package ojplg.skir.ai;

import ojplg.skir.map.Continent;
import ojplg.skir.map.Country;
import ojplg.skir.play.orders.Adjutant;
import ojplg.skir.play.orders.Order;
import ojplg.skir.state.Game;
import ojplg.skir.state.Player;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

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

    private Map<String,Float> tunings = new HashMap<>();

    private final Player _me;


    public Tuner(Player player){
        player.setDisplayName("Tuner");
        _me = player;
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

    private float computePlacementScore(Country country, Game game){

        Continent continent = Continent.find(country);
        boolean isContinentalBorder = game.isContinentalBorder(country);
        boolean isOwnedContinent = game.isContinentOwner(_me, continent);

        List<Country> alliedNeighbors = game.findAlliedNeighbors(country);
        List<Country> enemyNeighbors = game.findEnemyNeighbors(country);

        int currentOccupationStrength = game.getOccupationForce(country);
        int totalNeighbors = alliedNeighbors.size() + enemyNeighbors.size();
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
        
        return score;
    }

    private float ratioAdjust(float current, int numerator, int denominator, String testKey, String scaleKey){
        float ratio = numerator/denominator;
        float scale = tunings.get(scaleKey);
        float test = tunings.get(testKey);
        return test < ratio ? scale * current : (1-scale) * current;
    }

    private float booleanAdjust(float current, boolean test, String key){
        float scale = tunings.get(key);
        return test ? scale * current : (1-scale) * current;
    }
}
