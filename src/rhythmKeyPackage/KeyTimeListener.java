package rhythmKeyPackage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;


public class KeyTimeListener implements KeyListener {
	
	private KeyPress currentKeyPress;
	private ArrayList<KeyPress> keyPressList;
	private boolean isEnter;

	public KeyTimeListener() {
		currentKeyPress = new KeyPress();
		keyPressList = new ArrayList<KeyPress>();
		isEnter = false;
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
		currentKeyPress = new KeyPress();
	}

	/** Handle the key-released event from the text field. */
	public void keyReleased(KeyEvent e) {
		//Sets special flag if the last keyup is enter
		int keyCode = e.getKeyCode();
		if (e.VK_ENTER == keyCode) {
			isEnter = true;		
		}
		
		//process key-released event
		this.currentKeyPress.setKeyIdentifier(e);
		this.currentKeyPress.setKeydown(0);
		this.currentKeyPress.setKeyup(System.currentTimeMillis());
		keyPressList.add(currentKeyPress);
		currentKeyPress = new KeyPress();
	}
	
	public ArrayList<KeyPress> getKeyPressList() {
		ArrayList<KeyPress> temp = keyPressList;
		keyPressList = new ArrayList<KeyPress>();
		return temp;
	}
	
	public boolean isEnter() {
		return isEnter;
	}
	
}
