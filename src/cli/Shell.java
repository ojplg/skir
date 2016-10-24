package cli;

import play.orders.Adjutant;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class Shell {
    public static void printPrompt(Adjutant adjutant) {
        System.out.println("The current attacker is " + adjutant.getActivePlayer());
        if( adjutant.mustChooseOrderType() ) {
            for (int index = 0; index < adjutant.allowableOrders().size(); index++) {
                System.out.println(" " + (index + 1) + ") " + adjutant.allowableOrders().get(index));
            }
        } else {
            System.out.println("Turn phase is " + adjutant.allowableOrders().get(0));
        }
    }

    public static int awaitResponse() throws IOException {
        Reader reader = new InputStreamReader(System.in);
        int character = reader.read();
        System.out.println("READ " + character);
        return character - 48;
    }
}
