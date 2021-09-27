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

import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Random;

public class MigrationCheck
{
	private DS3Interface java_cli;
	private FileManager report;
	private Logger logbook;
	private Gson gson;
	private String tape_report_path;

	public MigrationCheck(String pathToJavaCLI, boolean isSecure, String endpoint, String access_key, String secret_key, boolean isWindows)
	{
		java_cli = new DS3Interface(pathToJavaCLI, isSecure, endpoint, access_key, secret_key, isWindows);
		logbook = new Logger("../logs/migration-check.log", 10240, 0, 1);
		gson = new Gson();

		// Configure the output report.
		report = new FileManager();

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
		LocalDateTime now = LocalDateTime.now();

		tape_report_path = "../output/migrate_tapes-" + dtf.format(now) + ".csv";
		report.createFileDeleteOld(tape_report_path, true);
		report.appendToFile(tape_report_path, "barcode,verification");
	}

	public void verifyMigration(String tapeList, int EESize, int restoreCount, long restoreSize, String restorePath, boolean clearCache, boolean isWindows, boolean printToShell, boolean debug)
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
			clearCache(isWindows, printToShell, debug);
		}


		// Check objects on individual tape.
		for(int i=0; i<tapes.size(); i++)
		{	
			
			if(ejectTape(tapes.get(i), isWindows, printToShell, debug))
			{
				if(verifyTapeEjected(tapes.get(i), isWindows, printToShell, debug))
				{
					if(checkObjectsOnTape(tapes.get(i), restoreCount, restoreSize, restorePath, isWindows, printToShell, debug))
					{
						if(printToShell || debug)
						{
							System.out.println("Verified tape (" + tapes.get(i) + ") is ready for removal.");
						}

						logbook.logWithSizedLogRotation("Verified tape (" + tapes.get(i) + ") and is READY for ejection.", 2);
				
						reportFinding(tapes.get(i), "SUCCEEDED", tape_report_path);
					}
					else
					{
						if(printToShell || debug)
						{
							System.out.println("Unable to verify tape (" + tapes.get(i) + "). Do not remove.");
						}

						logbook.logWithSizedLogRotation("Verification of tape (" + tapes.get(i) + ") FAILED.", 2);
			
						reportFinding(tapes.get(i), "FAILED", tape_report_path);
					}
				}
				else
				{
					logbook.logWithSizedLogRotation("Adding tape (" + tapes.get(i) + ") to end of list and moving on.", 2);

					if(printToShell || debug)
					{
						System.out.println("Pausing for 5 seconds");
					}

					tapes.add(tapes.get(i));
					
					pause(5000, debug);
				}
			}
			else
			{
				if(printToShell || debug)
				{
					System.out.println("Tape (" + tapes.get(i) + ") does not exist.");
				}
				
				logbook.logWithSizedLogRotation("Tape (" + tapes.get(i) + ") does not exist. Skipping.", 3);

				reportFinding(tapes.get(i), "Does not exist.", tape_report_path);
			}
		}
		
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
		boolean successful_restores = true;

		String command = formatObjectsOnTapeCall(barcode);
		ObjectsOnTapeCall objects = getObjectsOnTape(command, isWindows, printToShell, debug);

		if(printToShell || debug)
		{
			if(objects.getObjectCount()<=0)
			{
				System.out.println("No objects to verify on tape (" + barcode + ")");
			}

			if(debug)
			{
				System.out.println("Objects count: " + objects.getObjectCount());
			}
		}

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
				System.out.println("Restore attempt:\t[SUCCESS]");
			}
			else
			{
				successful_restores = false;
				System.out.println("Restore attempt:\t[FAILED]");
			}

			itr++;
		}
		
		
		return successful_restores;
	}

	private void clearCache(boolean isWindows, boolean printToShell, boolean debug)
	{
		String command = formatClearCache();

		logbook.logWithSizedLogRotation("Clearing system cache...", 2);

		if(printToShell || debug)
		{
			System.out.println("Clearing system cache...");
			
			if(debug)
			{
				System.out.println(command);
			}
		}

		java_cli.executeProcess(command, isWindows);
	}


	private boolean ejectTape(String barcode, boolean isWindows, boolean printToShell, boolean debug)
	{
		// Ejecting the tape to test if the data is accessible someplace else.
		String command = formatEjectTapeCall(barcode);
			
		logbook.logWithSizedLogRotation("Ejecting tape (" + barcode + ") to test data accessibility.", 2);

		if(printToShell || debug)
		{
			System.out.println("\nEjecting tape: " + barcode);

			if(debug)
			{
				System.out.println(command);
			}
		}

		String result = java_cli.executeProcess(command, isWindows);

		try
		{
			TapeInfoCall api_check = gson.fromJson(result, TapeInfoCall.class);	
		}
		catch(JsonParseException e)
		{
			logbook.logWithSizedLogRotation("ERROR: Unable to verify tape (" + barcode + ")", 3);
			logbook.logWithSizedLogRotation(e.getMessage(), 3);

			return false;
		}


		return true;
	}

	private String formatClearCache()
	{
		return java_cli.getCommand() + " -c reclaim_cache";
	}

	private String formatEjectTapeCall(String barcode)
	{
		return java_cli.getCommand() + " -c eject_tape -i " + barcode;
	}

	private String formatDownloadObject(String bucket, String objectName, String savePath)
	{
		return java_cli.getCommand() + " -c get_object -b " + bucket 
			+ " -o '" + objectName + "' -d " + savePath;
	}

	private String formatObjectsOnTapeCall(String barcode)
	{
		return java_cli.getCommand() + " -c get_objects_on_tape -i " + barcode;
	}

	private String formatVerifyEject(String barcode)
	{
		return java_cli.getCommand() + " -c get_tape -i " + barcode;
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
			logbook.logWithSizedLogRotation("ERROR: " + e.getMessage(), 3);
			logbook.logWithSizedLogRotation("WARNING: No objects returned from tape call.", 3);

			if(printToShell || debug)
			{
				System.out.println("Error: invalid tape barcode.");
				System.out.println(e.getMessage());
			}
		
			return  new ObjectsOnTapeCall();
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

	private void pause(int duration, boolean debug)
	{
		if(debug)
		{
			System.out.println("Pausing execution for " + duration 
					+ "ms.");
		}

		try { Thread.sleep(duration); }
		catch(InterruptedException e) { System.out.println(e.getMessage()); }
	}
	
	private void reportFinding(String barcode, String result, String path)
	{
		report.appendToFile(path, barcode + "," + result);
	}

	private boolean restoreObject(ObjectsOnTapeCall objects, String restorePath, long fileSize, boolean[] objectsSelected, boolean isWindows, boolean printToShell, boolean debug)
	{
		// restoreObject
		// 	restores individual objects on tape.
		// 	tapesSelected is used to ensure we don't restore the 
		// 	same object twice and to make sure we do pick an object.

		
		String[] toRestore = selectObjectForRestore(objects, fileSize, objectsSelected, printToShell, debug);
	
		if(!toRestore[0].equals("none selected"))
		{
			System.out.println(toRestore[1]);

			String command = formatDownloadObject(toRestore[0], toRestore[1], restorePath);

			if(printToShell || debug)
			{
				System.out.println("Initiating restore request");
				if(debug)
				{
					System.out.println(command);
				}
			}
			
			java_cli.executeProcess(command, isWindows);

			// Check to see if the file exists.
			String path = restorePath + "/" + toRestore[1];
			File test = new File(path);

			logbook.logWithSizedLogRotation("Testing object path: " + path, 2);


			if(test.exists())
			{
				logbook.logWithSizedLogRotation("Object was verified in restore directory.", 2);
				return true;
			}
			else
			{
				logbook.logWithSizedLogRotation("WARNING: Unabled to verify object in restore directory.", 2);
				return false;
			}
		}

		logbook.logWithSizedLogRotation("WARNING: Unabled to find asset to verify. Max-object size may be too small", 2);
		return false; 
	}

	private String[] selectObjectForRestore(ObjectsOnTapeCall objects, long fileSize, boolean[] objectsSelected, boolean printToShell, boolean debug)
	{
		Random rand = new Random();
		int selection = rand.nextInt(objectsSelected.length);
		ArrayList<Integer> remainingOptions = new ArrayList<Integer>();
		
		// The object chosen. 
		// [0] bucket - necessary for restore request.
		// [1] object.
		String[] chosen_object = new String[2];

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
				chosen_object[0] = "none selected";

				// No options remain.
				logbook.logWithSizedLogRotation("No assets remain on this tape.", 3);
				return chosen_object;
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
			chosen_object[0] = objects.getObjectBucket(selection);
			chosen_object[1] = objects.getObjectName(selection);
			return chosen_object;
		}
		else
		{
			
			if(fileSize < objects.getObjectLength(selection))
			{
				logbook.logWithSizedLogRotation("Object meets size requirements.", 2);
				chosen_object[0] = objects.getObjectBucket(selection);
				chosen_object[1] = objects.getObjectName(selection);
				return chosen_object;
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

	private boolean verifyTapeEjected(String barcode, boolean isWindows, boolean printToShell, boolean debug)
	{
		// Verify tape is ejected before initiating restores.
		// If the tape is performing a job, it won't be ejected.
		// If the tape is in a drive when the restore request comes
		// through the restore request will be performed before the
		// tape was ejected. This prevents the data from being validated.
		String command = formatVerifyEject(barcode);

		logbook.logWithSizedLogRotation("Verify tape was ejected before initiating restore requests.", 2);

		if(printToShell || debug)
		{
			System.out.println("Verifying tape (" + barcode 
					+ ") was ejected before proceding.");
			
			if(debug)
			{
				System.out.println(command);
			}
		}

		try
		{
			String response = java_cli.executeProcess(command, isWindows);
			TapeInfoCall result = gson.fromJson(response, TapeInfoCall.class);

			if(debug)
			{
				System.out.println("Tape state:\t[" 
						+ result.getState() + "]");
			}
			if(result.getState().equals("EJECT_FROM_EE_PENDING"))
			{
				logbook.logWithSizedLogRotation("Tape (" + barcode 
						+ ") has been ejected.", 2);

				if(printToShell || debug)
				{
					System.out.println("Tape has been ejected.");
				}

				return true;
			}
			else
			{
				logbook.logWithSizedLogRotation("Tape (" + barcode 
						+ ") eject is PENDING.", 2);

				if(printToShell || debug)
				{
					System.out.println("Tape has not been ejected.");
				}
				
				return false;
			}
		}
		catch(JsonParseException e)
		{
			logbook.logWithSizedLogRotation(e.getMessage(), 3);

			if(printToShell || debug)
			{
				System.out.println(e.getMessage());
			}

			return false;
		}

	}

	//==============================================
	// END PRIVATE FUNCTIONS
	//==============================================

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
