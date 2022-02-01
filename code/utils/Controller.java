//===================================================================
// Controller.java
// 	Description: This class provides the abstraction between the
// 	COMMAND classes and the UI classes in order to streamline
// 	script operation.
//===================================================================

package com.socialvagrancy.blackpearl.utils;

import com.socialvagrancy.blackpearl.commands.Advanced;
import com.socialvagrancy.blackpearl.commands.BasicCommands;
import com.socialvagrancy.utils.Logger;

public class Controller
{
	Advanced advanced;
	BasicCommands java_cli;

	public Controller(String ds3_path, boolean isSecure, String data_ip, String access_key, String secret_key, boolean isWindows, String log_path, int log_size, int log_count, int log_level)
	{
		Logger logs = new Logger(log_path, log_count, log_size, log_level);
		java_cli = new BasicCommands(ds3_path, isSecure, data_ip, access_key, secret_key, isWindows, logs);
		advanced = new Advanced(java_cli, logs);
	}

	public void downloadDatabase(String bucket, String savePath, String prefix, boolean printToShell, boolean debug, boolean isWINDOWS)
	{
		advanced.downloadDatabase(bucket, savePath, prefix, printToShell, debug, isWINDOWS);
	}

	public void deleteObjectsFromSingleColumnList(String bucket, String input_path, String output_path, boolean printToShell, boolean debug)
	{
		advanced.deleteListSingleColumn(bucket, input_path, output_path, printToShell, debug);
	}

	public void ejectTapesFromList(String input_path, String output_path, int max_moves, boolean ignoreUsedTapes, boolean printToShell, boolean debug)
	{
		advanced.ejectTapes(input_path, output_path, max_moves, ignoreUsedTapes, printToShell, debug);
	}
}
