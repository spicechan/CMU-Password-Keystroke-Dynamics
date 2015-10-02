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
	private double keydown;
	/* When key is released measured in
	 * time in milliseconds since program began.
	 */
	private double keyup;
	
	public KeyPress() {	
	}
	
	public KeyPress(KeyEvent keyIdentifier, long keydown, long keyup) {
		super();
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
	
	public double getKeydown() {
		return keydown;
	}
	
	public void setKeydown(long keydown) {
		this.keydown = keydown;
	}
	
	public double getKeyup() {
		return keyup;
	}
	
	public void setKeyup(long keyup) {
		this.keyup = keyup;
	} 
	
}
