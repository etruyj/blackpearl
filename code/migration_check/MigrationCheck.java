//===================================================================
// MigrationCheck.java
// 	This code automates the verification of migrated data by 
// 	ejecting tapes from the BlackPearl and then initiating restores
// 	of assets on those tapes via Spectra Logic's ds3_java_cli.
// 	Assets should be restored successfully as they would exist
// 	on tapes in a second (accessible) storage domain.
//===================================================================

package com.socialvagrancy.blackpearl.migrationcheck;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import com.socialvagrancy.blackpearl.ds3.ArgParser;
import com.socialvagrancy.blackpearl.ds3.DS3Interface;
import com.socialvagrancy.blackpearl.jsonObject.*;
import com.socialvagrancy.blackpearl.tapeEjection.TapeEjection;

import com.socialvagrancy.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Random;

public class MigrationCheck
{
	private DS3Interface java_cli;
	private Logger logbook;
	private Gson gson;

	public MigrationCheck(String pathToJavaCLI, boolean isSecure, String endpoint, String access_key, String secret_key, boolean isWindows)
	{
		java_cli = new DS3Interface(pathToJavaCLI, isSecure, endpoint, access_key, secret_key, isWindows);
		logbook = new Logger("../logs/migration-check.log", 10240, 1, 1);
		gson = new Gson();
	}

	public boolean verifyMigration(String tapeList, int EESize, int restoreCount, long restoreSize, String restorePath, boolean clearCache, boolean isWindows, boolean printToShell, boolean debug)
	{
		//=============================================
		// verifyMigration.
		// 	The verify migration process follows 
		// 	these steps.
		// 	1.) Import list of tapes in tapeList file.
		// 	2.) Select a subset for verification.
		// 		Subset <= EESlots.
		// 	3.) Identify a number of assets per tape.
		// 	4.) Eject the tapes.
		// 	5.) Clear cache.
		// 	6.) Restores assets from list.
		// 	7.) Verify assets are restored.
		// 	8.) Save a report of the assets.
		// 	9.) Print success or fail for each tape.
		//=============================================
		
		ArrayList<String> tapes = selectTapesToTest(tapeList, EESize, printToShell, debug);
	
		if(clearCache)
		{
			// Clear the cache.
			System.out.println("Cache should be cleared at this point.");
		}

		for(int i=0; i<tapes.size(); i++)
		{
			if(checkObjectsOnTape(tapes.get(i), restoreCount, restoreSize, restorePath, isWindows, printToShell, debug))
			{
				logbook.logWithSizedLogRotation("Verified tape (" + tapes.get(i) + ") and is ready for ejection.", 2);
			}
			else
			{
				logbook.logWithSizedLogRotation("Verification of tape (" + tapes.get(i) + ") failed.", 2);
			}
		}

		return true;
	}

	//==============================================
	// Internal Only functions
	//==============================================

	private boolean checkObjectsOnTape(String barcode, int restoreCount, long fileSize, String restorePath, boolean isWindows, boolean printToShell, boolean debug)
	{
		logbook.logWithSizedLogRotation("Checking objects on " + barcode, 2);

		if(printToShell || debug)
		{
			System.out.println("Checking " + restoreCount + " objects on " + barcode);
		}

		int itr = 0;
		int objects_tested_per_tape = 0;

		String command = formatObjectsOnTapeCall(barcode);
		ObjectsOnTapeCall objects = getObjectsOnTape(command, isWindows, printToShell, debug);

		System.out.println("Objects count: " + objects.getObjectCount());

		// Initialize the tracking variable as all false.
		// When objects are picked or too large for the fileSize,
		// this is marked as true.
		// This variable keeps us from choosing the same file.
		boolean[] objectsSelected = new boolean[objects.getObjectCount()];
		for(int i=0; i<objects.getObjectCount(); i++)
		{
			objectsSelected[i] = false;
		}
		
		while(itr < objects.getObjectCount() && itr < restoreCount)
		{
		
			// Attempt to restore a file
			if(restoreObject(objects, restorePath, fileSize, objectsSelected, isWindows, printToShell, debug))
			{
				objects_tested_per_tape++;
				System.out.println("Restore attempt successful.");
			}

			itr++;
		}
		
		
		return true;
	}

	private String formatEjectTapeCall(String barcode)
	{
		return java_cli.getCommand() + " -c eject_tape -i " + barcode;
	}

	private String formatDownloadObject(String objectName, String savePath)
	{
		return java_cli.getCommand() + " -c get_object -o " + objectName + " -p " + savePath;
	}

	private String formatObjectsOnTapeCall(String barcode)
	{
		return java_cli.getCommand() + " -c get_objects_on_tape -i " + barcode;
	}

	private ObjectsOnTapeCall getObjectsOnTape(String command, boolean isWindows, boolean printToShell, boolean debug)
	{
		if(debug)
		{
			System.out.println(command);
		}
		
		String result = java_cli.executeProcess(command, isWindows);
		
		try
		{
			ObjectsOnTapeCall objects = gson.fromJson(result, ObjectsOnTapeCall.class);

			return objects;
		}
		catch(JsonParseException e)
		{
			logbook.logWithSizedLogRotation("Error: " + e.getMessage(), 3);

			if(printToShell || debug)
			{
				System.out.println(e.getMessage());
			}
		
			return new ObjectsOnTapeCall();
		}
	}

	private ArrayList<String> importTapeList(String filePath, boolean printToShell, boolean debug)
	{
		ArrayList<String> tapeList = new ArrayList<String>();

		if(printToShell || debug)
		{
			System.out.println("Importing tapes from " + filePath + "...");
		}

		try
		{
			File inFile = new File(filePath);

			BufferedReader stdInput = new BufferedReader(new FileReader(inFile));

			String input = null;

			while((input = stdInput.readLine()) != null)
			{
				if(!(input.equals("bar_code") || input.equalsIgnoreCase("barcode")))
				{
					tapeList.add(input);

					if(debug)
					{
						System.out.println("Imported " + tapeList.get(tapeList.size()-1));	
					}
				}
			}

			logbook.logWithSizedLogRotation("Imported " + tapeList.size() + " tapes.", 2);

			if(printToShell || debug)
			{
				System.out.println("Imported " + tapeList.size() + " tapes.");
			}

		}
		catch(IOException e)
		{
			logbook.log("Error: " + e.getMessage(), 3);
			
			if(printToShell || debug)
			{
				System.out.println(e.getMessage());
			}
		}

		return tapeList;
	}

	private boolean restoreObject(ObjectsOnTapeCall objects, String restorePath, long fileSize, boolean[] objectsSelected, boolean isWindows, boolean printToShell, boolean debug)
	{
		// restoreObject
		// 	restores individual objects on tape.
		// 	tapesSelected is used to ensure we don't restore the 
		// 	same object twice and to make sure we do pick an object.

		
		String toRestore = selectObjectForRestore(objects, fileSize, objectsSelected, printToShell, debug);
	
		System.out.println(toRestore);

		return true; 
	}

	private String selectObjectForRestore(ObjectsOnTapeCall objects, long fileSize, boolean[] objectsSelected, boolean printToShell, boolean debug)
	{
		Random rand = new Random();
		int selection = rand.nextInt(objectsSelected.length);
		ArrayList<Integer> remainingOptions = new ArrayList<Integer>();

		// if tape has already been picked.
		// build a list of remaining options to pick from.
		if(objectsSelected[selection] == true)
		{
			logbook.logWithSizedLogRotation("Object at index (" 
					+ selection + ") was already chosen.", 2);
			for(int i = 0; i<objectsSelected.length; i++)
			{
				if(objectsSelected[i] == false)
				{
					remainingOptions.add(i);
				}
			}
		
			if(remainingOptions.size()>0)
			{
				selection = rand.nextInt(remainingOptions.size());
				selection = remainingOptions.get(selection);

			}
			else
			{
				// No options remain.
				logbook.logWithSizedLogRotation("No assets remain on this tape.", 3);
				return "none selected.";
			}
		}
		// Tape hasn't been selected. Mark it as selected.
		else 
		{
			objectsSelected[selection] = true;
		}

		logbook.logWithSizedLogRotation("Attempting restore of object at index (" + selection + ").", 2);

		if(fileSize<=0)
		{
			
			return objects.getObjectName(selection);
		}
		else
		{
			
			if(fileSize < objects.getObjectLength(selection))
			{
				logbook.logWithSizedLogRotation("Object meets size requirements.", 2);
				return objects.getObjectName(selection); 
			}
			else
			{
				logbook.logWithSizedLogRotation("Object is larger than specified max size.", 2);
				
				return selectObjectForRestore(objects, fileSize, objectsSelected, printToShell, debug);
				
			}
		}
	}

	private ArrayList<String> selectTapesToTest(String filePath, int EESlots, boolean printToShell, boolean debug)
	{
		ArrayList<String> tapes = importTapeList(filePath, printToShell, debug);
		ArrayList<String> selected_tapes = new ArrayList<String>();
		Random rand = new Random();
		int picked_tapes = 0;
		int pick;

		if(EESlots < tapes.size())
		{
			logbook.logWithSizedLogRotation("Selecting subset of " 
					+ EESlots + " tapes from the list of "
					+ tapes.size(), 2);
		}
		else
		{
			logbook.logWithSizedLogRotation("Testing all " + tapes.size() + " tapes.", 2);
		}
		
		if(printToShell || debug)
		{
			if(EESlots<tapes.size())
			{
				System.out.println("Selecting the specified number of tapes (" + EESlots + ") for testing.");
			}
			else
			{
				System.out.println("Testing all tapes specified.");
			}
		}

		while(picked_tapes < EESlots && tapes.size()>0)
		{
			pick = rand.nextInt(tapes.size());

			selected_tapes.add(tapes.get(pick));
			tapes.remove(pick);
			
			picked_tapes++;	
		}

		return selected_tapes;
	}

	public static void main(String[] args)
	{
		ArgParser aparser = new ArgParser("./migration_check/migration.conf");
		aparser.parseInputs(args);

		MigrationCheck checker = new MigrationCheck(aparser.getDS3Path(), aparser.getConnectionState(), aparser.getEndpoint(), aparser.getAccessKey(), aparser.getSecretKey(), aparser.getIsWindows());
		
		if(aparser.printHelp())
		{
			System.out.println("Help flag");
		}
		else
		{
			checker.verifyMigration(aparser.getInputFile(), aparser.getMaxMoves(), aparser.getRestoreCount(), aparser.getMaxFileSize(), aparser.getSavePath(), aparser.getClearCache(), aparser.getIsWindows(), aparser.getPrintToShell(), aparser.getDebugging());
		}
	}
}
