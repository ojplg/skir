package cli;

import card.Card;
import map.Country;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import play.QuitException;
import play.orders.Adjutant;
import play.orders.Attack;
import play.orders.AttackUntilVictoryOrDeath;
import play.orders.ClaimArmies;
import play.orders.DrawCard;
import play.orders.EndAttacks;
import play.orders.ExchangeCardSet;
import play.orders.Fortify;
import play.orders.Occupy;
import play.orders.OrderType;
import play.orders.PlaceArmy;
import state.Game;
import state.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Shell {

    private final static Logger _log = LogManager.getLogger(Shell.class);

    private final Game _game;

    public Shell(Game game){
        _game = game;
    }

    public OrderType next(Adjutant adjutant) throws QuitException, IOException {
        _log.info("next called with adjutant for " + adjutant.getActivePlayer() +
                " who is automated? " + adjutant.isAutomatedPlayer());
        if(adjutant.isAutomatedPlayer()){
            try {
                Thread.sleep(5);
            } catch(InterruptedException ignored) {
            }
            return adjutant.chooseOrderType(_game);
        } else {
            if (adjutant.mustChooseOrderType()) {
                String prompt = "The current attacker is " + adjutant.getActivePlayer();
                return selectFromChoices(adjutant.allowableOrders(), prompt);
            } else {
                OrderType ot = adjutant.allowableOrders().get(0);
                message("Turn phase is " + _game.currentAttacker() + "." + ot);
                _log.info("Adjutant thinks current player is " + adjutant.getActivePlayer());
                return ot;
            }
        }
    }

    public Adjutant handeOrderType(OrderType orderType, Adjutant adjutant)  throws IOException, QuitException {
        if( adjutant.isAutomatedPlayer()){
            return adjutant.executeAutomatedOrder(orderType, _game);
        } else {

            if (orderType == OrderType.ClaimArmies) {
                _log.info("About to claim armies for " + adjutant.getActivePlayer());
                ClaimArmies order = new ClaimArmies(adjutant);
                Adjutant newAdjutant = order.execute(_game);
                _log.info("New adjutant is for " + newAdjutant.getActivePlayer());
                return newAdjutant;
            }
            if (orderType == OrderType.PlaceArmy) {
                return handlePlaceArmies(adjutant);
            }
            if (orderType == OrderType.Attack) {
                return handleAttack(adjutant);
            }
            if (orderType == OrderType.AttackUntilVictoryOrDeath) {
                return handleCommittedAttack(adjutant);
            }
            if (orderType == OrderType.EndAttacks) {
                return handleEndAttacks(adjutant);
            }
            if (orderType == OrderType.Occupy) {
                return handleOccupy(adjutant);
            }
            if (orderType == OrderType.DrawCard) {
                return handleDrawCard(adjutant);
            }
            if (orderType == OrderType.Fortify) {
                return handleFortify(adjutant);
            }
            if( orderType == OrderType.ExchangeCardSet){
                return handleExchangeCardSet(adjutant);
            }

            message("Cannot handle order type " + orderType);
            return null;
        }
    }

    private Adjutant handleExchangeCardSet(Adjutant adjutant) throws IOException, QuitException {
        List<Card> cards = new ArrayList<Card>(adjutant.getActivePlayer().getCards());
        Card cardOne = selectFromChoices(cards, "Pick a card");
        cards.remove(cardOne);
        Card cardTwo = selectFromChoices(cards, "Pick a card");
        cards.remove(cardTwo);
        Card cardThree = selectFromChoices(cards, "Pick a card");

        ExchangeCardSet exchangeCardSet = new ExchangeCardSet(adjutant, cardOne, cardTwo, cardThree);
        return exchangeCardSet.execute(_game);
    }

    private Adjutant handleFortify(Adjutant adjutant) throws IOException, QuitException {
        // TODO: Allow player to skip fortifications
        Player currentPlayer = _game.currentAttacker();
        List<Country> countries = _game.possibleFortificationCountries(currentPlayer);
        Collections.sort(countries);
        Country from = selectFromChoices(countries, "Fortify from");
        countries = _game.allies(from);
        Country to = selectFromChoices(countries, "Fortify to");
        int numberArmies = readNumberInput("Number armies to move" ,1,(_game.getOccupationForce(from) - 1));
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

    private Adjutant handleOccupy(Adjutant adjutant) throws IOException, QuitException {
        Attack successfulAttack = adjutant.getSuccessfulAttack();

        int dieCount = successfulAttack.getAttackersDiceCount();
        int armiesInInvader = _game.getOccupationForce(successfulAttack.getInvader());
        int armiesLeftInInvader = armiesInInvader - dieCount;
        int numberToMove = dieCount;
        if( armiesLeftInInvader > 1) {
            numberToMove =
                    readNumberInput("How many armies to move? (" , dieCount,armiesInInvader -1);
        }
        Occupy occupy = new Occupy(adjutant, successfulAttack.getInvader(), successfulAttack.getTarget(),
                numberToMove);
        return occupy.execute(_game);
    }

    private Adjutant handleCommittedAttack(Adjutant adjutant) throws IOException, QuitException {
        Player currentPlayer = _game.currentAttacker();
        List<Country> countries = _game.countriesToAttackFrom(currentPlayer);
        Collections.sort(countries);
        Country invader = selectFromChoices(countries, "Attack from");
        countries = _game.targets(invader);
        Collections.sort(countries);
        Country target = selectFromChoices(countries, "Attack to");
        message("Attacking from " + invader.getName() + " (" + _game.getOccupationForce(invader) + ") to "
                + target.getName() + " (" + _game.getOccupationForce(target) + ")");
        AttackUntilVictoryOrDeath attack = new AttackUntilVictoryOrDeath(adjutant, invader, target, _game.getRoller());
        Adjutant newAdjutant =  attack.execute(_game);
        message("New counts are " + invader.getName() + " (" + _game.getOccupationForce(invader) + ") to "
                + target.getName() + " (" + _game.getOccupationForce(target) + ")");
        return newAdjutant;
    }

    //public void handleAttack(String cou)

    private Adjutant handleAttack(Adjutant adjutant) throws IOException, QuitException {
        Player currentPlayer = _game.currentAttacker();
        List<Country> countries = _game.countriesToAttackFrom(currentPlayer);
        Collections.sort(countries);
        Country invader = selectFromChoices(countries, "Attack from");
        Collections.sort(countries);
        countries = _game.targets(invader);
        Country target = selectFromChoices(countries, "Attack to");
        int numberDice = Math.min(3, _game.getOccupationForce(invader) - 1);
        message("Attacking from " + invader.getName() + " (" + _game.getOccupationForce(invader) + ") to "
                + target.getName() + " (" + _game.getOccupationForce(target) + ")");
        Attack attack = new Attack(adjutant, _game.getRoller(), invader, target, numberDice);
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
        int numberToPlace = readNumberInput(prompt, 1, reserveArmies);
        List<Country> ownedCountries = _game.countriesOccupied(currentPlayer);
        Collections.sort(ownedCountries);
        Country country = selectFromChoices(ownedCountries, prompt);
        PlaceArmy placeArmy = new PlaceArmy(adjutant, country, numberToPlace);
        return placeArmy.execute(_game);
    }

    private <T> T selectFromChoices(List<T> possibilities, String prompt) throws IOException, QuitException {
        if( possibilities.size() == 1 ){
            return possibilities.get(0);
        } else {
            StringBuilder buf = new StringBuilder();
            for(int index = 0; index < possibilities.size(); index++ ){
                buf.append(" ");
                buf.append(index + 1);
                buf.append(" ");
                buf.append(possibilities.get(index));
                buf.append("\n");
            }
            int selection = readNumberInput(buf.toString() + prompt,1,possibilities.size());
            T item = possibilities.get(selection - 1);
            message("selected " + item);
            return item;
        }
    }

    private int readNumberInput(String prompt, int min, int max) throws IOException, QuitException {
        message(prompt + "(" + min + "-" + max + ")");
        System.out.print("> ");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String entered = reader.readLine();

        if( entered.equals("q") ){
            throw new QuitException();
        }
        if (entered.equals("p") ){
            message(_game.toString());
            return readNumberInput(prompt, min, max);
        }
        int value;
        try {
            value = Integer.parseInt(entered);
        } catch (NumberFormatException nfe){
            message("Could not parse " + entered);
            return readNumberInput(prompt, min, max);
        }
        message("Read number " + value);
        if( value < min || value > max){
            message("out of range " + value);
            return readNumberInput(prompt, min, max);
        }
        return value;
    }

    private static void message(String msg){
        System.out.println(msg);
    }

}
