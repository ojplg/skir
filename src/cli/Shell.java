package cli;

import map.Country;
import play.QuitException;
import play.RandomRoller;
import play.orders.*;
import state.Game;
import state.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

public class Shell {

    private final Game _game;

    public Shell(Game game){
        _game = game;
    }

    public OrderType next(Adjutant adjutant) throws QuitException, IOException {
        if( adjutant.mustChooseOrderType() ) {
            String prompt = "The current attacker is " + adjutant.getActivePlayer();
            return selectFromChoices(adjutant.allowableOrders(), prompt);
        } else {
            OrderType ot = adjutant.allowableOrders().get(0);
            message("Turn phase is " + _game.currentAttacker() + "." + ot);
            return ot;
        }
    }

    public Adjutant handeOrderType(OrderType orderType, Adjutant adjutant)  throws IOException, QuitException {
        if( orderType == OrderType.ClaimArmies ){
            ClaimArmies order = new ClaimArmies(adjutant);
            order.execute(_game);
            return order.getAdjutant();
        }
        if (orderType == OrderType.PlaceArmy){
            return handlePlaceArmies(adjutant);
        }
        if( orderType == OrderType.Attack ){
            return handleAttack(adjutant);
        }
        if( orderType == OrderType.EndAttacks){
            return handleEndAttacks(adjutant);
        }
        if( orderType == OrderType.Occupy){
            return handleOccupy(adjutant);
        }
        if( orderType == OrderType.DrawCard){
            return handleDrawCard(adjutant);
        }
        if( orderType == OrderType.Fortify){
            return handleFortify(adjutant);
        }

        message("Cannot handle order type " + orderType);
        return null;
    }

    private Adjutant handleFortify(Adjutant adjutant) throws IOException, QuitException {
        // TODO: Allow player to skip fortifications
        // TODO: Correct filtering on to and from countries
        Player currentPlayer = _game.currentAttacker();
        List<Country> countries = _game.countriesOccupied(currentPlayer);
        Country from = selectFromChoices(countries, "Fortify from");
        countries = _game.targets(from);
        Country to = selectFromChoices(countries, "Fortify to");
        int numberArmies = readNumberInput("Number armies to move 1-" + (_game.getOccupationForce(from) - 1));
        Fortify order = new Fortify(adjutant, from, to, numberArmies);
        return order.execute(_game);
    }

    private Adjutant handleDrawCard(Adjutant adjutant){
        DrawCard drawcard = new DrawCard(adjutant);
        return drawcard.execute(_game);
    }

    private Adjutant handleEndAttacks(Adjutant adjutant){
        EndAttacks endAttacks = new EndAttacks(adjutant);
        return endAttacks.execute(_game);
    }

    private Adjutant handleOccupy(Adjutant adjutant){
        Attack successfulAttack = adjutant.getSuccessfulAttack();
        Occupy occupy = new Occupy(adjutant, successfulAttack.getInvader(), successfulAttack.getTarget(),
                successfulAttack.getAttackersDiceCount());
        return occupy.execute(_game);
    }

    private Adjutant handleAttack(Adjutant adjutant) throws IOException, QuitException {
        Player currentPlayer = _game.currentAttacker();
        List<Country> countries = _game.countriesToAttackFrom(currentPlayer);
        Country invader = selectFromChoices(countries, "Attack from");
        countries = _game.targets(invader);
        Country target = selectFromChoices(countries, "Attack to");
        int numberDice = Math.min(3, _game.getOccupationForce(invader));
        message("Attacking from " + invader.getName() + " (" + _game.getOccupationForce(invader) + ") to "
                + target.getName() + " (" + _game.getOccupationForce(target) + ")");
        Attack attack = new Attack(adjutant, new RandomRoller(1), invader, target, numberDice);
        Adjutant newAdjutant =  attack.execute(_game);
        message("New counts are " + invader.getName() + " (" + _game.getOccupationForce(invader) + ") to "
                + target.getName() + " (" + _game.getOccupationForce(target) + ")");
        return newAdjutant;
    }

    private Adjutant handlePlaceArmies(Adjutant adjutant) throws IOException, QuitException {
        Player currentPlayer = _game.currentAttacker();
        int reserveArmies = currentPlayer.reserveCount();
        String prompt = "Player " + currentPlayer.getColor() + " has " + reserveArmies + " to place.\n" +
            "Select number to place.";
        int numberToPlace = readNumberInput(prompt);
        List<Country> ownedCountries = _game.countriesOccupied(currentPlayer);
        Country country = selectFromChoices(ownedCountries, prompt);
        PlaceArmy placeArmy = new PlaceArmy(adjutant, country, numberToPlace);
        return placeArmy.execute(_game);
    }

    private <T> T selectFromChoices(List<T> possibilities, String prompt) throws IOException, QuitException {
        if( possibilities.size() == 1 ){
            return possibilities.get(0);
        } else {
            StringBuffer buf = new StringBuffer();
            for(int index = 0; index < possibilities.size(); index++ ){
                buf.append(" " + (index + 1) + " " + possibilities.get(index));
                buf.append("\n");
            }
            int selection = readNumberInput(buf.toString() + prompt);
            T item = possibilities.get(selection - 1);
            message("selected " + item);
            return item;
        }
    }

    private int readNumberInput(String prompt) throws IOException, QuitException {
        message(prompt);
        System.out.print("> ");
        Reader reader = new InputStreamReader(System.in);
        int character = reader.read();
        if( character == 'q' ){
            throw new QuitException();
        }
        if (character == 'p' ){
            message(_game.toString());
            return readNumberInput(prompt);
        }
        int value = character - 48;
        message("Read number " + value);
        return value;
    }

    private static void message(String msg){
        System.out.println(msg);
    }

}