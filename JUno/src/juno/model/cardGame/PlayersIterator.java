/**
 * 
 */
package juno.model.cardGame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import juno.model.cardDeck.Card;
import juno.model.cardPlayers.*;


/**
 * @author val7e
 *
 */
public class PlayersIterator implements Iterable<Player> {
	private List<Player> players;
	private HashMap<String, List<Card>> handsMap;
	private int current = 0;
	private Direction direction = Direction.CLOCKWISE;
	
	public PlayersIterator(List<Player> players) {
		super();
		this.players = players;
		this.handsMap = new HashMap<String, List<Card>>();
	}

	@Override
	public Iterator<Player> iterator() {
		return new Iterator<Player>() {

            @Override
            public boolean hasNext() {
				return true; // circular loop
            }

            @Override
            public Player next() {
				int increment = direction == Direction.CLOCKWISE ? 1 : -1;
				int next = (players.size() + current + increment) % players.size();
				return players.get(next);
			}

            @Override
            public void remove() {
                throw new UnsupportedOperationException("no changes allowed");
            }
		};
	}
	
	/**
	 * This method returns a stream of the players nicknames.
	 * @return
	 */
//	public Stream<Player> stream() {
//		return Arrays.stream(players);
//		
//	}
	
	public List<Player> getPlayers(){
		return players;
	}
	
	public Player getCurrentPlayer() {
		return players.get(current);
	}
	
	/**
	 * The default direction in the game is from left to right,
	 * so clockwise.
	 */
	public void reverseDirection() {
		direction = Direction.COUNTER_CLOCKWISE;
	}
	
	private int getNextPlayerIndex() {
		int increment = direction == Direction.CLOCKWISE ? 1 : -1;
		return (players.size() + current + increment) % players.size();
	}
	
	public Player nextPlayer() {
		current = getNextPlayerIndex();
		return getCurrentPlayer();
	}
	
	public PlayersIterator setHand(String nickname, ArrayList<Card> hand) {
		this.handsMap.put(nickname, hand);
		return this;
	}

	public List<Card> getHand(String nickname) {
		return this.handsMap.get(nickname);
	}

	public List<Card> getCurrentPlayerHand() {
		return this.getHand(this.getCurrentPlayer().getNickname());
	}

	public HashMap<String, List<Card>> getHandsMap() {
		return handsMap;
	}
}