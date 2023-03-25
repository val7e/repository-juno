package juno.model.cardGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;
import java.util.stream.Collectors;

import juno.model.cardDeck.Card;
import juno.model.cardDeck.Color;
import juno.model.cardDeck.Deck;
import juno.model.cardDeck.Value;
import juno.model.cardPlayers.PlayerDeprecated;
import juno.model.cardPlayers.Avatar;
import juno.model.cardPlayers.Player;

/**
 * 
 * @author val7e
 * 
 * The field currentDeck is the deck of the game
 * The field discardDeck is the pile of discard cards
 *
 */
public class GameDeprecated2 {
	
	private Deck deck = new Deck();
	private Stack<Card> discardDeck;
	private List<Player> players;
	private Color currentColor;
	private Value currentValue;
	private final PlayersIterator iterator = new PlayersIterator(players);
	
	
	private static TreeMap<String, List<DeprecCard>> playersHands = new TreeMap<String, List<DeprecCard>>();
	
	public GameDeprecated2(List<Player> players) {
		this.players = players;
        this.deck = new Deck();
        this.discardDeck = new Stack<Card>();
	}
	/**
	 * This method initialize the game following these points:
	 * 1. gives 7 cards for each player in players
	 * 2. it extracts the initial card from the currentDeck
	 * 3. adds the initial card to the discardDeck
	 * 4. invokes getPlayableCards, a class method that filters each players hand to find the playable cards
	 */
	
	public void start(String user) {
		
		// Shuffle the deck and deal cards to each player
        deck.shuffleCards();
		getPlayersHands(deck, players);
		
		//extracting the first card
		Card firstCard = checkFirstCard(deck);
		
		//adding the card to the discard deck
		discardDeck.add(firstCard);
		
		//evaluating which action the card on top do
		checkCardAction(firstCard);
		
		//calculating for each player their playable cards
		List<Card> playableCards = getPlayableCards(drawnCard, iterator);
		
		System.out.println(playableCards);
		
		
		
	}
	/**
	 * These following methods are about card actions.
	 * @param drawnCard
	 */
	public void checkCardAction(Card topCard) {
		
		currentColor = topCard.getColor();
		currentValue = topCard.getValue();
		switch (currentValue) {
		    case SALTO -> {
		    	skipAction();
		    }
		    case INVERTI -> {
		    	reverseCard();
		    }
		    case PIU_DUE -> {
		    	drawTwoAction();
		    }
		    case PIU_QUATTRO -> {
		    	drawFourJolly();
		    }
		    case CAMBIO_COLORE -> {
		    	//change color method
		    	currentColor = Color.BLU;
		    }
		default -> discardDeck.add(topCard);
		// ! this has to be fixed
		}
		
	}
	public void reverseCard(Card drawnCard) {
		iterator.reverseDirection();
	}
	public void drawTwoAction() {
		for (int i = 0; i<2; i++) {	
			playersHands.get(iterator.getCurrentPlayer().getNickname()).add(deck.drawCard());
		}
		//the player that draws 2 cards skips the turn
		iterator.nextPlayer();
	}
	
	public void drawFourJolly() {
		for (int i = 0; i<4; i++) {	
			playersHands.get(iterator.getCurrentPlayer().getNickname()).add(deck.drawCard());
		}
		//the player that draws 4 cards skips the turn
		iterator.nextPlayer();
	}
	
	public void skipAction() {
		
	}
	
	
	/**
	 * This method checks if the first extracted card is valid
	 * @param currentDeck
	 * @param drawnCard
	 * @param isFirstCard
	 * @return
	 */
	public Card checkFirstCard(Deck deck) {
		Card firstCard = deck.drawCard();
		while (firstCard.getValue().equals(Value.PIU_QUATTRO)) {
            // If the first card is a PESCA QUATTRO wild card, keep drawing cards until it's another card
            deck.restartInvalidDeck(firstCard);
            firstCard = deck.drawCard();
        }
		return firstCard;
	}
	
	/**
	 * This method creates a Player array
	 * @param name of the only real player of the game
	 * @return
	 */
//	public PlayerDeprecated[] initPlayers(String name) {
//		PlayerDeprecated playerUser = new PlayerDeprecated(name, true, Avatar.USER);
//		PlayerDeprecated playerBot0 = new PlayerDeprecated("jim", false, Avatar.JIM);
//		PlayerDeprecated playerBot1 = new PlayerDeprecated("pam", false, Avatar.PAM);	
//		PlayerDeprecated playerBot2 = new PlayerDeprecated("dwight", false, Avatar.DWIGHT);
//			
//		PlayerDeprecated[] builtPlayers = {playerUser, playerBot0, playerBot1, playerBot2};
//		return builtPlayers;
//	}
	
	public void getPlayersHands(Deck currentDeck, List<Player> players) {
		for (Player player : players) {
            for (int i = 0; i < 7; i++) {
                player.addCardToHand(deck.drawCard());
            }
		}
	}
	
	public List<Card> getPlayableCards(Card topCard, List<Player> players) {
		currentColor = topCard.getColor();
		currentValue = topCard.getValue();
		
		var currentPlayer = players.getCurrentPlayer().getNickname();
		System.out.println(currentPlayer + playersHands.get(iterator.getCurrentPlayer().getNickname()));
		var playableCards = playersHands.get(iterator.getCurrentPlayer().getNickname())
				.stream().filter(w -> w.getNumber()==card || w.getAction()==card || w.getColor()==drawnCard.getColor() || w.getType()=="Jolly").collect(Collectors.toList());
		return playableCards.stream().filter(x->x.getColor()==drawnCard.getColor()).count()>0? 
			       playableCards.stream().filter(x->x.getJolly()!=DeprecJolly.JOLLYPESCAQUATTRO).collect(Collectors.toList()):playableCards;
	}
}
