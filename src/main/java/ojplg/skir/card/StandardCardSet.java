package ojplg.skir.card;

import ojplg.skir.map.Country;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StandardCardSet {

    private static Card[] cards = new Card[]{
            make(CardType.Horse, Country.Congo),
            make(CardType.Soldier, Country.East_Africa),
            make(CardType.Cannon, Country.Egypt),
            make(CardType.Horse, Country.Madagascar),
            make(CardType.Soldier, Country.North_Africa),
            make(CardType.Cannon, Country.South_Africa),
            make(CardType.Horse, Country.Afghanistan),
            make(CardType.Soldier, Country.China),
            make(CardType.Cannon, Country.India),
            make(CardType.Horse, Country.Irkutsk),
            make(CardType.Soldier, Country.Japan),
            make(CardType.Cannon, Country.Kamchatka),
            make(CardType.Horse, Country.Middle_East),
            make(CardType.Soldier, Country.Mongolia),
            make(CardType.Cannon, Country.Siam),
            make(CardType.Horse, Country.Siberia),
            make(CardType.Soldier, Country.Ural),
            make(CardType.Cannon, Country.Yakutsk),
            make(CardType.Horse, Country.Eastern_Australia),
            make(CardType.Soldier, Country.Indonesia),
            make(CardType.Cannon, Country.New_Guinea),
            make(CardType.Horse, Country.Western_Australia),
            make(CardType.Soldier, Country.Great_Britain),
            make(CardType.Cannon, Country.Iceland),
            make(CardType.Horse, Country.Northern_Europe),
            make(CardType.Soldier, Country.Scandinavia),
            make(CardType.Cannon, Country.Southern_Europe),
            make(CardType.Horse, Country.Ukraine),
            make(CardType.Soldier, Country.Western_Europe),
            make(CardType.Cannon, Country.Alaska),
            make(CardType.Horse, Country.Alberta),
            make(CardType.Soldier, Country.Central_America),
            make(CardType.Cannon, Country.Eastern_United_States),
            make(CardType.Horse, Country.Greenland),
            make(CardType.Soldier, Country.Northwest_Territory),
            make(CardType.Cannon, Country.Ontario),
            make(CardType.Horse, Country.Quebec),
            make(CardType.Soldier, Country.Western_United_States),
            make(CardType.Cannon, Country.Argentina),
            make(CardType.Horse, Country.Brazil),
            make(CardType.Soldier, Country.Peru),
            make(CardType.Cannon, Country.Venezuela),
            make(CardType.Joker, null),
            make(CardType.Joker, null)
    };

    public static final List<Card> deck = Collections.unmodifiableList(Arrays.asList(cards));

    private static Card make(CardType type, Country country) {
        return new Card(type, country);
    }
}
