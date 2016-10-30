package play;

import cli.Shell;
import play.orders.Adjutant;
import state.Game;

public class GameRunner {

    private final Game _game;
    private boolean _gameRunning = true;
    private Shell _shell;

    public GameRunner(Game game, Shell shell){
        _game = game;
        _shell = shell;
    }

    public Adjutant newAdjutant(Roller roller){
        return new Adjutant(_game.currentAttacker(), roller, null);
    }

    public boolean isGameRunning(){
        return _gameRunning;
    }

    public void doAttack(String color, String attackingCountry, String defendingCountry){


    }

    public void doAllOutAttack(String color, String attackingCountry, String defendingCountry){}
}
