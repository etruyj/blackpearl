//===================================================================
// BPCLI.java
// 	DESCRIPTION: This is the main user access point for CLI access
// 	to the blackpearl CLI script. ArgParser is used to process 
// 	flags and the the appropriate UTILS/controller function is called
// 	in order to process the command.
//
// 	**CONTAINS MAIN()**
//===================================================================

package com.socialvagrancy.blackpearl.ui;

import com.socialvagrancy.blackpearl.utils.Controller;

public class BPCLI
{
	public static void main(String[] args)
	{
		ArgParser aparser = new ArgParser("../config.yaml");
		aparser.parseInputs(args);

		Controller controller = new Controller(aparser.getDS3Path(), aparser.getConnectionState(), aparser.getEndpoint(), aparser.getAccessKey(), aparser.getSecretKey(), aparser.getIsWindows(), aparser.getLogPath(), aparser.getLogCount(), aparser.getLogSize(), aparser.getLogLevel());

		switch(aparser.getCommand())
		{
			case "download-database":
				controller.downloadDatabase(aparser.getBucket(), aparser.getOutputFile(), aparser.getFilePrefix(), aparser.getPrintToShell(), aparser.getDebugging(), aparser.getIsWindows());
				break;
			case "delete-objects":
				controller.deleteObjectsFromSingleColumnList(aparser.getBucket(), aparser.getInputFile(), aparser.getOutputFile(), aparser.getPrintToShell(), aparser.getDebugging());
				break;
			case "eject-tapes":
				controller.ejectTapesFromList(aparser.getInputFile(), aparser.getOutputFile(), aparser.getMaxMoves(), aparser.getIgnoreUsedTapes(), aparser.getPrintToShell(), aparser.getDebugging());
				break;
			default:
				System.err.println(aparser.getCommand());
				System.err.println("Invalid command selected. Use -c help to see a list of valid commands.");
				break;
		}


	}

}
