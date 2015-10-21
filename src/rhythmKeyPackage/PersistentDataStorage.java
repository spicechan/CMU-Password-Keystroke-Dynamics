package rhythmKeyPackage;

import java.util.List;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class PersistentDataStorage {

	public PersistentDataStorage(String userName){
		this.path = userName + ".arff";
		this.userName = userName;
	}

	String path;
	String userName;

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
		int i = 0;
		try {
			while ((newLine = reader.readLine()) != null){
				i++;
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

	public void storeData(Session s){
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
				txt = "@attribute keyPressed";
				txt += Integer.toString(i + id);
				txt += " string";
				printer.println(txt);
				txt = "@attribute flightTime";
				txt += Integer.toString(i + id);
				txt += " string";
				printer.println(txt);
				txt = "@attribute dwellTime";
				txt += Integer.toString(i + id);
				txt += " string";
				printer.println(txt);
			}

			printer.println("@attribute class {accepted,denied}");
			printer.println("");
			printer.println("@data");
		}

		//now write data
		List<KeyPress> keyStrokes = s.getKeyStrokes();
		for(int i = 0; i < keyStrokes.size(); i++){
			txt = "";
			KeyPress k1 = keyStrokes.get(i);
			KeyEvent ke1 = k1.getKeyIdentifier();
			txt += ke1.getKeyChar();
			txt += ",";
			if (i + 1 < keyStrokes.size()){
				KeyPress k2 = keyStrokes.get(i+1);
				txt += Double.toString(k2.getKeydown() - k1.getKeyup());
			}
			else txt += "0.0";
			txt += ",";
			txt += Double.toString(k1.getKeyup() - k1.getKeydown());	
			txt += ",";
			printer.print(txt);
		}
		txt = "accepted";
		printer.println(txt);
		//printer.printf("%d", id);

		//createDeniedData(printer, s);

		printer.close();
	}

	private void createDeniedData(PrintWriter printer, Session s){
		int amountOfKeyPresses = s.getKeyStrokes().size()/2;
		int amountOfSessions = 50; // Just a random maximum of sessions per user
		int FlightOrDwell = 2;


		String[] keyPressed = new String[amountOfKeyPresses];
		double[][][] flightAndDwellData = new double[amountOfKeyPresses][amountOfSessions][FlightOrDwell];

		int numberOfSessions = fillArraysWithDataInFile(keyPressed, flightAndDwellData);

		final int FLIGHT = 0;
		final int DWELL = 1;


		//mean
		double[][] meanFlightAndDwellData = new double[amountOfKeyPresses][FlightOrDwell];

		double sumFlight = 0.0;
		double sumDwell = 0.0;

		for (int keypressed = 0; keypressed < flightAndDwellData.length; keypressed++) {
			for (int sessionNumber = 0; sessionNumber < numberOfSessions; sessionNumber++) {
				sumFlight += flightAndDwellData[keypressed][sessionNumber][FLIGHT];
				sumDwell += flightAndDwellData[keypressed][sessionNumber][DWELL];
			}
			meanFlightAndDwellData[keypressed][FLIGHT]= sumFlight/numberOfSessions;
			meanFlightAndDwellData[keypressed][DWELL]= sumDwell/numberOfSessions;
		}


		//std var
		double[][] standVariationFlightAndDwellData = new double[amountOfKeyPresses][FlightOrDwell];



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
			standVariationFlightAndDwellData[keypressed][FLIGHT]= Math.sqrt(tempFlight/numberOfSessions);
			standVariationFlightAndDwellData[keypressed][DWELL]= Math.sqrt(tempDwell/numberOfSessions);
		}
	}


	private int fillArraysWithDataInFile(String[] keyPressed, double[][][] flightAndDwellData){
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
					for (int i = 0; i < elementsSeparatedByCommas.length; i+=3) {
						keyPressed[i/3] = elementsSeparatedByCommas[i];
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

	public int getCurrentSessionId(){
		return 0; //Chris: Change this for the method that looks at the number on the last line of the file, yoou can assume userName is filled out with the user name.
	}
}
