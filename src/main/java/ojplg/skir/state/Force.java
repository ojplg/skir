package ojplg.skir.state;

import java.io.Serializable;

public class Force implements Serializable {

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

    public void addArmies(int cnt) {
        _armies += cnt;
    }
}
