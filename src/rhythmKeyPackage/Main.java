package rhythmKeyPackage;

public class Main {

	private PersistentDataStorage pds;
	private String username;
	
	public void startSession(){
		UserInput ui = new UserInput(this);
		ui.start();
	}
	
	public void storeUsername(String username) {
		this.username = username;
	}
	
	public void storeSession(Session session) {
		this.pds = new PersistentDataStorage(username);
		this.pds.storeData(session);
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		m.startSession();
	}

}
