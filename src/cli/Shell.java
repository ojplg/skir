package cli;

import map.Country;
import play.QuitException;
import play.orders.Adjutant;
import play.orders.ClaimArmies;
import play.orders.OrderType;
import play.orders.PlaceArmy;
import state.Game;
import state.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Shell {

    public static OrderType next(Adjutant adjutant) throws QuitException, IOException {
        System.out.println("The current attacker is " + adjutant.getActivePlayer());
        Map<Integer, OrderType> orderMap = new HashMap<Integer, OrderType>();
        if( adjutant.mustChooseOrderType() ) {
            for (int index = 0; index < adjutant.allowableOrders().size(); index++) {
                OrderType ot = adjutant.allowableOrders().get(index);
                System.out.println(" " + (index + 1) + ") " + ot);
                orderMap.put(index, ot);
            }
        } else {
            OrderType ot = adjutant.allowableOrders().get(0);
            System.out.println("Turn phase is " + ot);
            return ot;
        }
        int value = readNumberInput();
        return orderMap.get(value);
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
        System.out.println("Cannot handle order type " + orderType);
        return null;
    }

    private static Adjutant handlePlaceArmies(Adjutant adjutant, Game game) throws IOException, QuitException{
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
