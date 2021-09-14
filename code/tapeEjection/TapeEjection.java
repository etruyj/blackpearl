//===================================================================
// TapeEjection.java
// 	This script handles the ejection of tapes when provided in a
// 	csv format.
//
// 	Includes main()
//===================================================================

package com.socialvagrancy.blackpearl.tapeEjection;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import com.socialvagrancy.blackpearl.ds3.ArgParser;
import com.socialvagrancy.blackpearl.ds3.DS3Interface;
import com.socialvagrancy.blackpearl.jsonObject.EjectTapeCall;
import com.socialvagrancy.blackpearl.jsonObject.ObjectsOnTapeCall;
import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

public class TapeEjection
{
	private Gson gson;
	private DS3Interface java_cli;
	private Logger logbook;
	private FileManager files;

	public TapeEjection(String pathToJavaCLI, boolean isSecure, String endpoint, String access_key, String secret_key, boolean isWindows)
	{
		logbook = new Logger("../logs/tape-eject.log", 10240, 0, 1);
		java_cli = new DS3Interface(pathToJavaCLI, isSecure, endpoint, access_key, secret_key, isWindows);
		gson = new Gson();
	}

	public String checkTapeUsage(String barcode, boolean isWindows, boolean printToShell, boolean debug)
	{
		String command = " -c get_objects_on_tape -i " + barcode;
		command = java_cli.getCommand() + command;
		String resultsJson;
		ObjectsOnTapeCall results;
		

		if(printToShell || debug)
		{
			System.out.print("Checking tape status for " + barcode + "\t");
			
			if(debug)
			{
				System.out.println(command);
			}
		}

		resultsJson = java_cli.executeProcess(command, isWindows);
		
		try
		{
			results = gson.fromJson(resultsJson, ObjectsOnTapeCall.class);

			// No objects on tape.
			if(results.getObjectName(0).equals("Invalid index selected"))
			{
				if(printToShell || debug)
				{
					System.out.println("pending eject");
				}

				logbook.logWithSizedLogRotation("Tape " + barcode + " is available to eject.", 2);
				return "pending";
			}
			else // tape is in use
			{
				if(printToShell || debug)
				{
					System.out.println("in-use");
				}
				logbook.logWithSizedLogRotation("Tape " + barcode + " holds data.", 2);
				return "tape is in use.";
			}
		}
		catch(JsonParseException e)
		{
			if(printToShell || debug)
			{
				System.out.println("not found");
				System.out.println(e.getMessage());
			}
			logbook.logWithSizedLogRotation("Could not find barcode " + barcode, 3);
			logbook.logWithSizedLogRotation(e.getMessage(), 3);

			return "not found";
		}

	}

	public void ejectTapes(String tapeListPath, int max_moves, boolean ignoreUsedTapes, boolean isWindows, boolean printToShell, boolean debug)
	{
		ArrayList<String[]> tapes;
		int moves = 0;
		int i=0;

		if(printToShell || debug) // debug or print to shell should print this process.
		{
			System.out.println("Starting tape ejection process...");
		}
		
		// Get list of tapes.
		tapes = importTapeList(tapeListPath, printToShell, debug);

		// Check tape status;

		if(printToShell || debug)
		{
			System.out.println("Checking tape status...");
		}

		while(i<tapes.size() && moves < max_moves)
		{
			if(!ignoreUsedTapes)
			{
				tapes.get(i)[1] = checkTapeUsage(tapes.get(i)[0], isWindows, printToShell, debug);
			}
			else
			{
				logbook.logWithSizedLogRotation("Ignoring tape status for " + tapes.get(i)[0] + " and marking for export.", 2);
				tapes.get(i)[1] = "pending";
			}

			if(tapes.get(i)[1].equals("pending"))
			{
				moves++;
			}

			i++;
		}
		
		if(i >= tapes.size())
		{
			if(printToShell || debug)
			{
				System.out.println("All tapes (" + tapes.size() + ") have been verified.");
			}

			logbook.logWithSizedLogRotation("All tapes (" + tapes.size() + ") have been verfied.", 2);

		}

		if(moves >= max_moves)
		{
			if(printToShell || debug)
			{
				System.out.println("Designated maximum number of moves (" + max_moves + ") have been prepared.");
			}

			logbook.logWithSizedLogRotation("Designated maximum number of moves (" + max_moves + ") have been prepared.", 2);

		}

		// Send Eject Commands
		moves =0;
		i = 0;
		
		if(printToShell || debug)
		{
			System.out.println("Sending eject commands...");
		}

		while(i<tapes.size() && moves < max_moves)
		{
			if(tapes.get(i)[1].equals("pending"))
			{
				if(sendEjectCommand(tapes.get(i)[0], isWindows, printToShell, debug))
				{
					tapes.get(i)[1] = "ejected";
					moves++;

					logbook.logWithSizedLogRotation("Ejected tape " + tapes.get(i)[0], 2);
				}
				else
				{
					tapes.get(i)[1] = "failed";
				}
			}
			i++;
		}

		// Update the csv to remove these tapes.
		
		updateInputFile(tapeListPath, tapes, printToShell, debug);

	}

	public ArrayList<String[]> importTapeList(String tapeListPath, boolean printToShell, boolean debug)
	{
		ArrayList<String[]> tapeList = new ArrayList<String[]>();
		String[] tape;
		int tapeCount = 0;		

		if(printToShell || debug)
		{
			System.out.println("Building list of tapes...");
		}

		try
		{
			File inFile = new File(tapeListPath);

			BufferedReader stdInput = new BufferedReader(new FileReader(inFile));

			String input = null;

			while((input = stdInput.readLine()) != null)
			{
				if(!input.equals("bar_code"))
				{
					tape = new String[2];

					tape[0] = input;
					tape[1] = "waiting";

					tapeList.add(tape);

					tapeCount++;
				
					if(debug)
					{
						System.out.println("Importing " + input 
							+ " with status " + tape[1] + ".");
					}
				}
			}

			stdInput.close();
		}
		catch(Exception e)
		{
			if(printToShell || debug)
			{
				System.out.println(e.getMessage());
			}

			logbook.logWithSizedLogRotation(e.getMessage(), 3);
		}

		if(tapeCount != tapeList.size())
		{
			if(printToShell || debug)
			{
				System.out.println("Warning: tape list does not match number of copied tapes.");
			}
				logbook.logWithSizedLogRotation("Warning: tape list does not match number of copied tapes.", 3);
				logbook.logWithSizedLogRotation("Tapes counted: " + tapeCount + " List Size: " + tapeList.size(), 3);
		}
		else
		{
			if(printToShell || debug)
			{
				System.out.println(tapeCount + " tapes imported.");
			}
			
			logbook.logWithSizedLogRotation(tapeCount + " tapes imported.", 2);
		}

		return tapeList;
	}

	public boolean sendEjectCommand(String barcode, boolean isWindows, boolean printToShell, boolean debug)
	{
		String command = " -c eject_tape -i " + barcode;
		command = java_cli.getCommand() + command;
		String resultsJson;
		EjectTapeCall results;


		if(printToShell || debug)
		{
			System.out.print("Ejecting tape " + barcode + "\t");

		}

		resultsJson = java_cli.executeProcess(command, isWindows);
		
		try
		{
			results = gson.fromJson(resultsJson, EjectTapeCall.class);
			
			if(printToShell || debug)
			{
				System.out.println("ejected");
			}
		}
		catch(JsonParseException e)
		{
			if(printToShell || debug)
			{
				System.out.println("failed");
			}
			
			logbook.logWithSizedLogRotation(resultsJson, 3);
			logbook.logWithSizedLogRotation("Error: tape " + barcode + " was not found on BlackPearl.", 3);
			return false;
		}

		return true;
	}

	public void updateInputFile(String inputFilePath, ArrayList<String[]> tapeList, boolean printToShell, boolean debug)
	{
		String completedPath = "../output/completed-ejects.csv";

		FileManager outFiles = new FileManager();

		outFiles.createFileDeleteOld(inputFilePath, debug);
		outFiles.appendToFile(inputFilePath, "bar_code");

		for(int i=0; i<tapeList.size(); i++)
		{
			if(tapeList.get(i)[1].equals("waiting"))
			{
				outFiles.appendToFile(inputFilePath, tapeList.get(i)[0]);
			}
			else
			{
				outFiles.appendToFile(completedPath, tapeList.get(i)[0] + "," + tapeList.get(i)[1]);
			}
		}
	}

	public void printAll(ArrayList<String[]> tapeList)
	{
		for(int i=0; i<tapeList.size(); i++)
		{
			System.out.println(tapeList.get(i)[0] + " " + tapeList.get(i)[1]);
		}
	}

	public void printHelp()
	{
		try
		{
			File inFile = new File("../lib/help/tape_ejects.txt");

			BufferedReader stdInput = new BufferedReader(new FileReader(inFile));

			String input = null;

			while((input = stdInput.readLine()) != null)
			{
				System.out.println(input);
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args)
	{
		// Initialization
		ArgParser aparser = new ArgParser();
		
		aparser.parseInputs(args);

		TapeEjection program = new TapeEjection(aparser.getDS3Path(), aparser.getConnectionState(), aparser.getEndpoint(), aparser.getAccessKey(), aparser.getSecretKey(), aparser.getIsWindows());
		
		if(aparser.printHelp())
		{
			program.printHelp();
		}
		else
		{

			program.ejectTapes(aparser.getInputFile(), aparser.getMaxMoves(), aparser.getIgnoreUsedTapes(), aparser.getIsWindows(), aparser.getPrintToShell(), aparser.getDebugging());
		}
	}
}
