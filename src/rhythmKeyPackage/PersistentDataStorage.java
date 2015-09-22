package rhythmKeyPackage;

public class PersistentDataStorage {

	public PersistentDataStorage(String userName){
		this.userName = userName;
	}
	
	String userName;
	
	public void storeData(Session s){
		//Chris: Implement this
	}
	
	public int getCurrentSessionId(){
		return 0; //Chris: Change this for the method that looks at the number on the last line of the file, yoou can assume userName is filled out with the user name.
	}
}
