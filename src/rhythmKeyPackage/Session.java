package rhythmKeyPackage;

import java.util.List;
import java.util.ArrayList;

public class Session {
	
	private List<KeyPress> keyStrokes = new ArrayList<KeyPress>();
	
	public Session() {
	}
	
	public Session(List<KeyPress> keyStrokes) {
		this.keyStrokes = keyStrokes;
	}

	public List<KeyPress> getKeyStrokes() {
		return keyStrokes;
	}

	public void setKeyStrokes(List<KeyPress> keyStrokes) {
		this.keyStrokes = keyStrokes;
	}
	
}
