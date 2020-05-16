package ojplg.skir.state;

import ojplg.skir.card.CardStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState implements Serializable {

    private GameId _gameId;
    private Occupations _occupations;
    private List<Player> _players = new ArrayList<>();
    private Map<String, PlayerHoldings> _playerHoldings = new HashMap<>();
    private Map<String, BattleStats> _playerStats = new HashMap<>();
    private CardStack _cardPile;

    private Player _currentAttacker;
    private int _turnNumber = 1;
    private int _lastAttackTurn = 0;

    private Map<Player, String> _aiPlayerNames;

    public Map<Player, String> getAiPlayerNames() {
        return _aiPlayerNames;
    }

    public void setAiPlayerNames(Map<Player, String> _aiPlayerNames) {
        this._aiPlayerNames = _aiPlayerNames;
    }

    public GameId getGameId() {
        return _gameId;
    }

    public void setGameId(GameId _gameId) {
        this._gameId = _gameId;
    }

    public Occupations getOccupations() {
        return _occupations;
    }

    public void setOccupations(Occupations _occupations) {
        this._occupations = _occupations;
    }

    public List<Player> getPlayers() {
        return _players;
    }

    public void setPlayers(List<Player> _players) {
        this._players = _players;
    }

    public Map<String, PlayerHoldings> getPlayerHoldings() {
        return _playerHoldings;
    }

    public void setPlayerHoldings(Map<String, PlayerHoldings> _playerHoldings) {
        this._playerHoldings = _playerHoldings;
    }

    public Map<String, BattleStats> getPlayerStats() {
        return _playerStats;
    }

    public void setPlayerStats(Map<String, BattleStats> _playerStats) {
        this._playerStats = _playerStats;
    }

    public CardStack getCardPile() {
        return _cardPile;
    }

    public void setCardPile(CardStack _cardPile) {
        this._cardPile = _cardPile;
    }

    public Player getCurrentAttacker() {
        return _currentAttacker;
    }

    public void setCurrentAttacker(Player _currentAttacker) {
        this._currentAttacker = _currentAttacker;
    }

    public int getTurnNumber() {
        return _turnNumber;
    }

    public void setTurnNumber(int _turnNumber) {
        this._turnNumber = _turnNumber;
    }

    public int getLastAttackTurn() {
        return _lastAttackTurn;
    }

    public void setLastAttackTurn(int _lastAttackTurn) {
        this._lastAttackTurn = _lastAttackTurn;
    }

    public boolean isOver(){
        return _players.size() <= 1;
    }
}
