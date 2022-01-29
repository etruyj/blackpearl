//===================================================================
// BasicCommands.java
// 	Description:
// 		This class handles formating commands that are passed
// 		directly to the DS3Interface and passing that
// 		information back up to the calling function, whether
// 		that is the controller or for further processing 
// 		under Advanced commands.
//===================================================================

package com.socialvagrancy.blackpearl.commands;

import com.socialvagrancy.blackpearl.structures.APICall;
import com.socialvagrancy.blackpearl.structures.ObjectsOnTapeCall;
import com.socialvagrancy.blackpearl.utils.DS3Interface;
import com.socialvagrancy.utils.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class BasicCommands
{
	DS3Interface blackpearl;
	Logger logbook;
	String secret_key;

	public BasicCommands(String path_ds3, boolean isSecure, String data_ip, String access_key, String secretKey, boolean isWindows, Logger logs)
	{
		blackpearl = new DS3Interface(path_ds3, isSecure, data_ip, access_key, isWindows);
		logbook = logs;
		secret_key = secretKey;
	}	

	public boolean deleteObject(String bucket, String object, boolean confirmed)
	{
		logbook.logWithSizedLogRotation("CALL: deleteObject(" + bucket + ", " + object + ") with confirmed deletion " + String.valueOf(confirmed), 1);
	
		// Validation needed to delete the object.
		if(!confirmed)
		{
			System.out.println("Are you sure you want to delete " + object + "? Type DELETE to confirm.");
			String confirmation = System.console().readLine();

			if(confirmation.equals("DELETE"))
			{
				logbook.logWithSizedLogRotation("Object (" + object + ") deletion confirmed by user.", 2);
				confirmed = true;
			}
		}
		
		if(confirmed)
		{
			String command = " -c delete_object -b " + bucket + " -o " + object;

			if(executeBooleanCall(command))
			{
				return true;
			}
			else
			{
					logbook.logWithSizedLogRotation("ERROR: Failed to delete object.", 3);

					return false;
				}
			}
			else
			{
				logbook.logWithSizedLogRotation("WARN: deleteObject(" + bucket + ", " + object + ") aborted. Deletion not confirmed by user.", 2);
		
			return false;
		}
	}
	
	public boolean ejectTape(String barcode)
	{
		logbook.logWithSizedLogRotation("CALL: ejectTape(" + barcode + ")", 1);

		String command = " -c eject_tape -i " + barcode;

		if(executeBooleanCall(command))
		{
			return true;
		}
		else
		{
			logbook.logWithSizedLogRotation("ERROR: Failed to eject tape [" + barcode + "].", 3);
				
			return false;
		}
	}

	public boolean executeBooleanCall(String command)
	{
		if(blackpearl.instanceValid())
		{
			Gson gson = new Gson();

			String response = blackpearl.executeProcess(command + " -k " + secret_key);

			try
			{
				APICall result = gson.fromJson(response, APICall.class);
	
				logbook.logWithSizedLogRotation(result.getAPICallMessage(), 2);
			
				if(result.getAPICallStatus().equals("OK"))
				{
					return true;
				}
				else
				{
					return false;
				}
			}
			catch(JsonParseException e)
			{
				logbook.logWithSizedLogRotation(e.getMessage(), 3);
				logbook.logWithSizedLogRotation(blackpearl.getCommand() + command, 2);

				return false;
			}
		}
		else
		{
			logbook.logWithSizedLogRotation("ERROR: Unable to initialize java_cli instance.", 3);
			
			return false;
		}
	}

	public void getBucketSize(String bucket)
	{
		//logbook.logWithSizedLogRotation();
	}

	public void getObject(String bucket, String object)
	{
		//logbook.logWithSizedLogRotation();
	}

	public ObjectsOnTapeCall getTapeContents(String barcode)
	{
		if(blackpearl.instanceValid())
		{
			logbook.logWithSizedLogRotation("CALL: getTapeContents(" + barcode + ")", 1);
	
			Gson gson = new Gson();
	
			String command = " -c get_objects_on_tape -i " + barcode;

			String response = blackpearl.executeProcess(command + " -k " + secret_key);

			try
			{
				ObjectsOnTapeCall objects = gson.fromJson(response, ObjectsOnTapeCall.class);

				logbook.logWithSizedLogRotation("Found (" + objects.getObjectCount() + ") on tape [" + barcode + "]", 1);
			
				return objects;
			}
			catch(JsonParseException e)
			{
				logbook.logWithSizedLogRotation(e.getMessage(), 3);
				logbook.logWithSizedLogRotation(blackpearl.getCommand() + command, 1);
				logbook.logWithSizedLogRotation("ERROR: Unable to locate tape [" + barcode + "]", 3);
			
				return null;
			}
		}
		else
		{
			logbook.logWithSizedLogRotation("ERROR: Unable to initialize java_cli instance.", 3);
		
			return null;
		}
	}
}
