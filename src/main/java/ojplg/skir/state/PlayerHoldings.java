package ojplg.skir.state;

import ojplg.skir.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerHoldings {

    private final List<Card> _cards = new ArrayList<>();
    private int _reserveArmies;

    PlayerHoldings(int initialArmies){
        _reserveArmies = initialArmies;
    }

    public List<Card> getCards(){
        return Collections.unmodifiableList(new ArrayList(_cards));
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

    public int reserveCount(){
        return _reserveArmies;
    }

    public void drawReserves(int cnt){
        if (cnt > _reserveArmies){
            throw new RuntimeException("Cannot draw " + cnt + " armies from " + _reserveArmies + " reserves");
        }
        _reserveArmies -= cnt;
    }

    public boolean hasReserves(){
        return _reserveArmies > 0;
    }

    public boolean hasMaximumCards(){
        return _cards.size() >= Constants.MAXIMUM_CARD_HOLDINGS;
    }

    public boolean hasTooManyCards(){
        return _cards.size() > Constants.MAXIMUM_CARD_HOLDINGS;
    }

    @Override
    public String toString() {
        return "PlayerHoldings{" +
                "_cards=" + _cards +
                ", _reserveArmies=" + _reserveArmies +
                '}';
    }
}
