package rhythmKeyPackage;

import java.util.Scanner;

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
		if (counter == 1) {
			System.out.println("Password typed: " + counter + " time.");
		}
		else {
			System.out.println("Password typed: " + counter + " times.");
		}
	}
	
	public void readAndStoreDenied(String filename) {
		this.pds = new PersistentDataStorage(filename);
		this.pds.readArff();
	}
	
	public static void main(String[] args) {
		// Either type passwords or create denied data
		
		Main m = new Main();
		
		System.out.println("Do you want to type a new password or create denied data?\n" +
				"Type new password: 1\n" +
				"Create denied data: 2");
		int choice = 0;
		Scanner scan = new Scanner(System.in);
		while (choice != 1 && choice != 2) {
			System.out.println("Choice must be 1 or 2: ");
			String choiceString = scan.next();
			try {
				choice = Integer.parseInt(choiceString);
			}
			catch (NumberFormatException e) {
				choice = 0;
			}
		}
		
		if (choice == 1) {
			// Start GUI to type password
			m.startSession();
		}
		else if (choice == 2) {
			// Read in data from arff file and create denied data
			System.out.print("Name of file (without .arff): ");
			String filename = scan.next();
			m.readAndStoreDenied(filename);
			System.out.println("Denied data was created.\nCheck " + filename + ".arff to see results.");
		}
		
		scan.close();
	}

}
