package rhythmKeyPackage;

import java.util.List;

public class UserInput {
	
	public UserInput(PersistentDataStorage pds) {
		this.pds = pds;
	}
	
	Session session;
	PersistentDataStorage pds;
	
	Session getSession(){
		buildSession();
		return session;
	}
	
	private List<KeyPress> getKeyStrokes(){
		return null;//Laura, implement this method
	}
	
	public void buildSession(){
		session.setSessionId(pds.getCurrentSessionId());
		session.setKeyStrokes(getKeyStrokes());
	}

}
