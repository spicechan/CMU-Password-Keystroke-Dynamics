package rhythmKeyPackage;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class PersistentDataStorage {
	
	private String path;
	private String userName;
	private int buildTheDeniedCasesWhenItReachesThisAmountOfSessions = 100;
	private int backspaceTotal = 0;
	//private int amountOfSpecialKeysWeAreStoring = 3;

	public PersistentDataStorage(String userName){
		this.path = userName + ".arff";
		this.userName = userName;
	}

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
	
	/**Cleans the data by removing backspaced characters and backspaces.
	 * Leaves other special key keystrokes
	 * in place to be processed later.
	 * 
	 * @param s - the session to be cleaned
	 * @return the cleaned session
	 */
	public Session cleanData(Session s) {
		
		List<KeyPress> keyStrokes = s.getKeyStrokes();
		//int backspaceKeycode = 8;
		
		//reverse list to process backspaces correctly
		int length = keyStrokes.size();
		List<KeyPress> keyStrokesReversed = new ArrayList<KeyPress>();
		for (int i = length - 1; i >= 0; i--) {
			keyStrokesReversed.add(keyStrokes.get(i));
		}
		
		int backspaceCounter = 0;
		
		//process backspaces
		List<KeyPress> keyStrokesReversedBack = new ArrayList<KeyPress>();
		for (int i = 0; i < length; i++) {
			KeyPress k = keyStrokesReversed.get(i);
			//printing for debugging
			//Scanner scan = new Scanner(k.getKeyIdentifier().paramString());
			//scan.useDelimiter(",");
			//if(k.getKeyIdentifier().getExtendedKeyCode() == backspaceKeycode) {
			KeyEvent event = k.getKeyIdentifier();
			int keyCode = event.getKeyCode();
			if(event.VK_BACK_SPACE == keyCode
			|| event.VK_DELETE == keyCode) {
				backspaceCounter++;
				backspaceTotal++;
			}
			else if (backspaceCounter > 0) {
				backspaceCounter --;
			}
			else {
				keyStrokesReversedBack.add(k);
			}
			//printing for debugging
			//scan.close();
			//System.out.println(k.getKeyIdentifier().paramString());
		}
		
		//reverse list to put back in correct order
		length = keyStrokesReversedBack.size();
		List<KeyPress> keyStrokesBackspaced = new ArrayList<KeyPress>();
		for (int i = length - 1; i >= 0; i--) {
			keyStrokesBackspaced.add(keyStrokesReversedBack.get(i));
			// Print statement for debugging
			//System.out.println(keyStrokesReversedBack.get(i).getKeyIdentifier().paramString());
		}
		
		//set return value
		s.setKeyStrokes(keyStrokesBackspaced);
		return s;
	}

	public void storeData(Session s){
		//clean data of backspaces
		s = cleanData(s);
		//Set up to write to txt file
		FileWriter write = null;
		try {
			write = new FileWriter(path, true);
		} catch (IOException e) {
			System.out.println("Error storing Data");
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
			printer.println("@attribute backspaceCount numeric");
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
			if (i + 1 < keyStrokes.size()){ //add flight
				KeyPress k2 = keyStrokes.get(i+1);
				txt += (k2.getKeydown() - k1.getKeyup());
			}
			else txt += "0";
			txt += ",";
			txt += (int)(k1.getKeyup() - k1.getKeydown()); //add dwell
			txt += ",";
			printer.print(txt);
		}
		
		txt = "" + backspaceTotal;
		txt += ",";
		printer.print(txt);
		txt = "accepted";
		printer.print(txt);
		

		/// When the user gets to X sessions it builds the denied cases
		//if(getNumberOfSessionsSoFar() == buildTheDeniedCasesWhenItReachesThisAmountOfSessions){
			//createDeniedData(printer, s);
		//}

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
			System.out.print("Keypressed: " + keypressed);
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
			System.out.print("Keypressed: " + keypressed);
			System.out.print(", std variation flight time: " + standVariationFlightAndDwellData[keypressed][FLIGHT]);
			System.out.println(", std variation dwell time: " + standVariationFlightAndDwellData[keypressed][DWELL]);
		}

		//Write the denied data in the file (variations of the originals)
		//printer.println();
		//printer.print("% Denied data: variations of the originals");
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
			txt += "0,denied";
			//printer.print(txt);
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
					int iterateThroughTheKeyStrokesNotTheSpecialKeys = elementsSeparatedByCommas.length - 2;
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

	/**
	 * Reads the flight time, dwell time, and number of backspaces from an arff file
	 */
	public void readArff() {
		Path path = FileSystems.getDefault().getPath(this.path);
		try {
			Scanner scan = new Scanner(path);
			
			// skips first two strings
			// print statements for debugging
			String s = scan.next();
			//System.out.println(s);
			s = scan.next();
			//System.out.println(s);
			
			// print statement for debugging
			//System.out.println("LOOP START");
			int keystrokeCount = 0; 
			// loops over attribute tags at beginning of file to count them
			while (true) {
				s = scan.next();
				if (s.equals("{accepted,denied}")) { break; }
				s = s.substring(0, s.length() - 1);
				// print statement for debugging
				//System.out.println(s);
				
				if (s.equals("dwellTime") ||
				    s.equals("flightTime") ||
				    s.equals("backspaceCoun")) {
					keystrokeCount++;
				}
			}
			// print statement for debugging
			//System.out.println("LOOP END " + keystrokeCount);
			
			// Stores all password keystroke info (dwell times, flight times, backspace count)
			ArrayList<int[]> allKeystrokes = new ArrayList<int[]>();
			
			// skips next four strings
			// print statements for debugging
			s = scan.next();
			//System.out.println(s);
			s = scan.next();
			//System.out.println(s);
			s = scan.next();
			//System.out.println(s);
			s = scan.next();
			//System.out.println(s);
			
			// change delimiter to a comma since numbers are separated by commas
			scan.useDelimiter(",");
			int time = 0;
			
			// scan file for keystroke info and add to arrays
			while (scan.hasNext()) {
				int[] keystrokes = new int[keystrokeCount];
				
				for (int i = 0; i < keystrokeCount; i++) {
					s = scan.next();
					s = s.trim();
					time = Integer.valueOf(s);
					keystrokes[i] = time;
				}
				
				// Printing for debugging
				/*for (int i = 0; i < keystrokeCount; i++) {
					System.out.print(keystrokes[i] + ", ");
				}
				System.out.println();
				*/
				
				allKeystrokes.add(keystrokes);
				
				// skip next string, will have a \n so must reset delimiter
				scan.reset();
				s = scan.next();
				// print statement for debugging
				//System.out.println(s);
				scan.useDelimiter(",");
			}
			
			// !!!!!
			// call specified method for using read in data here!
			int numDevs = 2;
			createRandomDenied(allKeystrokes, numDevs);
			
			scan.close();
			
		} catch (IOException e) {
			System.out.println("Specified file name " + this.path + " doesn't match any files.");
		}	
	}
	
	/** 
	 * Finds the mean of a set of data read in as an array list of integer arrays.
	 * The integer arrays are all expected to be the same length and contain
	 * times.  The average will be taken of all times in the same index in each array.
	 * Uses doubles for calculations to keep rounding errors down even though ints should be used later.
	 * 
	 * @param allTimes
	 * 	The data set to take the average of times
	 * @return
	 * 	The average of the time in the index of the arrays sent in
	 */
	private ArrayList<Double> findMeanSets(ArrayList<int[]> allTimes) {
		int size = allTimes.size();
		int numTimes = allTimes.get(0).length;
		ArrayList<Double> avgTimes = new ArrayList<Double>();
		// initialize avgTimes with all 0s
		for (int i = 0; i < numTimes; i++) {
			avgTimes.add(0.0);
		}
		
		// loop over outer array list to add times for every array
		for (int i = 0; i < size; i++) {
			int[] times = allTimes.get(i);
			// loop over inner array to add times for each index
			for (int j = 0; j < numTimes; j++) {
				avgTimes.set(j, avgTimes.get(j) + times[j]);
			}
		}
		
		for (int i = 0; i < numTimes; i++) {
			avgTimes.set(i, avgTimes.get(i) / size);
		}
		
		// Printing for debugging
		/*
		System.out.print("Average times: ");
		for (int i = 0; i < numTimes; i++ ) {
			System.out.print(avgTimes.get(i) + " ");
		}
		System.out.println();
		*/

		return avgTimes;
	}
	
	/**
	 * Find the standard deviation of the times sent in of an array list of arrays.
	 * Uses the averages sent in to calculate the standard deviation.
	 * Uses doubles for calculations to keep rounding errors down even though ints should be used later.
	 * 
	 * @param allTimes
	 * 	The data set to take the standard deviation of times
	 * @param means
	 * 	The average of allTimes by index in array
	 * @return
	 * 	The standard deviation of each index in allTimes
	 */
	private ArrayList<Double> findStdDeviationSets(ArrayList<int[]> allTimes, ArrayList<Double> means) {
		int size = allTimes.size();
		int numTimes = allTimes.get(0).length;
		ArrayList<Double> stdDevs = new ArrayList<Double>();
		// initialize stdDevDs with all 0s
		for (int i = 0; i < numTimes; i++) {
			stdDevs.add(0.0);
		}
		
		// loop over outer array list to add times for every array
		for (int i = 0; i < size; i++) {
			int[] times = allTimes.get(i);
			// loop over inner array to add times for each index
			for (int j = 0; j < numTimes; j++) {
				stdDevs.set(j, stdDevs.get(j) + (((double) times[j]) - means.get(j)) * (((double) times[j]) - means.get(j)));
				// Print statement for debugging
				//System.out.print(stdDevsD.get(j) + " ");
			}
			//System.out.println();
		}
		
		// if adding last element for this index, divide it by the total entries
		for (int i = 0; i < numTimes; i++) {
			stdDevs.set(i, Math.sqrt(stdDevs.get(i) / size));
		}
		
		// Printing for debugging
		/*
		System.out.print("Standard deviations: ");
		for (int i = 0; i < numTimes; i++ ) {
			System.out.print(stdDevs.get(i) + " ");
		}
		System.out.println();
		*/

		return stdDevs;
	}
	
	/**
	 * Creates random denied data uniformly distributed within a given number
	 * of standard deviations of a data set, then prints it to the file named above.
	 * 
	 * @param allKeystrokes
	 * 	The dataset from which to create random denied data.
	 * @param numDevs
	 * 	The number of standard deviations to create random denied data within.
	 */
	private void createRandomDenied(ArrayList<int[]> allKeystrokes, int numDevs) {
		// Test data for mean and standard deviation
		// Mean: 6.5, Standard Deviation: 3.304...
		/*
		ArrayList<int[]> testSet = new ArrayList<int[]>();
		int[] num1 = {9, 9};
		testSet.add(num1);
		int[] num2 = {2, 2};
		testSet.add(num2);
		int[] num3 = {5, 5};
		testSet.add(num3);
		int[] num4 = {4, 4};
		testSet.add(num4);
		int[] num5 = {12, 12};
		testSet.add(num5);
		int[] num6 = {7, 7};
		testSet.add(num6);
		findStdDeviationSets(testSet, findMeanSets(testSet));
		 */
		
		ArrayList<Double> keystrokeAvgsD = findMeanSets(allKeystrokes);
		ArrayList<Double> keystrokeStdDevsD = findStdDeviationSets(allKeystrokes, keystrokeAvgsD);
		
		/* Since times are milliseconds represented as integers,
		 *convert averages and standard deviations to integers
		 */
		ArrayList<Integer> keystrokeAvgs = new ArrayList<Integer>();
		ArrayList<Integer> keystrokeStdDevs = new ArrayList<Integer>();
		int size = keystrokeAvgsD.size();
		for (int i = 0; i < size; i++) {
			keystrokeAvgs.add((int) Math.round(keystrokeAvgsD.get(i)));
			keystrokeStdDevs.add((int) Math.round(keystrokeStdDevsD.get(i)));
		}
		
		// Creates randomly denied data and prints it
		Random rand = new Random();
		// The number of denied entries to make and print
		int deniedEntries = 50;
		for (int i = 0; i < deniedEntries; i++) {
			ArrayList<Integer> deniedNums = new ArrayList<Integer>();
			for (int j = 0; j < size; j++) {
				int stdDev = keystrokeStdDevs.get(j);
				int mean = keystrokeAvgs.get(j);
				int deniedRange = rand.nextInt(stdDev * (numDevs * 2) + 1);
				deniedRange -= (stdDev * numDevs);
				int deniedNum = mean + deniedRange;
				deniedNums.add(deniedNum);
			}
			printDenied(deniedNums);
		}
		
	}
	
	/**
	 * Prints denied data to a text file.
	 * Assumes the text file already has accepted data in it.
	 * 
	 * @param deniedData
	 *	The denied data to be printed.
	 *	Assumed to be in same format as accepted data already written to the file.
	 */
	private void printDenied(ArrayList<Integer> deniedData) {
		// Set up to write to txt file
		FileWriter write = null;
		try {
			write = new FileWriter(path, true);
		} catch (IOException e) {
			System.out.println("Error storing Data");
		}
		PrintWriter printer = new PrintWriter(write);
		String txt = "";
		
		int size = deniedData.size();
		printer.println();
		for(int i = 0; i < size; i++){
			txt += deniedData.get(i);
			txt += ",";
		}
		txt += "denied";
		printer.print(txt);
		
		printer.close();
	}
}
