//===================================================================
// TapeEjection.java
// 	This script handles the ejection of tapes when provided in a
// 	csv format.
//
// 	Includes main()
//===================================================================

package com.socialvagrancy.blackpearl.commands.sub;

import com.socialvagrancy.blackpearl.commands.BasicCommands;
import com.socialvagrancy.blackpearl.structures.ObjectsOnTapeCall;
import com.socialvagrancy.blackpearl.utils.DS3Interface;
import com.socialvagrancy.blackpearl.utils.FileInterface;
import com.socialvagrancy.utils.FileManager;
import com.socialvagrancy.utils.Logger;

import java.util.ArrayList;

public class TapeEjection
{
	public static String checkTapeUsage(BasicCommands java_cli, String barcode, Logger logbook, boolean printToShell, boolean debug)
	{
		if(printToShell || debug)
		{
			System.out.print("Checking tape status for " + barcode + "\t");
		}
		
		ObjectsOnTapeCall tape = java_cli.getTapeContents(barcode);

		if(tape != null)
		{
			// No objects on tape.
			if(tape.getObjectName(0).equals("Invalid index selected"))
			{
				if(printToShell || debug)
				{
					System.out.println("[PENDING]");
				}
	
				logbook.logWithSizedLogRotation("Tape " + barcode + " is available to eject.", 2);
				return "pending";
			}
			else // tape is in use
			{
				if(printToShell || debug)
				{
					System.out.println("[IN-USE]");
				}
					
				logbook.logWithSizedLogRotation("Tape " + barcode + " holds data.", 2);
				return "tape is in use.";
			}
		}
		else
		{
			if(printToShell || debug)
			{
				System.out.println("[NOT FOUND]");
			}
			
			return "not found";
		}
	}

	public static void fromList(BasicCommands java_cli, String tapeListPath, String output_path, int max_moves, boolean ignoreUsedTapes, Logger logbook, boolean printToShell, boolean debug)
	{
		ArrayList<String[]> tapes;
		int moves = 0;
		int i=0;

		logbook.logWithSizedLogRotation("Ejecting tapes from list [" + tapeListPath + "]...", 1);

		if(printToShell || debug) // debug or print to shell should print this process.
		{
			System.out.println("Starting tape ejection process...");
		}
		
		// Get list of tapes.
		tapes = FileInterface.importSingleColumnList(tapeListPath, logbook, printToShell, debug);
		
		logbook.logWithSizedLogRotation("Imported (" + tapes.size() + ") tapes from file.", 1);

		// Check tape status;

		if(printToShell || debug)
		{
			System.out.println("Checking tape status...");
		}

		while(i<tapes.size() && moves < max_moves)
		{
			if(!ignoreUsedTapes)
			{
				tapes.get(i)[1] = checkTapeUsage(java_cli, tapes.get(i)[0], logbook, printToShell, debug);
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
				if(printToShell || debug)
				{
					System.out.print("Ejecting tape " + tapes.get(i)[0] + "\t");

				}	

				if(java_cli.ejectTape(tapes.get(i)[0]))
				{
					tapes.get(i)[1] = "ejected";
					moves++;

					logbook.logWithSizedLogRotation("Ejected tape " + tapes.get(i)[0], 2);
						
					if(printToShell || debug)
					{
						System.out.println("[EJECTED]");
					}
				}
				else
				{
					tapes.get(i)[1] = "failed";
						
					if(printToShell || debug)
					{
						System.out.println("[FAILED]");
					}
				}
			}
			i++;
		}

		// Update the csv to remove these tapes.
		
		FileInterface.updateSingleColumnInputFile(tapes, tapeListPath, output_path + "/completed_ejects.csv", debug);
	}
}
