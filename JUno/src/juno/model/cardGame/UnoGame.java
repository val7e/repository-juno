package juno.model.cardGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;


import juno.model.cardDeck.Card;
import juno.model.cardDeck.Color;
import juno.model.cardDeck.Deck;
import juno.model.cardDeck.Value;
import juno.model.cardPlayers.Player;
import juno.model.cardPlayers.PlayerBot;

/**
 * 
 * @author val7e
 * 
 * The field currentDeck is the deck of the game
 * The field discardDeck is the pile of discard cards
 *
 */
@SuppressWarnings("deprecation")
public class UnoGame extends Observable {
	
	private Deck deck = new Deck();
	private Stack<Card> discardDeck;
	private Color currentColor;
	private Value currentValue;
	private PlayersIterator playersIterator;
	private boolean gameOver = false;
	
	public UnoGame(List<Player> players) {
        this.deck = new Deck();
        this.discardDeck = new Stack<Card>();
        this.playersIterator = new PlayersIterator(players);
	}
	/**
	 * This method initialize the game following these points:
	 * 1. shuffles the deck
	 * 2. gives 7 cards for each player in the game
	 * 3. it extracts the initial card from the currentDeck
	 * 4. invokes checkCardAction which evaluates if the topCard does something
	 * 5. invokes getPlayableCards, a class method that filters each players hand to find the playable cards and it stores it into a list of cards
	 */
	
	public UnoGame prepareGame() {
		// Shuffle the deck and deal cards to each player
        deck.shuffleCards();
        //get hands to players
        for (Player player : playersIterator.getPlayers()) {
			ArrayList<Card> hand = new ArrayList<Card>();
            for (int i = 0; i < 7; i++) {
                hand.add(deck.drawCard());
            }
            playersIterator.getHandsMap().put(player.getNickname(), hand);
		}
		// draw first card
		discardDeck.add(deck.drawCard());
		return this;
	}

	public void startGame(Queue<Card> ipc) {
		/**
		 * - se e' una carta azione: subire l'azione (auto)
		 * - altrimenti pescare e aggiungere la carta alla mano (auto)
		 * - ottenere le carte giocabili (auto)
		 * - giocarne una (auto/manuale)
		 * - next player
		 */
		while (!this.gameOver) {
			for (Player p: this.playersIterator) {
				List<Card> hand = this.playersIterator.getCurrentPlayerHand();
				String nick = this.playersIterator.getCurrentPlayer().getNickname();
				Card lastDiscarded = getLastDiscardedCard();
				if (lastDiscarded.isActionCard()) this.checkCardAction(lastDiscarded, gameOver);
				
				this.playersIterator.getCurrentPlayerHand().add(this.deck.drawCard());
				List<Card> playables = this.getPlayableCards(lastDiscarded);
				Card choosen;
				if (!p.getIsBot()) {
					do {
						choosen = ipc.poll();
						if (choosen != null) break;
					} while (true);
				}
				else {
					choosen = playables.get(new Random().nextInt(playables.size()));
				}
				
				System.out.println(String.format("[%s]", nick));
				System.out.println(String.format("hand: %s", Arrays.toString(hand.toArray())));
				System.out.println(String.format("discarded: %s", lastDiscarded));
				System.out.println(String.format("choosen: %s", choosen));
				try {
					TimeUnit.SECONDS.sleep(3);
				} catch (Exception e) {
					System.out.println("eccezione in timer");
				}
			}
		}
	}
	
	/**
	 * This method checks if the first extracted card is valid.
	 * If the first card is a PIU_QUATTRO wild card, it keeps drawing cards until it's another card.
	 * @param deck is the current deck of the game
	 * @return the first card
	 */
	private void checkDiscardedCard() {

		Card cardToCheck = this.getLastDiscardedCard();

	}
	
	private Card getLastDiscardedCard() {
		return this.discardDeck.get(0);
	}

	/**
	 * The core of the following method is a switch that decides which action method to call based on the value of the extracted card.
	 * @param playedActionCard the extracted card
	 * @param isFirst a boolean which tells the method if is the first card extracted or not 
	 */
	private void checkCardAction(Card playedActionCard, boolean isFirst) {
		
		currentColor = playedActionCard.getColor();
		currentValue = playedActionCard.getValue();
		discardCard(playedActionCard);
		switch (currentValue) {
		    case SALTO -> {
		    	skipAction();
		    }
		    case INVERTI -> {
		    	reverseTurn();
		    }
		    case PIU_DUE -> {
		    	if (isFirst) drawTwoAction();
		    	else {
		    		playersIterator.nextPlayer();
		    		drawTwoAction();
		    	}
		    }
		    case PIU_QUATTRO -> {
		    	currentColor = chooseColor(currentColor);
		    	drawFourJolly();
		    }
		    case CAMBIO_COLORE -> {
		    	// change color method: for now it picks a random color
		    	// handle it for CAMBIO_COLORE and PIU_QUATTRO
		    	currentColor = chooseColor(currentColor);
		    	if (isFirst) System.out.println(playersIterator.getCurrentPlayer().getNickname() + " has chosen the color " + currentColor);
		    	else playersIterator.nextPlayer();
		    }
		    default -> {
		    	System.out.println("SWITCH: A numbered card has been extracted");
		    }
		}
		
	}
	
	/**
	 * This method is invoked when a SALTO card is played.
	 */
	private void skipAction() {
		playersIterator.nextPlayer();
		System.out.println("SKIP: New current player: " + playersIterator.getCurrentPlayer().getNickname());
	}
	
	/**
	 * This method is invoked when a INVERTI card is played.
	 */
	private void reverseTurn() {
		playersIterator.reverseDirection();
		playersIterator.nextPlayer();
		System.out.println("REVERSE: New current player: " + playersIterator.getCurrentPlayer().getNickname());
	}
	
	/**
	 * This method is invoked when a PIU_DUE card is played.
	 */
	private void drawTwoAction() {
		for (int i = 0; i<2; i++) {	
			playersIterator.getCurrentPlayerHand().add(deck.drawCard());
		}
		System.out.println("DRAW TWO: " + playersIterator.getCurrentPlayer().getNickname() + " " + playersIterator.getCurrentPlayerHand());
		//the player that draws 2 cards skips the turn
		playersIterator.nextPlayer();
		System.out.println("DRAW TWO: New current player: " + playersIterator.getCurrentPlayer().getNickname());
	}
	
	/**
	 * This method is invoked when a PIU_QUATTRO card is played.
	 */
	private void drawFourJolly() {
		for (int i = 0; i<4; i++) {	
			playersIterator.getHandsMap().get(playersIterator.nextPlayer().getNickname()).add(deck.drawCard());
		}
		System.out.println("DRAW FOUR: " + playersIterator.getCurrentPlayer().getNickname() + " " + playersIterator.getCurrentPlayerHand());
		
		//the player that draws 4 cards skips the turn
		playersIterator.nextPlayer();
		System.out.println("DRAW FOUR: New current player: " + playersIterator.getCurrentPlayer().getNickname());
	}
	
	/**
	 * This method is invoked when a CAMBIO_COLORE card is played.
	 * @param currentColor is the current color of the card
	 * @return
	 */
	private Color chooseColor(Color currentColor) {
		Random random = new Random();
		Color[] colors = Color.values();
		int index = random.nextInt(colors.length);
		currentColor = colors[index];
		return currentColor;
	}
	
	/**
	 * This method is add the extracted card to the discardDeck
	 * @param card
	 */
	private void discardCard(Card card) {
		discardDeck.add(card);
	}
	
	/**
	 * This method is invoked when all the cards in the deck were dealt, so it restarts the deck from the discardDeck.
	 */
	private void restartDeckFromDiscard() {
		if (deck.isEmpty()) {
			for (Card c : discardDeck) {
				deck.add(c);
			}
		}
		deck.shuffleCards();
	}
	
	private List<Card> getPlayableCards(Card topCard) {
		String currentPlayer = this.playersIterator.getCurrentPlayer().getNickname();
		Predicate<Card> colorCountPredicate = x->x.getColor() == currentColor;
		Predicate<Card> piuQuattroFilter = x->x.getValue() != Value.PIU_QUATTRO;
		
		Predicate<Card> playableFilter = w -> {
			return w.getColor() == currentColor ||
			w.getValue() == currentValue ||
			w.getValue() == Value.CAMBIO_COLORE ||
			w.getValue() == Value.PIU_QUATTRO;
		};

		List<Card> playableCards = this.playersIterator
			.getHand(currentPlayer)
			.stream().filter(playableFilter)
			.collect(Collectors.toList());
		long currentColorCards = playableCards.stream().filter(colorCountPredicate).count();
		// togli il piu quattro se c'e almeno una carta del colore corrente nella mano
		List<Card> noPiuQuattroFiltered = playableCards.stream().filter(piuQuattroFilter).collect(Collectors.toList());
		return currentColorCards > 0 ? noPiuQuattroFiltered : playableCards;
	}
	
	private Card playCard(Card card, PlayersIterator iterator) {
		List<Card> playableCards = getPlayableCards(card);
		// the player has to choose the card from playableCards
		return card;
		
	}
}



