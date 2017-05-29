package ojplg.skir.card;

import ojplg.skir.map.Country;

public class Card {

    private final CardType _type;
    private final Country _country;

    public Card(CardType type, Country country){
        _type = type;
        _country = country;
    }

    public CardType getType() {
        return _type;
    }

    public Country getCountry() {
        return _country;
    }

    public boolean isJoker(){
        return CardType.Joker == _type;
    }

    public boolean unMatchesType(Card other){
        if ( isJoker() || other.isJoker() ){
            return true;
        }
        return ! _type.equals(other._type);
    }

    public boolean matchesType(Card other){
        if ( isJoker() || other.isJoker() ){
            return true;
        }
        return _type.equals(other._type);
    }

    public String toString(){
        return "Card { type=" + _type + ", country=" + _country +"}";
    }
}
