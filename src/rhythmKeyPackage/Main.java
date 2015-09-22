package rhythmKeyPackage;

public class Main {

	public void startSession(){
		String userName = getUserName();
		PersistentDataStorage pds = new PersistentDataStorage(userName);
		UserInput ui = new UserInput(pds);
		Session session = ui.getSession();
		pds.storeData(session);
	}
	
	private String getUserName(){
		return "";//Mau, make this method
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		m.startSession();
	}

}
