package ojplg.skir.test.ai;

import ojplg.skir.ai.PossibleAttack;
import ojplg.skir.ai.Tuney;
import ojplg.skir.map.Continent;
import ojplg.skir.map.Country;
import ojplg.skir.state.Player;
import ojplg.skir.test.helper.GameHelper;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TuneyTest {

    @Test
    public void testEnemyContinentBorderIncreasesPlacementChances(){
        GameHelper gameHelper = new GameHelper();

        gameHelper.setCountry(Country.Argentina, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Brazil, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Peru, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Venezuela, gameHelper.RedPlayer, 1);

        gameHelper.setCountry(Country.Central_America, gameHelper.BlackPlayer, 1);

        Tuney tuney = new Tuney(gameHelper.BlackPlayer, Tuney.presetTunings());

        double continentSplitValue = tuney.computePlacementScore(Country.Central_America, gameHelper.Game);

        gameHelper.setCountry(Country.Venezuela, gameHelper.BluePlayer, 1);

        double continentOwnedValue = tuney.computePlacementScore(Country.Central_America, gameHelper.Game);

        assertTrue( continentSplitValue < continentOwnedValue);

    }

    @Test
    public void testIsBorderIncreasesPlacementChances(){

        GameHelper gameHelper = new GameHelper();

        gameHelper.setCountry(Country.Alaska, gameHelper.BlackPlayer, 1);
        gameHelper.setCountry(Country.Northwest_Territory, gameHelper.BlackPlayer, 1);
        gameHelper.setCountry(Country.Alberta, gameHelper.BlackPlayer, 1);
        gameHelper.setCountry(Country.Ontario, gameHelper.BlackPlayer, 1);
        gameHelper.setCountry(Country.Western_United_States, gameHelper.BlackPlayer, 1);

        Tuney tuney = new Tuney(gameHelper.BlackPlayer, Tuney.presetTunings());

        double albertaNotOnBorderScore = tuney.computePlacementScore(Country.Alberta, gameHelper.Game);

        gameHelper.setCountry(Country.Ontario, gameHelper.RedPlayer, 1);

        double albertaIsOnBorderScore = tuney.computePlacementScore(Country.Alberta, gameHelper.Game);

        assertTrue( albertaIsOnBorderScore > albertaNotOnBorderScore);
    }

    @Test
    public void testBetterRatioMakesAttackMoreLikely(){

        GameHelper gameHelper = new GameHelper();

        gameHelper.setCountry(Country.Alaska, gameHelper.BlackPlayer, 2);
        gameHelper.setCountry(Country.Northwest_Territory, gameHelper.RedPlayer, 1);

        PossibleAttack possibleAttack = new PossibleAttack(Country.Alaska, Country.Northwest_Territory,
                2 , 1);

        Tuney tuney = new Tuney(gameHelper.BlackPlayer, Tuney.presetTunings());

        double attackLikelihoodAt2to1 = tuney.computePossibleAttackScore(possibleAttack, gameHelper.Game);

        gameHelper.setCountry(Country.Alaska, gameHelper.BlackPlayer, 3);

        possibleAttack = new PossibleAttack(Country.Alaska, Country.Northwest_Territory,
                3 , 1);

        double attackLikelihoodAt3to1 = tuney.computePossibleAttackScore(possibleAttack, gameHelper.Game);

        assertTrue(attackLikelihoodAt3to1 > attackLikelihoodAt2to1);
    }

    @Test
    public void TestIgnoresWesternAustralia(){

        GameHelper helper = new GameHelper();

        // countries of 5 continents owned randomly by enemies
        Continent[] continents = new Continent[]{ Continent.Asia, Continent.Africa, Continent.Europe,
            Continent.North_America, Continent.South_America};
        Player[] enemies = new Player[] { helper.BluePlayer, helper.RedPlayer, helper.WhitePlayer};
        int cnt = 0;
        for(Continent continent : continents){
            for(Country country : continent.getCountries()){
                cnt++;
                Player player = enemies[cnt % (enemies.length)];
                helper.setCountry(country, player, 1);
            }
        }

        // black has Australia and southeast Asia
        helper.setCountry(Country.Western_Australia, helper.BlackPlayer, 1);
        helper.setCountry(Country.Eastern_Australia, helper.BlackPlayer, 1);
        helper.setCountry(Country.Indonesia, helper.BlackPlayer, 1);
        helper.setCountry(Country.New_Guinea, helper.BlackPlayer, 1);
        helper.setCountry(Country.China, helper.BlackPlayer, 1);
        helper.setCountry(Country.Siam, helper.BlackPlayer, 1);
        helper.setCountry(Country.India, helper.BlackPlayer, 1);

        Tuney tuney = new Tuney(helper.BlackPlayer, Tuney.presetTunings());

        double chinaScore = tuney.computePlacementScore(Country.China, helper.Game);
        double indiaScore = tuney.computePlacementScore(Country.India, helper.Game);
        double siamScore = tuney.computePlacementScore(Country.Siam, helper.Game);
        double westernAustraliaScore = tuney.computePlacementScore(Country.Western_Australia, helper.Game);
        double easternAustraliaScore = tuney.computePlacementScore(Country.Eastern_Australia, helper.Game);
        double indonesiaScore = tuney.computePlacementScore(Country.Indonesia, helper.Game);
        double newGuineaScore = tuney.computePlacementScore(Country.New_Guinea, helper.Game);

        // China and India should be top countries for occupation of armies
        assertTrue( chinaScore > siamScore);
        assertTrue( chinaScore > westernAustraliaScore);
        assertTrue( chinaScore > easternAustraliaScore);
        assertTrue( chinaScore > indonesiaScore);
        assertTrue( chinaScore > newGuineaScore);
        assertTrue( indiaScore > siamScore);
        assertTrue( indiaScore > westernAustraliaScore);
        assertTrue( indiaScore > easternAustraliaScore);
        assertTrue( indiaScore > indonesiaScore);
        assertTrue( indiaScore > newGuineaScore);

    }
}
