/**
 * 
 */
package juno.view;

/**
 * @author val7e
 *
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class JUnoGameTable {
    private JFrame frame;
    private JPanel playerPanel;
    private JPanel pilePanel;
    private JLabel drawPile;
    private JLabel discardPile;
    private JButton drawCard;

    public JUnoGameTable() {
        // Create the main frame
        frame = new JFrame();
        frame.setSize(800, 600);
        frame.setTitle("Uno Game Table");

        // Create the player panel
        playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(1, 4));
        playerPanel.setBackground(new Color(63, 115, 85));

        // Create the pile panel
        pilePanel = new JPanel();
        drawPile = new JLabel("Draw Pile");
        discardPile = new JLabel("Discard Pile");
        drawCard = new Button("Draw");
        pilePanel.add(drawPile);
        pilePanel.add(discardPile);
        pilePanel.add(drawCard);
        // Add the panels to the frame
 
        frame.setLayout(new BorderLayout());
        frame.add(playerPanel, BorderLayout.CENTER);
        frame.add(pilePanel, BorderLayout.SOUTH);

        // Set the frame visible
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        JUnoGameTable gameTable = new JUnoGameTable();
    }
}
