package ojplg.skir.test.ai;

import ojplg.skir.ai.AiUtils;
import ojplg.skir.ai.PossibleAttack;
import ojplg.skir.map.Country;
import ojplg.skir.utils.ListUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PossibleAttackTest {

    @Test
    public void testOrdering(){
        PossibleAttack goodOdds = new PossibleAttack(Country.Afghanistan, Country.Middle_East,
                19, 3);

        PossibleAttack badOdds = new PossibleAttack(Country.Alaska, Country.Kamchatka,
                4, 9);

        PossibleAttack evenOdds = new PossibleAttack(Country.Argentina, Country.Brazil,
                6, 6);

        assertTrue(goodOdds.compareTo(badOdds) > 1);
        assertTrue(badOdds.compareTo(evenOdds) < 1);

        List<PossibleAttack> attacks = new ArrayList<>();
        attacks.add(goodOdds);
        attacks.add(badOdds);
        attacks.add(evenOdds);
        Collections.shuffle(attacks);

        PossibleAttack best = ListUtils.findMax(attacks).get();
        assertEquals(Country.Afghanistan,best.getAttacker());

    }
}
