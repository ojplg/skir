package state;

import card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {

    private final String _color;
    private final List<Card> _cards = new ArrayList<Card>();
    private int _reserveArmies;

    public Player(String color){
        _color = color;
    }

    public List<Card> getCards(){
        return Collections.unmodifiableList(_cards);
    }

    public void removeCards(List<Card> cards){
        _cards.removeAll(cards);
    }

    public void addCards(List<Card> cards) {
        _cards.addAll(cards);
    }

    public void addCard(Card card){
        _cards.add(card);
    }

    public void grantReserves(int cnt){
        _reserveArmies += cnt;
    }

    public void drawReserves(int cnt){
        if (cnt > _reserveArmies){
            throw new RuntimeException("Cannot draw " + cnt + " armies from " + _reserveArmies + "reserves");
        }
        _reserveArmies -= cnt;
    }

    public boolean hasReserves(){
        return _reserveArmies > 0;
    }


    public boolean hasTooManyCards(){
        return _cards.size() > Constants.MAXIMUM_CARD_HOLDINGS;
    }

    @Override
    public String toString() {
        return "Player{" +
                "_color='" + _color + '\'' +
                ", _cards=" + _cards +
                ", _reserveArmies=" + _reserveArmies +
                '}';
    }

    public String getColor(){
        return _color;
    }

    public boolean equals(Object that){
        if ( that == null ){
            return false;
        }
        if ( ! (that instanceof Player) ) {
            return false;
        }
        return this._color.equals(((Player)that)._color);
    }

    public int hashCode(){
        return _color.hashCode();
    }
}
