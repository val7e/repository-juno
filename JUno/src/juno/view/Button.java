/**
 * 
 */
package juno.view;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;

/**
 * @author val7e
 *
 */
public class Button extends JButton {
	public Button(String text) {
		super(text);
		
		setForeground(Color.BLACK);
		
		setBackground(Color.PINK);
		
		setFont(new Font("Arial", Font.BOLD, 16));
	}
}
