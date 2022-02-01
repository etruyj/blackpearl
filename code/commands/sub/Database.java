//===================================================================
// DatabaseDownloader.java
// 	Description: Utilizes the APIInterface to call the ds3 java
// 	cli to automatically download the most recent object in the 
// 	bucket. In the case of the database backup bucket, it grabs
// 	the most recent database file.
//===================================================================

package com.socialvagrancy.blackpearl.commands.sub;

import com.socialvagrancy.blackpearl.commands.BasicCommands;
import com.socialvagrancy.blackpearl.structures.APICall;
import com.socialvagrancy.blackpearl.structures.ObjectsCall;
import com.socialvagrancy.utils.Logger;

import java.io.IOException;

public class Database
{
	public static void downloadMostRecentLog(BasicCommands java_cli, String bucket, String savePath, String prefix, Logger logbook, boolean printToShell, boolean debugging, boolean isWINDOWS)
	{
		logbook.logWithSizedLogRotation("Downloading most recent file from bucket [" + bucket + "].", 1);

		ObjectsCall objectList = java_cli.listObjects(bucket);
		
		logbook.logWithSizedLogRotation("Gather object list: " + objectList.getAPICallStatus(), 2);
		
		if(printToShell || debugging)
		{
			System.out.println("Gather Object List: " + objectList.getAPICallStatus());
		}

		// Restore object from bucket.			
	
		if(printToShell || debugging)
		{
			System.out.print("Downloading Object: ");
		}

		APICall api_response = java_cli.getObject(bucket, objectList.getObjectName(objectList.getNumObjects()-1), savePath);

		logbook.logWithSizedLogRotation("Download Object: " + api_response.getAPICallStatus(), 2);
		
		if(printToShell || debugging)
		{
			System.out.println(api_response.getAPICallStatus() + "\n");
			System.out.println(api_response.getAPICallMessage());	

		}

		// Move object to the retrieval location.
		renameFile(savePath, objectList.getObjectName(objectList.getNumObjects()-1), prefix, logbook, printToShell, debugging, isWINDOWS);
	}

	public static void renameFile(String savePath, String fileName, String prefix, Logger logbook, boolean printToShell, boolean debugging, boolean isWindows)
	{
		String[] splitFile = fileName.split("_");
		String newFile = prefix + "-" + splitFile[2] + ".tar.xz";
		String command;
		ProcessBuilder processor = new ProcessBuilder();


		// Prep command.
		if(isWindows)
		{
			command = "Rename-Item -Path " + savePath + "/" + fileName + " -NewName " + newFile;	
			processor.command("powershell.exe", "/c", command);
		}
		else
		{
			command = "mv " + savePath + "/" + fileName + " " 
				+ savePath + "/" + newFile;
			processor.command("bash", "-c", command);
		}

		logbook.logWithSizedLogRotation("Executing move command: " + command, 2);
		if(debugging)
		{
			System.out.println(command);
		}

		try
		{
			Process process = processor.start();
			
			if(printToShell || debugging)
			{
				System.out.println("File renamed successfully.");
			}	
		}
		catch(IOException e)
		{
			logbook.logWithSizedLogRotation("Error: " + e.getMessage(), 3);
			if(printToShell || debugging)
			{
				System.out.println(e.getMessage());
			}
		}
	}
}
