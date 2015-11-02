package rhythmKeyPackage;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PersistentDataStorage {

	public PersistentDataStorage(String userName){
		this.path = userName + ".arff";
		this.userName = userName;
	}

	String path;
	String userName;
	int buildTheDeniedCasesWhenItReachesThisAmountOfSessions = 50;
	//int amountOfSpecialKeysWeAreStoring = 3;

	public int getSessionId(){

		FileReader fileReader = null;
		try {
			fileReader = new FileReader(path);
		}
		catch (FileNotFoundException e) {
			return -1;
		}

		BufferedReader reader = new BufferedReader(fileReader);

		String line = null, newLine;
		try {
			while ((newLine = reader.readLine()) != null){
				line = newLine;
				//System.out.print(line);
			}
		} catch (IOException e) {
			return -1;
		}
		try {
			reader.close();
		} catch (IOException e) {
			return -1;
		}
		if(line == null){
			return -1;
		}
		return 1;
	}
	
	/**Cleans the data by removing backspaced characters.
	 * Leaves backspace and other special key keystrokes
	 * in place to be processed later.
	 * 
	 * @param s - the session to be cleaned
	 * @return the cleaned session
	 */
	public synchronized Session cleanData(Session s) {
		
		List<KeyPress> keyStrokes = s.getKeyStrokes();
		int backspaceKeycode = 8;
		
		//reverse list to process backspaces correctly
		int length = keyStrokes.size();
		List<KeyPress> keyStrokesReversed = new ArrayList<KeyPress>();
		for (int i = length - 1; i >= 0; i--) {
			keyStrokesReversed.add(keyStrokes.get(i));
		}
		
		int backspaceCounter = 0;
		int backspaceTotal = 0;
		
		//process backspaces
		List<KeyPress> keyStrokesReversedBack = new ArrayList<KeyPress>();
		for (int i = 0; i < length; i++) {
			KeyPress k = keyStrokesReversed.get(i);
			//Scanner scan = new Scanner(k.getKeyIdentifier().paramString());
			//scan.useDelimiter(",");
			//if(k.getKeyIdentifier().getExtendedKeyCode() == backspaceKeycode) {
			KeyEvent event = k.getKeyIdentifier();
			if(event.VK_BACK_SPACE == event.getKeyCode()
					|| event.VK_DELETE == event.getKeyCode()) {
				backspaceCounter++;
				backspaceTotal++;
			}
			else if (backspaceCounter > 0) {
				backspaceCounter --;
			}
			else {
				keyStrokesReversedBack.add(k);
			}
			//scan.close();
			//System.out.println(k.getKeyIdentifier().paramString());
		}
		
		//reverse list to put back in correct order
		length = keyStrokesReversedBack.size();
		List<KeyPress> keyStrokesBackspaced = new ArrayList<KeyPress>();
		for (int i = length - 1; i >= 0; i--) {
			keyStrokesBackspaced.add(keyStrokesReversedBack.get(i));
			System.out.println(keyStrokesReversedBack.get(i).getKeyIdentifier().paramString());
		}
		
		s.setKeyStrokes(keyStrokesBackspaced);
		
		//temp return value
		return s;
	}

	public void storeData(Session s){
		s = cleanData(s);
		//Set up to write to txt file
		FileWriter write = null;
		try {
			write = new FileWriter(path, true);
		} catch (IOException e) {
			System.out.println("Error storing Data");
			return;
		}
		PrintWriter printer = new PrintWriter(write);

		String txt = "";

		//Now write data to the file
		int id = getSessionId() + 1;

		//Print the header if first time
		if (id == 0){
			printer.print("@relation ");
			printer.print(userName);
			printer.println(".accepted.passwords.keystroke.rhythm");

			List<KeyPress> keyStrokes = s.getKeyStrokes();

			//Add the parameters
			for(int i = 0; i < keyStrokes.size(); i++){
				txt = "@attribute flightTime";
				txt += Integer.toString(i + id);
				txt += " numeric";	
				printer.println(txt);
				txt = "@attribute dwellTime";
				txt += Integer.toString(i + id);
				txt += " numeric";
				printer.println(txt);
			}

			printer.println("@attribute class {accepted,denied}");
			printer.println("");
			printer.println("@data");
			printer.print("% Accepted data");

		}

		//now write data
		List<KeyPress> keyStrokes = s.getKeyStrokes();
		printer.println();
		for(int i = 0; i < keyStrokes.size(); i++){
			txt = "";
			KeyPress k1 = keyStrokes.get(i);
			if (i + 1 < keyStrokes.size()){
				KeyPress k2 = keyStrokes.get(i+1);
				txt += (int)(k2.getKeydown() - k1.getKeyup());
			}
			else txt += "0";
			txt += ",";
			txt += (int)(k1.getKeyup() - k1.getKeydown());	
			txt += ",";
			printer.print(txt);
		}

		txt = "accepted";
		printer.print(txt);
		

		/// When the user gets to X sessions it builds the denied cases
		if(getNumberOfSessionsSoFar() == buildTheDeniedCasesWhenItReachesThisAmountOfSessions){
			//createDeniedData(printer, s);
		}

		printer.close();
	}

	private void createDeniedData(PrintWriter printer, Session s){
		int amountOfKeyPresses = s.getKeyStrokes().size();
		int amountOfSessions = buildTheDeniedCasesWhenItReachesThisAmountOfSessions;
		int FlightOrDwell = 2;

		String[] keysPressed = new String[amountOfKeyPresses];
		double[][][] flightAndDwellData = new double[amountOfKeyPresses][amountOfSessions][FlightOrDwell];
		//int[][] specialKeys = new int[amountOfSessions][amountOfSpecialKeysWeAreStoring];

		int numberOfSessions = fillArraysWithDataInFile(keysPressed, flightAndDwellData);

		final int FLIGHT = 0;
		final int DWELL = 1;

		//mean
		System.out.println("Mean");
		int[][] meanFlightAndDwellData = new int[amountOfKeyPresses][FlightOrDwell];

		for (int keypressed = 0; keypressed < flightAndDwellData.length; keypressed++) {

			int sumFlight = 0;
			int sumDwell = 0;
			

			for (int sessionNumber = 0; sessionNumber < numberOfSessions; sessionNumber++) {
				sumFlight += flightAndDwellData[keypressed][sessionNumber][FLIGHT];
				sumDwell += flightAndDwellData[keypressed][sessionNumber][DWELL];
				
			}
			meanFlightAndDwellData[keypressed][FLIGHT]= sumFlight/numberOfSessions;
			meanFlightAndDwellData[keypressed][DWELL]= sumDwell/numberOfSessions;
			

			//debug info
			System.out.print("Keypressed: " + keysPressed[keypressed]);
			System.out.print(", mean flight time: " + meanFlightAndDwellData[keypressed][FLIGHT]);
			System.out.println(", mean dwell time: " + meanFlightAndDwellData[keypressed][DWELL]);
		}
		
		//int meanSpecialKeys[] = new int[specialKeys.length];
		/*
		for (int i = 0; i < specialKeys.length; i++) {
			int sumSpecialKeys = 0;	
			for (int sessionNumber = 0; sessionNumber < numberOfSessions; sessionNumber++) {
				sumSpecialKeys += specialKeys[sessionNumber][i];
			}
			meanSpecialKeys[i]= sumSpecialKeys/numberOfSessions;
			
			//debug info
			System.out.print("Mean of key " + i + ": " + meanSpecialKeys[i]);
		}
		 */
		
		//std var
		System.out.println("Std variation");
		int[][] standVariationFlightAndDwellData = new int[amountOfKeyPresses][FlightOrDwell];

		for (int keypressed = 0; keypressed < flightAndDwellData.length; keypressed++) {

			double meanFlight = meanFlightAndDwellData[keypressed][FLIGHT];
			double tempFlight = 0;
			double meanDwell = meanFlightAndDwellData[keypressed][DWELL];
			double tempDwell = 0;
			

			for (int sessionNumber = 0; sessionNumber < numberOfSessions; sessionNumber++) {

				double valueToMultiplyFlight = meanFlight-flightAndDwellData[keypressed][sessionNumber][FLIGHT];
				double valueToMultiplyDwell = meanDwell-flightAndDwellData[keypressed][sessionNumber][DWELL];

				tempFlight += valueToMultiplyFlight*valueToMultiplyFlight;
				tempDwell += valueToMultiplyDwell*valueToMultiplyDwell;

			}
			standVariationFlightAndDwellData[keypressed][FLIGHT]= (int) Math.sqrt(tempFlight/numberOfSessions-1);
			standVariationFlightAndDwellData[keypressed][DWELL]= (int) Math.sqrt(tempDwell/numberOfSessions-1);

			//debug info
			System.out.print("Keypressed: " + keysPressed[keypressed]);
			System.out.print(", std variation flight time: " + standVariationFlightAndDwellData[keypressed][FLIGHT]);
			System.out.println(", std variation dwell time: " + standVariationFlightAndDwellData[keypressed][DWELL]);
		}

		//Write the denied data in the file (variations of the originals)
		printer.println();
		printer.print("% Denied data: variations of the originals");
		Random rand = new Random();

		for (int i = 0; i < numberOfSessions*2; i++) {
			printer.println();
			String txt = "";
			for (int keypressed = 0; keypressed < keysPressed.length; keypressed++) {

				int aboveOrBelowMean = rand.nextBoolean()? 1 : -1; //1 for creating denied data above the std deviation, -1 for creating denied data below the std deviation
				//I'm adding a 1 here below because I think to be denied it should be at least 100% over the std dev. nextRand returns something between 0 and 1. So this will always be between 100% and 200% over the std dev.
				float percentageOfIncreaseOfDeniedData = 1 + rand.nextFloat(); // percentage above the std deviation. 0.3 means 30%, 1 means 100% more and so on. So if mean is 10, and std dev is 2, then it would introduce a 14 (mean (10) + std dev (2) + 100% increase (2) )

				//txt += keysPressed[keypressed];
				//txt += ",";
				txt +=  (int) ( meanFlightAndDwellData[keypressed][FLIGHT] + (aboveOrBelowMean * (standVariationFlightAndDwellData[keypressed][FLIGHT] + (percentageOfIncreaseOfDeniedData * standVariationFlightAndDwellData[keypressed][FLIGHT]))));
				txt += ",";
				txt += (int) (meanFlightAndDwellData[keypressed][DWELL] + (aboveOrBelowMean * (standVariationFlightAndDwellData[keypressed][DWELL] + (percentageOfIncreaseOfDeniedData * standVariationFlightAndDwellData[keypressed][DWELL]))));
				txt += ",";
			}
			//Variations of the special keys just adds 1, or substracts 1. And if that is less than 0, then it assigns 0.
			/*for (int j = 0; j < specialKeys.length; j++) {
				int oneMoreOrOneLess = rand.nextBoolean()? 1 : -1;
				int smallVariationFromOriginal = meanSpecialKeys[j] + oneMoreOrOneLess;
				txt += (smallVariationFromOriginal)<0? 0: smallVariationFromOriginal;
				txt += ",";
			}*/
			txt += "denied";
			printer.print(txt);
		}

		//Write the denied data in the file (completely different from the original)
		/*printer.println();
		printer.print("% Denied data: completely different from the original");
		for (int i = 0; i < numberOfSessions*2; i++) {
			printer.println();
			String txt = "";
			//random password between 5 and 15 characters long
			int lengthOfPass = keysPressed.length; //5 + rand.nextInt(10);
			for (int keypressed = 0; keypressed < lengthOfPass; keypressed++) {

				int aboveOrBelowMean = rand.nextBoolean()? 1 : -1; //1 for creating denied data above the std deviation, -1 for creating denied data below the std deviation

				final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$&*";
				final int length = alphabet.length();

				txt += alphabet.charAt(rand.nextInt(length));
				txt += ",";
				txt += (int) (meanFlightAndDwellData[0][FLIGHT] + (aboveOrBelowMean * (standVariationFlightAndDwellData[0][FLIGHT] )));
				txt += ",";
				txt += (int) (meanFlightAndDwellData[0][DWELL] + (aboveOrBelowMean * (standVariationFlightAndDwellData[0][DWELL])));
				txt += ",";
			}
			txt += "denied";
			printer.print(txt);
		}*/

	}


	private int fillArraysWithDataInFile(String[] keysPressed, double[][][] flightAndDwellData){
		BufferedReader br = null;
		boolean startGatheringData = false;

		final int FLIGHT = 0;
		final int DWELL = 1;

		int sessionIterator = 0;

		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(this.userName + ".arff"));

			while ((sCurrentLine = br.readLine()) != null) {
				System.out.println(sCurrentLine);
				
				if(startGatheringData){
					String[] elementsSeparatedByCommas = sCurrentLine.split(",");
					int iterateThroughTheKeyStrokesNotTheSpecialKeys = elementsSeparatedByCommas.length - 1;
					for (int i = 0; i < iterateThroughTheKeyStrokesNotTheSpecialKeys; i+=2) {
						//keysPressed[i/3] = elementsSeparatedByCommas[i];
						flightAndDwellData[i/2][sessionIterator][FLIGHT] = Double.parseDouble(elementsSeparatedByCommas[i]);
						flightAndDwellData[i/2][sessionIterator][DWELL] = Double.parseDouble(elementsSeparatedByCommas[i+1]);
					}
					/*
					int lastIndexForSpecialKeys =  iterateThroughTheKeyStrokesNotTheSpecialKeys - 1;
					int j = 0;
					for (int i = iterateThroughTheKeyStrokesNotTheSpecialKeys; i < lastIndexForSpecialKeys; i++, j++) {
						specialKeys[sessionIterator][j] = Integer.parseInt(elementsSeparatedByCommas[i]);
					}*/
					sessionIterator++;
				}
				if (sCurrentLine.equalsIgnoreCase("% Accepted data")){
					startGatheringData = true;
					sCurrentLine = br.readLine(); //Move to the next line from the one with "Accepted data"
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return sessionIterator;
	}

	private int getNumberOfSessionsSoFar(){
		BufferedReader br = null;
		boolean startGatheringData = false;

		int sessionIterator = 0;

		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(this.userName + ".arff"));

			while ((sCurrentLine = br.readLine()) != null) {
				if(startGatheringData){
					sessionIterator++;
				}
				if (sCurrentLine.equalsIgnoreCase("% Accepted data")){
					startGatheringData = true;
					sCurrentLine = br.readLine(); //Move to the next line from the one with "% Accepted data"
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		return sessionIterator;
	}

	public int getCurrentSessionId(){
		return 0; //Chris: Change this for the method that looks at the number on the last line of the file, yoou can assume userName is filled out with the user name.
	}
}
