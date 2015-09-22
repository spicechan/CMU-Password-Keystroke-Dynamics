package rhythmKeyPackage;

public class KeyPress {
	
	public KeyPress(String keyIdentifier, double keydown, double keyup) {
		super();
		this.keyIdentifier = keyIdentifier;
		this.keydown = keydown;
		this.keyup = keyup;
	}
	String keyIdentifier; 
	double keydown;
	double keyup;
	
	public String getKeyIdentifier() {
		return keyIdentifier;
	}
	public void setKeyIdentifier(String keyIdentifier) {
		this.keyIdentifier = keyIdentifier;
	}
	public double getKeydown() {
		return keydown;
	}
	public void setKeydown(double keydown) {
		this.keydown = keydown;
	}
	public double getKeyup() {
		return keyup;
	}
	public void setKeyup(double keyup) {
		this.keyup = keyup;
	} 
	
}
