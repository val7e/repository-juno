/**
 * 
 */
package juno.model.cardDeck;

/**
 * @author val7e
 *
 */
public class CardJolly extends Card {
	
	/**
	 * This is constructor method
	 * @param jolly
	 */
	public CardJolly (Jolly jolly) {
		this.jolly = jolly;
	}
	
	private final int score = 50;
	
	@Override
	public int getScore() {
		return score;
	}
	
	@Override
	public String toString() {
		return this.getJolly() + "";
	}
}
