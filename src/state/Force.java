package state;

public class Force {

    private final Player _player;
    private int _armies;

    public Force(Player player, int armies){
        _player = player;
        _armies = armies;
    }

    public Player getPlayer() {
        return _player;
    }

    public int getArmies() {
        return _armies;
    }

    public void killArmies(int cnt){
        _armies -= cnt;
    }

    public boolean forceExterminated(){
        return 0 == _armies;
    }

    public void addArmies(int cnt) {
        _armies += cnt;
    }
}
