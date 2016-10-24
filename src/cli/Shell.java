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
        System.out.println("The current attacker is " + adjutant.getActivePlayer());
        if( adjutant.mustChooseOrderType() ) {
            return selectFromChoices(adjutant.allowableOrders());
        } else {
            OrderType ot = adjutant.allowableOrders().get(0);
            System.out.println("Turn phase is " + ot);
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

        System.out.println("Cannot handle order type " + orderType);
        return null;
    }

    private static Adjutant handleAttack(Adjutant adjutant, Game game) throws IOException, QuitException {
        Player currentPlayer = game.currentAttacker();
        List<Country> countries = game.countriesOccupied(currentPlayer);
        System.out.println("Attack from ");
        Country invader = selectFromChoices(countries);
        countries = game.targets(invader);
        System.out.println("Attack to ");
        Country target = selectFromChoices(countries);
        int numberDice = Math.min(3, game.getOccupationForce(invader));
        System.out.println("Attacking from " + invader.getName() + " (" + game.getOccupationForce(invader) + ") to "
                + target.getName() + " (" + game.getOccupationForce(target) + ")");
        Attack attack = new Attack(adjutant, new RandomRoller(1), invader, target, numberDice);
        Adjutant newAdjutant =  attack.execute(game);
        System.out.println("New counts are " + invader.getName() + " (" + game.getOccupationForce(invader) + ") to "
                + target.getName() + " (" + game.getOccupationForce(target) + ")");
        return newAdjutant;
    }

    private static Adjutant handlePlaceArmies(Adjutant adjutant, Game game) throws IOException, QuitException {
        Player currentPlayer = game.currentAttacker();
        int reserveArmies = currentPlayer.reserveCount();
        System.out.println("Player " + currentPlayer.getColor() + " has " + reserveArmies + " to place.");
        System.out.println("Select number to place.");
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
                System.out.println(" " + (index + 1) + " " + possibilities.get(index));
            }
            int selection = readNumberInput();
            T item = possibilities.get(selection - 1);
            System.out.println("selected " + item);
            return item;
        }
    }

    private static int readNumberInput() throws IOException, QuitException {
        System.out.print("> ");
        Reader reader = new InputStreamReader(System.in);
        int character = reader.read();
        System.out.println("READ " + character);
        if( character == 'q' ){
            throw new QuitException();
        }
        int value = character - 48;
        System.out.println("Read number " + value);
        return value;
    }
}
