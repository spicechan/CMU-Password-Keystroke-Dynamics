package rhythmKeyPackage;

import java.awt.event.KeyEvent;

public class KeyPress {
	
	/* KeyEvent allows us to get useful information about the key press.
	 * Check Oracle documentation. 
	 */
	private KeyEvent keyIdentifier; 
	/* When key is pressed measured in
	 * time in milliseconds since program began.
	 */
	private long keydown;
	/* When key is released measured in
	 * time in milliseconds since program began.
	 */
	private long keyup;
	
	public KeyPress() {	
	}
	
	public KeyPress(KeyEvent keyIdentifier, long keydown, long keyup) {
		this.keyIdentifier = keyIdentifier;
		this.keydown = keydown;
		this.keyup = keyup;
	}
	
	public KeyEvent getKeyIdentifier() {
		return keyIdentifier;
	}
	
	public void setKeyIdentifier(KeyEvent keyIdentifier) {
		this.keyIdentifier = keyIdentifier;
	}
	
	public long getKeydown() {
		return keydown;
	}
	
	public void setKeydown(long keydown) {
		this.keydown = keydown;
	}
	
	public long getKeyup() {
		return keyup;
	}
	
	public void setKeyup(long keyup) {
		this.keyup = keyup;
	} 
	
}
