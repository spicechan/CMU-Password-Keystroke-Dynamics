package rhythmKeyPackage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTextField;


public class KeyTimeListener implements KeyListener {
	
	private KeyPress currentKeyPress;
	private JTextField typingArea;
	private ArrayList<KeyPress> keyPressList;

	public KeyTimeListener() {
		currentKeyPress = new KeyPress();
		keyPressList = new ArrayList<KeyPress>();
		typingArea = new JTextField(20);
		typingArea.addKeyListener(this);

		// Disables focus traversal and the Tab events
		// become available to the key event listener
		typingArea.setFocusTraversalKeysEnabled(false);
	}

	/** Handle the key typed event from the text field. */
	public void keyTyped(KeyEvent e) {
	}

	/** Handle the key-pressed event from the text field. */
	public void keyPressed(KeyEvent e) {
		this.currentKeyPress.setKeydown(System.currentTimeMillis());
		this.currentKeyPress.setKeyIdentifier(e);
	}

	/** Handle the key-released event from the text field. */
	public void keyReleased(KeyEvent e) {
		this.currentKeyPress.setKeyup(System.currentTimeMillis()); 
		keyPressList.add(currentKeyPress);
	}
	
	public List<KeyPress> getKeyPressList() {
		return keyPressList;
	}
}
