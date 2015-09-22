package rhythmKeyPackage;

import java.util.List;
import java.util.ArrayList;

public class Session {
	
	int sessionId;
	List<KeyPress> keyStrokes = new ArrayList<KeyPress>();

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public List<KeyPress> getKeyStrokes() {
		return keyStrokes;
	}

	public void setKeyStrokes(List<KeyPress> keyStrokes) {
		this.keyStrokes = keyStrokes;
	}

	public Session(int sessionId, List<KeyPress> keyStrokes) {
		super();
		this.sessionId = sessionId;
		this.keyStrokes = keyStrokes;
	}
	
}
