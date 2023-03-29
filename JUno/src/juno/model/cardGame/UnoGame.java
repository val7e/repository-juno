package juno.model.cardGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.Stack;
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
	private PlayersIterator iterator;
	private boolean gameOver;
	
	public UnoGame(List<Player> players) {
        this.deck = new Deck();
        this.discardDeck = new Stack<Card>();
        this.iterator = new PlayersIterator(players);
        
        start();
	}
	/**
	 * This method initialize the game following these points:
	 * 1. shuffles the deck
	 * 2. gives 7 cards for each player in the game
	 * 3. it extracts the initial card from the currentDeck
	 * 4. invokes checkCardAction which evaluates if the topCard does something
	 * 5. invokes getPlayableCards, a class method that filters each players hand to find the playable cards and it stores it into a list of cards
	 */
	
	public void start() {
		
		// Shuffle the deck and deal cards to each player
        deck.shuffleCards();
        
        //get hands to players
        getPlayersHands(deck, iterator);
		System.out.println("MAIN: " +iterator.getHandsMap());
		
		//extracting the first card
		Card firstCard = checkFirstCard(deck);
//		Card firstCard = new Card(Color.JOLLY, Value.CAMBIO_COLORE, Value.CAMBIO_COLORE.get);
		System.out.println("MAIN: Current card: " + firstCard);
		
		boolean isFirst = true;
		//evaluating which action the first card on top do
		checkCardAction(firstCard, isFirst);
		isFirst = false;
		
		System.out.println("MAIN:" + currentColor +" " +currentValue);
		
		//calculating for each player their playable cards
		List<Card> playableCards = getPlayableCards(firstCard, iterator);
		
		System.out.println("MAIN: It's " + iterator.getCurrentPlayer().getNickname() + " turn!" + playableCards);
		
		
		
	}
	
	/**
	 * This method checks if the first extracted card is valid.
	 * If the first card is a PIU_QUATTRO wild card, it keeps drawing cards until it's another card.
	 * @param deck is the current deck of the game
	 * @return the first card
	 */
	private Card checkFirstCard(Deck deck) {
		Card firstCard = deck.drawCard();
		//Card firstCard = new Card(Color.JOLLY, Value.CAMBIO_COLORE, Value.CAMBIO_COLORE.getScore());
		while (firstCard.getValue().equals(Value.PIU_QUATTRO)) {
            
            deck.restartInvalidDeck(firstCard);
            firstCard = deck.drawCard();
        }
		return firstCard;
	}
	
	/**
	 * The core of the following method is a switch that decides which action method to call based on the value of the extracted card.
	 * @param topCard the extracted card
	 * @param isFirst a boolean which tells the method if is the first card extracted or not 
	 */
	private void checkCardAction(Card topCard, boolean isFirst) {
		
		currentColor = topCard.getColor();
		currentValue = topCard.getValue();
		switch (currentValue) {
		    case SALTO -> {
		    	discardCard(topCard);
		    	skipAction();
		    }
		    case INVERTI -> {
		    	discardCard(topCard);
		    	reverseCard();
		    }
		    case PIU_DUE -> {
		    	discardCard(topCard);
		    	if (isFirst) drawTwoAction();
		    	else {
		    		iterator.nextPlayer();
		    		drawTwoAction();
		    	}
		    }
		    case PIU_QUATTRO -> {
		    	discardCard(topCard);
		    	currentColor = chooseColor(currentColor);
		    	drawFourJolly();
		    }
		    case CAMBIO_COLORE -> {
		    	discardCard(topCard);
		    	// change color method: for now it picks a random color
		    	// handle it for CAMBIO_COLORE and PIU_QUATTRO
		    	currentColor = chooseColor(currentColor);
		    	if (isFirst) System.out.println(iterator.getCurrentPlayer().getNickname() + " has chosen the color " + currentColor);
		    	else iterator.nextPlayer();
		    }
		    default -> {
		    	discardCard(topCard);
		    	System.out.println("SWITCH: A numbered card has been extracted");
		    }
		}
		
	}
	
	/**
	 * This method is invoked when a SALTO card is played.
	 */
	private void skipAction() {
		iterator.nextPlayer();
		System.out.println("SKIP: New current player: " + iterator.getCurrentPlayer().getNickname());
	}
	
	/**
	 * This method is invoked when a INVERTI card is played.
	 */
	private void reverseCard() {
		iterator.reverseDirection();
		iterator.nextPlayer();
		System.out.println("REVERSE: New current player: " + iterator.getCurrentPlayer().getNickname());
	}
	
	/**
	 * This method is invoked when a PIU_DUE card is played.
	 */
	private void drawTwoAction() {
		for (int i = 0; i<2; i++) {	
			iterator.getHandsMap().get(iterator.getCurrentPlayer().getNickname()).add(deck.drawCard());
		}
		System.out.println("DRAW TWO: " + iterator.getCurrentPlayer().getNickname() + " " + iterator.getHandsMap().get(iterator.getCurrentPlayer().getNickname()));
		//the player that draws 2 cards skips the turn
		iterator.nextPlayer();
		System.out.println("DRAW TWO: New current player: " + iterator.getCurrentPlayer().getNickname());
	}
	
	/**
	 * This method is invoked when a PIU_QUATTRO card is played.
	 */
	private void drawFourJolly() {
		for (int i = 0; i<4; i++) {	
			iterator.getHandsMap().get(iterator.nextPlayer().getNickname()).add(deck.drawCard());
		}
		System.out.println("DRAW FOUR: " + iterator.getCurrentPlayer().getNickname() + " " + iterator.getHandsMap().get(iterator.getCurrentPlayer().getNickname()));
		
		//the player that draws 4 cards skips the turn
		iterator.nextPlayer();
		System.out.println("DRAW FOUR: New current player: " + iterator.getCurrentPlayer().getNickname());
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
	
	
	private void getPlayersHands(Deck currentDeck, PlayersIterator iterator) {
		for (Player player : iterator.getPlayers()) {
			ArrayList<Card> hand = new ArrayList<Card>();
            for (int i = 0; i < 7; i++) {
                hand.add(deck.drawCard());
            }
            iterator.getHandsMap().put(player.getNickname(), hand);
		}
	}
	
	private List<Card> getPlayableCards(Card topCard, PlayersIterator iterator) {
		
		
		String currentPlayer = iterator.getCurrentPlayer().getNickname();
		List<Card> playableCards = iterator.getHandsMap().get(currentPlayer)
				.stream().filter(w -> w.getColor()==currentColor || w.getValue()==currentValue || w.getValue()==Value.CAMBIO_COLORE || w.getValue()==Value.PIU_QUATTRO)
				.collect(Collectors.toList());
		return playableCards.stream().filter(x->x.getColor()==currentColor).count()>0? 
			       playableCards.stream().filter(x->x.getValue()!=Value.PIU_QUATTRO).collect(Collectors.toList()):playableCards;
	}
	
	private Card playCard(Card card, PlayersIterator iterator) {
		List<Card> playableCards = getPlayableCards(card, iterator);
		// the player has to choose the card from playableCards
		return card;
		
	}
}



