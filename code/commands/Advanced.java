//===================================================================
// Advanced.java
// 	Description: This class acts at the management layer of the
// 	BasicCommands and the sub command scripts to allow easier 
// 	interfacing with the UI/ layer via the UTILS/controller.
//===================================================================

package com.socialvagrancy.blackpearl.commands;

import com.socialvagrancy.blackpearl.commands.sub.DeleteObjects;
import com.socialvagrancy.blackpearl.commands.sub.TapeEjection;
import com.socialvagrancy.utils.Logger;

public class Advanced
{
	Logger logbook;
	BasicCommands java_cli;

	public Advanced(BasicCommands bsc, Logger logs)
	{
		java_cli = bsc;
		logbook = logs;
	}
	
	public void deleteListSingleColumn(String bucket, String input_path, String output_path, boolean printToShell, boolean debug)
	{
		DeleteObjects.fromSingleColumnList(java_cli, bucket, input_path, output_path, logbook, printToShell, debug);
	}

	public void ejectTapes(String input_path, String output_path, int max_moves, boolean ignoreUsedTapes, boolean printToShell, boolean debug)
	{
		TapeEjection.fromList(java_cli, input_path, output_path, max_moves, ignoreUsedTapes, logbook, printToShell, debug);
	}
}
