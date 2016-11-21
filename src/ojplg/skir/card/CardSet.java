package ojplg.skir.card;

public class CardSet {
    private final Card _one;
    private final Card _two;
    private final Card _three;

    public CardSet(Card one, Card two, Card three){
        _one = one;
        _two = two;
        _three = three;
    }

    public Card getOne() {
        return _one;
    }

    public Card getTwo() {
        return _two;
    }

    public Card getThree() {
        return _three;
    }

    public boolean isExchangeableSet(){
        if (_one == null || _two == null || _three == null){
            return false;
        }
        if( _one.matchesType(_two) && _one.matchesType(_three) ){
            return true;
        }
        if ( _one.unMatchesType(_two) && _one.unMatchesType(_three) && _two.unMatchesType(_three) ){
            return true;
        }
        return false;
    }
}
