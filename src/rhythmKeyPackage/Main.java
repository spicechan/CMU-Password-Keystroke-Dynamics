package rhythmKeyPackage;

public class Main {

	private PersistentDataStorage pds;
	private String username;
	private int counter;
	
	public void startSession(){
		UserInput ui = new UserInput(this);
		ui.start();
		counter = 0;
	}
	
	public void storeUsername(String username) {
		this.username = username;
	}
	
	public void storeSession(Session session) {
		this.pds = new PersistentDataStorage(username);
		this.pds.storeData(session);
		counter++;
		System.out.println("Password typed: " + counter + " times.");
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		m.startSession();
	}

}
