package ojplg.skir.test.ai;

import ojplg.skir.ai.PossibleAttack;
import ojplg.skir.ai.TuneyTwo;
import ojplg.skir.map.Country;
import ojplg.skir.test.helper.GameHelper;
import org.junit.Assert;
import org.junit.Test;

public class TuneyTwoTest {

    @Test
    public void equalAttacksScoreEqually(){

        GameHelper gameHelper = new GameHelper();
        gameHelper.setAllCountries(gameHelper.WhitePlayer, 1);

        gameHelper.setCountry(Country.Iceland, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Great_Britain, gameHelper.WhitePlayer, 2);
        gameHelper.setCountry(Country.Scandinavia, gameHelper.WhitePlayer, 2);

        PossibleAttack attackFromBritain = new PossibleAttack(Country.Great_Britain, Country.Iceland, 2, 1);
        PossibleAttack attackFromScandinavia = new PossibleAttack(Country.Scandinavia, Country.Iceland, 2, 1);

        TuneyTwo tuneyTwo = new TuneyTwo(gameHelper.WhitePlayer, TuneyTwo.presetTunings());

        double britainScore = tuneyTwo.computeAttackScore(attackFromBritain, gameHelper.Game);
        double scandinaviaScore = tuneyTwo.computeAttackScore(attackFromScandinavia, gameHelper.Game);

        Assert.assertEquals(britainScore, scandinaviaScore, 0.00000001);
   }

    @Test
    public void higherRatioAttacksScoreHigher(){

        GameHelper gameHelper = new GameHelper();
        gameHelper.setAllCountries(gameHelper.WhitePlayer, 1);

        gameHelper.setCountry(Country.Iceland, gameHelper.BluePlayer, 1);
        gameHelper.setCountry(Country.Great_Britain, gameHelper.WhitePlayer, 2);
        gameHelper.setCountry(Country.Scandinavia, gameHelper.WhitePlayer, 3);

        PossibleAttack attackFromBritain = new PossibleAttack(Country.Great_Britain, Country.Iceland, 2, 1);
        PossibleAttack attackFromScandinavia = new PossibleAttack(Country.Scandinavia, Country.Iceland, 2, 1);

        TuneyTwo tuneyTwo = new TuneyTwo(gameHelper.WhitePlayer, TuneyTwo.presetTunings());

        double britainScore = tuneyTwo.computeAttackScore(attackFromBritain, gameHelper.Game);
        double scandinaviaScore = tuneyTwo.computeAttackScore(attackFromScandinavia, gameHelper.Game);

        Assert.assertTrue(britainScore < scandinaviaScore);
    }

    @Test
    public void blocStrengthIncreasesAttackScore(){

        GameHelper gameHelper = new GameHelper();
        gameHelper.setAllCountries(gameHelper.WhitePlayer, 1);

        gameHelper.setCountry(Country.Iceland, gameHelper.BluePlayer, 4);
        gameHelper.setCountry(Country.Great_Britain, gameHelper.WhitePlayer, 12);

        TuneyTwo tuneyTwo = new TuneyTwo(gameHelper.WhitePlayer, TuneyTwo.presetTunings());

        PossibleAttack attackNoHelp = new PossibleAttack(Country.Great_Britain, Country.Iceland, 12, 4);
        double noHelpScore = tuneyTwo.computeAttackScore(attackNoHelp, gameHelper.Game);

        gameHelper.setCountry(Country.Scandinavia, gameHelper.WhitePlayer, 10);
        PossibleAttack attackWithHelp = new PossibleAttack(Country.Great_Britain, Country.Iceland, 12, 4);
        double withHelpScore = tuneyTwo.computeAttackScore(attackWithHelp, gameHelper.Game);

        System.out.println("No help score " + noHelpScore);
        System.out.println("With help score " + withHelpScore);

        Assert.assertTrue(withHelpScore > noHelpScore);
    }

}
