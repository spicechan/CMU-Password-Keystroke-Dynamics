package rhythmKeyPackage;

public class Main {

	private PersistentDataStorage pds;
	
	public void startSession(){
		UserInput ui = new UserInput(this);
		ui.start();
	}
	
	public void storeUsername(String username) {
		pds = new PersistentDataStorage(username);
	}
	
	public void storeSession(Session session) {
		pds.storeData(session);
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		m.startSession();
	}

}
