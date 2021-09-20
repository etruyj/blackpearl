//===================================================================
// MigrationCheck.java
// 	This code automates the verification of migrated data by 
// 	ejecting tapes from the BlackPearl and then initiating restores
// 	of assets on those tapes via Spectra Logic's ds3_java_cli.
// 	Assets should be restored successfully as they would exist
// 	on tapes in a second (accessible) storage domain.
//===================================================================

package com.socialvagrancy.blackpearl.migrationcheck;

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

	public MigrationCheck(String pathToJavaCLI, boolean isSecure, String endpoint, String access_key, String secret_key, boolean isWindows)
	{
		java_cli = new DS3Interface(pathToJavaCLI, isSecure, endpoint, access_key, secret_key, isWindows);
		logbook = new Logger("../logs/migration-check.log", 10240, 1, 1);
	}

	public boolean verifyMigration(String tapeList, int EESize, int restoreCount, boolean isWindows, boolean printToShell, boolean debug)
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
		
		System.out.println(java_cli.getCommand());
	}

	//==============================================
	// Internal Only functions
	//==============================================

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

			logbook.logWithSizedLogRotation("Imported " + tapeList.size() + "tapes.", 2);

			if(printToShell || debug)
			{
				System.out.println("Imported " + tapeList.size() + "tapes.");
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

	private ArrayList<String> selectTapesToTest(String filePath, int EESlots, boolean printToShell, boolean debug)
	{
		ArrayList<String> tapes = importTapeList(filePath, printToShell, debug);
		ArrayList<String> selected_tapes = new ArrayList<String>();
		Random rand = new Random();

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
			checker.verifyMigration();
		}
	}
}
