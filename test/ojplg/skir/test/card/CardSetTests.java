package ojplg.skir.test.card;

import ojplg.skir.card.Card;
import ojplg.skir.card.CardSet;
import ojplg.skir.card.CardType;
import ojplg.skir.map.Country;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CardSetTests {

    @Test
    public void testIsExchangeableSet_true_allMatched(){
        CardSet set = new CardSet(new Card(CardType.Horse, Country.Afghanistan),
                                    new Card(CardType.Horse, Country.Great_Britain),
                                    new Card(CardType.Horse, Country.Ural));
        assertTrue(set.isExchangeableSet());
    }

    @Test
    public void testIsExchangeableSet_true_allUnmatched(){
        CardSet set = new CardSet(new Card(CardType.Horse, Country.Afghanistan),
                new Card(CardType.Cannon, Country.Great_Britain),
                new Card(CardType.Soldier, Country.Ural));
        assertTrue(set.isExchangeableSet());
    }

    @Test
    public void testIsExchangeableSet_false(){
        CardSet set = new CardSet(new Card(CardType.Horse, Country.Afghanistan),
                new Card(CardType.Cannon, Country.Great_Britain),
                new Card(CardType.Cannon, Country.Ural));
        assertFalse(set.isExchangeableSet());
    }

    @Test
    public void testIsExchangeableSet_true_withJoker_allUnmatched(){
        CardSet set = new CardSet(new Card(CardType.Horse, Country.Afghanistan),
                new Card(CardType.Cannon, Country.Great_Britain),
                new Card(CardType.Joker, null));
        assertTrue(set.isExchangeableSet());

    }

    @Test
    public void testIsExchangeableSet_true_withJoker_allMatched(){
        CardSet set = new CardSet(new Card(CardType.Horse, Country.Afghanistan),
                new Card(CardType.Horse, Country.Great_Britain),
                new Card(CardType.Joker, null));
        assertTrue(set.isExchangeableSet());

    }
}
