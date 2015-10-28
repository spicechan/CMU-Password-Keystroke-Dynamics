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
import java.util.Scanner;

public class PersistentDataStorage {

	public PersistentDataStorage(String userName){
		this.path = userName + ".arff";
		this.userName = userName;
	}

	String path;
	String userName;
	int buildTheDeniedCasesWhenItReachesThisAmountOfSessions = 50;

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
	public Session cleanData(Session s) {
		
		List<KeyPress> keyStrokes = s.getKeyStrokes();
		
		//reverse list to process backspaces correctly
		int length = keyStrokes.size();
		List<KeyPress> keyStrokesReversed = new ArrayList<KeyPress>();
		for (int i = length - 1; i >= 0; i--) {
			keyStrokesReversed.add(keyStrokes.get(i));
		}
		
		int backspaceCounter = 0;
		
		for (KeyPress k : keyStrokesReversed) {
			Scanner scan = new Scanner(k.getKeyIdentifier().paramString());
			scan.useDelimiter(",");
			if (scan.next().equals("KEY_PRESSED")) {
				
			}
			System.out.println(k.getKeyIdentifier().paramString());
		}
		
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
		
		final int shiftCode = 16;
		final int capsCode = 20; 
		final int backspaceCode = 8;

		//Print the header if first time
		if (id == 0){
			printer.print("@relation ");
			printer.print(userName);
			printer.println(".accepted.passwords.keystroke.rhythm");

			List<KeyPress> keyStrokes = s.getKeyStrokes();

			//Add the parameters
			for(int i = 0; i < keyStrokes.size(); i++){
				KeyPress k1 = keyStrokes.get(i);
				KeyEvent ke1 = k1.getKeyIdentifier();
				int keyCode = ke1.getKeyCode();
				if ((keyCode != shiftCode) && (keyCode != capsCode) && (keyCode != backspaceCode)){
					txt = "@attribute keyPressed";
					txt += Integer.toString(i + id);
					txt += " numeric";
					printer.println(txt);
					txt = "@attribute flightTime";
					txt += Integer.toString(i + id);
					txt += " numeric";
					printer.println(txt);
					txt = "@attribute dwellTime";
					txt += Integer.toString(i + id);
					txt += " numeric";
					printer.println(txt);
				}
			}
			txt = "@attribute capsPresses numeric";
			printer.println(txt);
			txt = "@attribute shiftPresses numeric";
			printer.println(txt);
			txt = "@attribute backspacePresses numeric";
			printer.println(txt);

			printer.println("@attribute class {accepted,denied}");
			printer.println("");
			printer.println("@data");
			printer.print("% Accepted data");
			
		}

		//now write data
		List<KeyPress> keyStrokes = s.getKeyStrokes();
		printer.println();
		int capsPresses = 0, shiftPresses = 0, backspacePresses = 0;
		for(int i = 0; i < keyStrokes.size(); i++){
			txt = "";
			KeyPress k1 = keyStrokes.get(i);
			KeyEvent ke1 = k1.getKeyIdentifier();
			int keyCode = ke1.getKeyCode();
			txt += keyCode;
			txt += ",";
			if (i + 1 < keyStrokes.size()){
				KeyPress k2 = keyStrokes.get(i+1);
				txt += (int)(k2.getKeydown() - k1.getKeyup());
			}
			else txt += "0";
			txt += ",";
			txt += (int)(k1.getKeyup() - k1.getKeydown());	
			txt += ",";
			if (keyCode == capsCode) capsPresses++;
			else if (keyCode == shiftCode) shiftPresses++;
			else if (keyCode == backspaceCode) backspacePresses++;
			else printer.print(txt);
		}
		
		printer.print(capsPresses + ",");
		printer.print(shiftPresses  + ",");
		printer.print(backspacePresses + ",");

		txt = "accepted";
		printer.print(txt);

		/// When the user gets to X sessions it builds the denied cases
		if(getNumberOfSessionsSoFar() <= buildTheDeniedCasesWhenItReachesThisAmountOfSessions){
			//createDeniedData(printer, s);
		}
		

		printer.close();
	}

	private void createDeniedData(PrintWriter printer, Session s){
		int amountOfKeyPresses = s.getKeyStrokes().size();
		int amountOfSessions = 50; // Just a random maximum of sessions per user
		int FlightOrDwell = 2;

		String[] keysPressed = new String[amountOfKeyPresses];
		double[][][] flightAndDwellData = new double[amountOfKeyPresses][amountOfSessions][FlightOrDwell];

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

		for (int i = 0; i < numberOfSessions; i++) {
			printer.println();
			String txt = "";
			for (int keypressed = 0; keypressed < keysPressed.length; keypressed++) {

				int aboveOrBelowMean = rand.nextBoolean()? 1 : -1; //1 for creating denied data above the std deviation, -1 for creating denied data below the std deviation
				//I'm adding a 1 here below because I think to be denied it should be at least 100% over the std dev. nextRand returns something between 0 and 1. So this will always be between 100% and 200% over the std dev.
				float percentageOfIncreaseOfDeniedData = 1 + rand.nextFloat(); // percentage above the std deviation. 0.3 means 30%, 1 means 100% more and so on. So if mean is 10, and std dev is 2, then it would introduce a 14 (mean (10) + std dev (2) + 100% increase (2) )

				txt += keysPressed[keypressed];
				txt += ",";
				txt +=  (int) ( meanFlightAndDwellData[keypressed][FLIGHT] + (aboveOrBelowMean * (standVariationFlightAndDwellData[keypressed][FLIGHT] + (percentageOfIncreaseOfDeniedData * standVariationFlightAndDwellData[keypressed][FLIGHT]))));
				txt += ",";
				txt += (int) (meanFlightAndDwellData[keypressed][DWELL] + (aboveOrBelowMean * (standVariationFlightAndDwellData[keypressed][DWELL] + (percentageOfIncreaseOfDeniedData * standVariationFlightAndDwellData[keypressed][DWELL]))));
				txt += ",";
			}
			txt += "denied";
			printer.print(txt);
		}

		//Write the denied data in the file (completely different from the original)
		printer.println();
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
		}

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
				if (sCurrentLine.equalsIgnoreCase("@data")){
					startGatheringData = true;
					sCurrentLine = br.readLine(); //Move to the next line from the one with "@data"
				}
				if(startGatheringData){
					String[] elementsSeparatedByCommas = sCurrentLine.split(",");
					for (int i = 0; i < elementsSeparatedByCommas.length - 1; i+=3) {
						keysPressed[i/3] = elementsSeparatedByCommas[i];
						flightAndDwellData[i/3][sessionIterator][FLIGHT] = Double.parseDouble(elementsSeparatedByCommas[i+1]);
						flightAndDwellData[i/3][sessionIterator][DWELL] = Double.parseDouble(elementsSeparatedByCommas[i+2]);
					}
					sessionIterator++;
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
				if (sCurrentLine.equalsIgnoreCase("@data")){
					startGatheringData = true;
					sCurrentLine = br.readLine(); //Move to the next line from the one with "@data"
				}
				if(startGatheringData){
					sessionIterator++;
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
