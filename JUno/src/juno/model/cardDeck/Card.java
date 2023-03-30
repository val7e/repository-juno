/**
 * 
 */
package juno.model.cardDeck;

/**
 * @author val7e
 *
 */
public class Card {
    
    private final Color color;
    private final Value value;
    private int score;

    public Card(Color color, Value value, int score) {
        this.color = color;
        this.value = value;
        this.score = score;
    }

    public Color getColor() {
        return color;
    }

    public Value getValue() {
        return value;
    }
    
    public int getScore() {
    	return score;
    }

    public boolean isActionCard() {
        return this.value == Value.CAMBIO_COLORE ||
        this.value == Value.PIU_DUE ||
        this.value == Value.PIU_QUATTRO ||
        this.value == Value.SALTO ||
        this.value == Value.CAMBIO_COLORE ||
        this.value == Value.INVERTI;
    }

    @Override
    public String toString() {
        return color + "_" + value;
    }
}

