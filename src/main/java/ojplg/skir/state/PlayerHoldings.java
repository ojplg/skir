package ojplg.skir.state;

import ojplg.skir.card.Card;
import ojplg.skir.card.CardSet;
import ojplg.skir.state.event.GameSpecifiable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerHoldings implements GameSpecifiable, Serializable {

    private final GameId _gameId;
    private final List<Card> _cards = new ArrayList<>();
    private int _reserveArmies;

    PlayerHoldings(GameId gameId, int initialArmies){
        _gameId = gameId;
        _reserveArmies = initialArmies;
    }

    public List<Card> getCards(){
        return Collections.unmodifiableList(new ArrayList<>(_cards));
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
            throw new GameException(_gameId, "Cannot draw " + cnt + " armies from " + _reserveArmies + " reserves");
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

    public boolean hasCardSet() {
        return CardSet.hasTradeableSet(_cards);
    }

    @Override
    public String toString() {
        return "PlayerHoldings{" +
                "_cards=" + _cards +
                ", _reserveArmies=" + _reserveArmies +
                '}';
    }

    @Override
    public GameId getGameId() {
        return _gameId;
    }

    @Override
    public boolean matches(GameSpecifiable other) {
        return other.getGameId().equals(_gameId);
    }
}
