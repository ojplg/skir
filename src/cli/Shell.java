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

    public static OrderType next(Adjutant adjutant) throws QuitException, IOException {
        message("The current attacker is " + adjutant.getActivePlayer());
        if( adjutant.mustChooseOrderType() ) {
            return selectFromChoices(adjutant.allowableOrders());
        } else {
            OrderType ot = adjutant.allowableOrders().get(0);
            message("Turn phase is " + ot);
            return ot;
        }
    }

    public static Adjutant handeOrderType(OrderType orderType, Adjutant adjutant, Game game)  throws IOException, QuitException {
        if( orderType == OrderType.ClaimArmies ){
            ClaimArmies order = new ClaimArmies(adjutant);
            order.execute(game);
            return order.getAdjutant();
        }
        if (orderType == OrderType.PlaceArmy){
            return handlePlaceArmies(adjutant, game);
        }
        if( orderType == OrderType.Attack ){
            return handleAttack(adjutant, game);
        }
        if( orderType == OrderType.EndAttacks){
            return handleEndAttacks(adjutant, game);
        }
        if( orderType == OrderType.Occupy){
            return handleOccupy(adjutant, game);
        }
        if( orderType == OrderType.DrawCard){
            return handleDrawCard(adjutant, game);
        }

        message("Cannot handle order type " + orderType);
        return null;
    }

    private static Adjutant handleDrawCard(Adjutant adjutant, Game game){
        DrawCard drawcard = new DrawCard(adjutant);
        return drawcard.execute(game);

    }


    private static Adjutant handleEndAttacks(Adjutant adjutant, Game game){
        EndAttacks endAttacks = new EndAttacks(adjutant);
        return endAttacks.execute(game);
    }

    private static Adjutant handleOccupy(Adjutant adjutant, Game game){
        Attack successfulAttack = adjutant.getSuccessfulAttack();
        Occupy occupy = new Occupy(adjutant, successfulAttack.getInvader(), successfulAttack.getTarget(),
                successfulAttack.getAttackersDiceCount());
        return occupy.execute(game);
    }

    private static Adjutant handleAttack(Adjutant adjutant, Game game) throws IOException, QuitException {
        Player currentPlayer = game.currentAttacker();
        List<Country> countries = game.countriesOccupied(currentPlayer);
        message("Attack from ");
        Country invader = selectFromChoices(countries);
        countries = game.targets(invader);
        message("Attack to ");
        Country target = selectFromChoices(countries);
        int numberDice = Math.min(3, game.getOccupationForce(invader));
        message("Attacking from " + invader.getName() + " (" + game.getOccupationForce(invader) + ") to "
                + target.getName() + " (" + game.getOccupationForce(target) + ")");
        Attack attack = new Attack(adjutant, new RandomRoller(1), invader, target, numberDice);
        Adjutant newAdjutant =  attack.execute(game);
        message("New counts are " + invader.getName() + " (" + game.getOccupationForce(invader) + ") to "
                + target.getName() + " (" + game.getOccupationForce(target) + ")");
        return newAdjutant;
    }

    private static Adjutant handlePlaceArmies(Adjutant adjutant, Game game) throws IOException, QuitException {
        Player currentPlayer = game.currentAttacker();
        int reserveArmies = currentPlayer.reserveCount();
        message("Player " + currentPlayer.getColor() + " has " + reserveArmies + " to place.");
        message("Select number to place.");
        int numberToPlace = readNumberInput();
        List<Country> ownedCountries = game.countriesOccupied(currentPlayer);
        Country country = selectFromChoices(ownedCountries);
        PlaceArmy placeArmy = new PlaceArmy(adjutant, country, numberToPlace);
        return placeArmy.execute(game);
    }

    private static <T> T selectFromChoices(List<T> possibilities) throws IOException, QuitException {
        if( possibilities.size() == 1 ){
            return possibilities.get(0);
        } else {
            for(int index = 0; index < possibilities.size(); index++ ){
                message(" " + (index + 1) + " " + possibilities.get(index));
            }
            int selection = readNumberInput();
            T item = possibilities.get(selection - 1);
            message("selected " + item);
            return item;
        }
    }

    private static int readNumberInput() throws IOException, QuitException {
        System.out.print("> ");
        Reader reader = new InputStreamReader(System.in);
        int character = reader.read();
        message("READ " + character);
        if( character == 'q' ){
            throw new QuitException();
        }
        int value = character - 48;
        message("Read number " + value);
        return value;
    }

    private static void message(String msg){
        System.out.println(msg);
    }

}
