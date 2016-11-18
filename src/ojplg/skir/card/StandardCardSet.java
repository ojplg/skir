package ojplg.skir.card;

import ojplg.skir.map.Country;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StandardCardSet {

    private static Card[] cards = new Card[]{
            make(CardType.Horse, Country.Congo),
            make(CardType.Horse, Country.East_Africa),
            make(CardType.Horse, Country.Egypt),
            make(CardType.Horse, Country.Madagascar),
            make(CardType.Horse, Country.North_Africa),
            make(CardType.Horse, Country.South_Africa),
            make(CardType.Horse, Country.Afghanistan),
            make(CardType.Horse, Country.China),
            make(CardType.Horse, Country.India),
            make(CardType.Horse, Country.Irkutsk),
            make(CardType.Horse, Country.Japan),
            make(CardType.Horse, Country.Kamchatka),
            make(CardType.Horse, Country.Middle_East),
            make(CardType.Horse, Country.Mongolia),
            make(CardType.Horse, Country.Siam),
            make(CardType.Horse, Country.Siberia),
            make(CardType.Horse, Country.Ural),
            make(CardType.Horse, Country.Yakutsk),
            make(CardType.Horse, Country.Eastern_Australia),
            make(CardType.Horse, Country.Indonesia),
            make(CardType.Horse, Country.New_Guinea),
            make(CardType.Horse, Country.Western_Australia),
            make(CardType.Horse, Country.Great_Britain),
            make(CardType.Horse, Country.Iceland),
            make(CardType.Horse, Country.Northern_Europe),
            make(CardType.Horse, Country.Scandinavia),
            make(CardType.Horse, Country.Southern_Europe),
            make(CardType.Horse, Country.Ukraine),
            make(CardType.Horse, Country.Western_Europe),
            make(CardType.Horse, Country.Alaska),
            make(CardType.Horse, Country.Alberta),
            make(CardType.Horse, Country.Central_America),
            make(CardType.Horse, Country.Eastern_United_States),
            make(CardType.Horse, Country.Greenland),
            make(CardType.Horse, Country.Northwest_Territory),
            make(CardType.Horse, Country.Ontario),
            make(CardType.Horse, Country.Quebec),
            make(CardType.Horse, Country.Western_United_States),
            make(CardType.Horse, Country.Argentina),
            make(CardType.Horse, Country.Brazil),
            make(CardType.Horse, Country.Peru),
            make(CardType.Horse, Country.Venezuela),
            make(CardType.Joker, null),
            make(CardType.Joker, null)
    };

    public static final List<Card> deck = Collections.unmodifiableList(Arrays.asList(cards));

    private static Card make(CardType type, Country country) {
        return new Card(type, country);
    }
}
