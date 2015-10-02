package rhythmKeyPackage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;


public class KeyTimeListener implements KeyListener {
	
	private KeyPress currentKeyPress;
	
	private ArrayList<KeyPress> keyPressList;

	public KeyTimeListener() {
		currentKeyPress = new KeyPress();
		keyPressList = new ArrayList<KeyPress>();
	}

	/** Handle the key typed event from the text field. */
	public void keyTyped(KeyEvent e) {
	}

	/** Handle the key-pressed event from the text field. */
	public void keyPressed(KeyEvent e) {
		this.currentKeyPress.setKeyIdentifier(e);
		this.currentKeyPress.setKeydown(System.currentTimeMillis());
		this.currentKeyPress.setKeyup(0);
		keyPressList.add(currentKeyPress);
	}

	/** Handle the key-released event from the text field. */
	public void keyReleased(KeyEvent e) {
		this.currentKeyPress.setKeyIdentifier(e);
		this.currentKeyPress.setKeydown(0);
		this.currentKeyPress.setKeyup(System.currentTimeMillis()); 
		keyPressList.add(currentKeyPress);
	}
	
	public void resetKeyPressList() {
		keyPressList = new ArrayList<KeyPress>(); 
	}
	
	public List<KeyPress> getKeyPressList() {
		for (KeyPress k1 : keyPressList) {
			if (k1.getKeyup() == 0) {
				for (KeyPress k2 : keyPressList) {
					if (k1.getKeyIdentifier().getID() == k2.getKeyIdentifier().getID() && k2.getKeydown() == 0) {
						keyPressList.remove(k2);
						k1.setKeyup(k2.getKeyup());
					}
				}
			}
		}
		return keyPressList;
	}
	
}
