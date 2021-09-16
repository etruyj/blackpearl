//===================================================================
// DatabaseDownloader.java
// 	Description: Utilizes the APIInterface to call the ds3 java
// 	cli to automatically download the most recent object in the 
// 	bucket. In the case of the database backup bucket, it grabs
// 	the most recent database file.
//===================================================================

package com.socialvagrancy.blackpearl.databaseDownloader;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import com.socialvagrancy.blackpearl.ds3.ArgParser;
import com.socialvagrancy.blackpearl.ds3.DS3Interface;
import com.socialvagrancy.blackpearl.jsonObject.*;
import com.socialvagrancy.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException;

import java.lang.ProcessBuilder;
import java.lang.Process;

public class DatabaseDownloader
{
	DS3Interface api_client;
	Gson gson;
	Logger logbook;
	
	public DatabaseDownloader()
	{
		gson = new Gson();
		logbook = new Logger("../logs/database-download.log", 10240, 1, 1);
	}

	public String buildCommandGetObject(String base_command, String bucket, String object, String savePath)
	{
		return base_command + " -c get_object -b " + bucket 
			+ " -d " + savePath + " -o " + object;
	}

	public String buildCommandListObjects(String base_command, String bucket)
	{
		return base_command + " -c get_bucket -b " + bucket;
	}

	public void downloadMostRecentLog(String ds3Path, String savePath, String prefix, String endpoint, boolean isSecure, String bucket, String access_key, String secret_key, boolean isWINDOWS, boolean printToShell, boolean debugging)
	{
		api_client = new DS3Interface(ds3Path, isSecure, endpoint, access_key, secret_key, isWINDOWS);
		
		String base_command;
		String process_command;
		String json_response;
		ObjectsCall objectList;
		APICall api_response;

		base_command = api_client.getCommand();

		// Get last object from bucket.
		process_command = buildCommandListObjects(base_command, bucket);

		json_response = api_client.executeProcess(process_command, isWINDOWS);
		if(debugging)
		{
			System.out.println(process_command);
			System.out.println("\n" + json_response + "\n");
		}

		try
		{
			objectList = gson.fromJson(json_response, ObjectsCall.class);
			
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

			process_command = buildCommandGetObject(base_command, bucket, objectList.getObjectName(objectList.getNumObjects()-1), savePath);

			json_response = api_client.executeProcess(process_command, isWINDOWS);
	

			api_response = gson.fromJson(json_response, APICall.class);

			logbook.logWithSizedLogRotation("Download Object: " + api_response.getAPICallStatus(), 2);
		
			if(printToShell || debugging)
			{
				System.out.println(api_response.getAPICallStatus() + "\n");
				System.out.println(api_response.getAPICallMessage());	

				if(debugging)
				{
					System.out.println(process_command);
					System.out.println("\n" + json_response + "\n");
				}	
			}

			// Move object to the retrieval location.
			renameFile(savePath, objectList.getObjectName(objectList.getNumObjects()-1), prefix, printToShell, debugging, isWINDOWS);
		}
		catch(JsonParseException e)
		{
			logbook.logWithSizedLogRotation(e.getMessage(), 3);

			if(debugging)
			{
				System.out.println(e.getMessage());
			}
		}


		

	}

	public void printHelp()
	{
		try
		{
			File inFile = new File("../lib/help/database_download.txt");

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

	public void renameFile(String savePath, String fileName, String prefix, boolean printToShell, boolean debugging, boolean isWindows)
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

	public static void main(String[] args)
	{
		ArgParser aparser = new ArgParser();
		DatabaseDownloader logGather = new DatabaseDownloader();
		
		aparser.parseInputs(args);

		if(aparser.printHelp())
		{
			logGather.printHelp();
		}	
		else
		{
	
			logGather.downloadMostRecentLog(aparser.getDS3Path(), aparser.getSavePath(), aparser.getFilePrefix(), aparser.getEndpoint(), aparser.getConnectionState(), aparser.getBucket(), aparser.getAccessKey(), aparser.getSecretKey(), aparser.getIsWindows(), aparser.getPrintToShell(), aparser.getDebugging());
		}
	}
}
