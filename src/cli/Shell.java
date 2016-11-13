package cli;

import card.Card;
import map.Country;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetlang.core.Callback;
import org.jetlang.fibers.Fiber;
import play.Channels;
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
    private final Fiber _fiber;
    private final Channels _channels;

    public Shell(Game game, Channels channels, Fiber fiber){
        _game = game;
        _fiber = fiber;
        _channels = channels;

        _channels.AdjutantChannel.subscribe(_fiber,
                new Callback<Adjutant>() {
                    @Override
                    public void onMessage(Adjutant adjutant) {
                        handleAdjutantUpdate(adjutant);
                    }
                }
        );
    }

    private void handleAdjutantUpdate(Adjutant adjutant){
        try {
            OrderType ot = next(adjutant);
            handeOrderType(ot, adjutant);
        } catch (Exception ex){
            _log.error("Adjutant update exception", ex);
        }
    }


    public OrderType next(Adjutant adjutant) throws QuitException, IOException {
        _log.info("next called with adjutant for " + adjutant.getActivePlayer());

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

    public void handeOrderType(OrderType orderType, Adjutant adjutant)  throws IOException, QuitException {

            if (orderType == OrderType.ClaimArmies) {
                handClaimArmies(adjutant);
            } else if (orderType == OrderType.PlaceArmy) {
                handlePlaceArmies(adjutant);
            } else if (orderType == OrderType.Attack) {
                handleAttack(adjutant);
            } else if (orderType == OrderType.AttackUntilVictoryOrDeath) {
                handleCommittedAttack(adjutant);
            } else if (orderType == OrderType.EndAttacks) {
                handleEndAttacks(adjutant);
            } else if (orderType == OrderType.Occupy) {
                handleOccupy(adjutant);
            } else if (orderType == OrderType.DrawCard) {
                handleDrawCard(adjutant);
            } else if (orderType == OrderType.Fortify) {
                handleFortify(adjutant);
            } else if( orderType == OrderType.ExchangeCardSet){
                handleExchangeCardSet(adjutant);
            } else {
                message("Cannot handle order type " + orderType);
            }

    }

    private void handClaimArmies(Adjutant adjutant){
        ClaimArmies order = new ClaimArmies(adjutant);
        _channels.OrderEnteredChannel.publish(order);
    }

    private void handleExchangeCardSet(Adjutant adjutant) throws IOException, QuitException {
        List<Card> cards = new ArrayList<Card>(adjutant.getActivePlayer().getCards());
        Card cardOne = selectFromChoices(cards, "Pick a card");
        cards.remove(cardOne);
        Card cardTwo = selectFromChoices(cards, "Pick a card");
        cards.remove(cardTwo);
        Card cardThree = selectFromChoices(cards, "Pick a card");

        ExchangeCardSet exchangeCardSet = new ExchangeCardSet(adjutant, cardOne, cardTwo, cardThree);
        _channels.OrderEnteredChannel.publish(exchangeCardSet);
    }

    private void handleFortify(Adjutant adjutant) throws IOException, QuitException {
        // TODO: Allow player to skip fortifications
        Player currentPlayer = _game.currentAttacker();
        List<Country> countries = _game.possibleFortificationCountries(currentPlayer);
        Collections.sort(countries);
        Country from = selectFromChoices(countries, "Fortify from");
        countries = _game.alliedNeighbors(from);
        Country to = selectFromChoices(countries, "Fortify to");
        int numberArmies = readNumberInput("Number armies to move" ,1,(_game.getOccupationForce(from) - 1));
        Fortify fortify = new Fortify(adjutant, from, to, numberArmies);
        _channels.OrderEnteredChannel.publish(fortify);
    }

    private void handleDrawCard(Adjutant adjutant){
        DrawCard drawcard = new DrawCard(adjutant);
        _channels.OrderEnteredChannel.publish(drawcard);
    }

    private void handleEndAttacks(Adjutant adjutant){
        EndAttacks endAttacks = new EndAttacks(adjutant);
        _channels.OrderEnteredChannel.publish(endAttacks);
    }

    private void handleOccupy(Adjutant adjutant) throws IOException, QuitException {
        Attack successfulAttack = new Attack(adjutant, Country.Afghanistan, Country.Afghanistan, 3);

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
        _channels.OrderEnteredChannel.publish(occupy);
    }

    private void handleCommittedAttack(Adjutant adjutant) throws IOException, QuitException {
        Player currentPlayer = _game.currentAttacker();
        List<Country> countries = _game.countriesToAttackFrom(currentPlayer);
        Collections.sort(countries);
        Country invader = selectFromChoices(countries, "Attack from");
        countries = _game.enemyNeighbors(invader);
        Collections.sort(countries);
        Country target = selectFromChoices(countries, "Attack to");
        message("Attacking from " + invader.getName() + " (" + _game.getOccupationForce(invader) + ") to "
                + target.getName() + " (" + _game.getOccupationForce(target) + ")");
        AttackUntilVictoryOrDeath attack = new AttackUntilVictoryOrDeath(adjutant, invader, target);
        _channels.OrderEnteredChannel.publish(attack);
    }

    private void handleAttack(Adjutant adjutant) throws IOException, QuitException {
        Player currentPlayer = _game.currentAttacker();
        List<Country> countries = _game.countriesToAttackFrom(currentPlayer);
        Collections.sort(countries);
        Country invader = selectFromChoices(countries, "Attack from");
        Collections.sort(countries);
        countries = _game.enemyNeighbors(invader);
        Country target = selectFromChoices(countries, "Attack to");
        int numberDice = Math.min(3, _game.getOccupationForce(invader) - 1);
        message("Attacking from " + invader.getName() + " (" + _game.getOccupationForce(invader) + ") to "
                + target.getName() + " (" + _game.getOccupationForce(target) + ")");
        Attack attack = new Attack(adjutant, invader, target);
        _channels.OrderEnteredChannel.publish(attack);
    }

    private void handlePlaceArmies(Adjutant adjutant) throws IOException, QuitException {
        Player currentPlayer = _game.currentAttacker();
        int reserveArmies = currentPlayer.reserveCount();
        String prompt = "Player " + currentPlayer.getColor() + " has " + reserveArmies + " to place.\n" +
            "Select number to place.";
        int numberToPlace = readNumberInput(prompt, 1, reserveArmies);
        List<Country> ownedCountries = _game.countriesOccupied(currentPlayer);
        Collections.sort(ownedCountries);
        Country country = selectFromChoices(ownedCountries, prompt);
        PlaceArmy placeArmy = new PlaceArmy(adjutant, country, numberToPlace);
        _channels.OrderEnteredChannel.publish(placeArmy);
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
