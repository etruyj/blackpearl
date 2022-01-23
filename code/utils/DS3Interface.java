//===================================================================
// DS3Interface.java
//	This is the command line utility that cals the ds3_java_cli
//	and returns the value.
//===================================================================

package com.socialvagrancy.blackpearl.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;

import java.lang.ProcessBuilder;
import java.lang.Process;
import java.lang.StringBuilder;

public class DS3Interface
{
	ProcessBuilder processor;
	String cmd;
	boolean isValid; // Whether the java_cli instance is valid or not.
	boolean WINDOWS; // Whether the application is Windows or not.

	public DS3Interface(String pathToJavaCLI, boolean isSecure, String endpoint, String access_key, boolean isWindows)
	{
		isValid = true;
		WINDOWS = isWindows;

		processor = new ProcessBuilder();

		if(isWindows)
		{
			cmd = buildCommandPathWindows(pathToJavaCLI, isSecure, endpoint, access_key);
		}
		else
		{
			cmd = buildCommandPathLinux(pathToJavaCLI, isSecure, endpoint, access_key);
		}
			
	}

	public String buildCommandPathLinux(String pathToJavaCLI, boolean isSecure, String endpoint, String access_key)
	{
		File eFile = new File(pathToJavaCLI + "/ds3_java_cli");
		String command;

		if(eFile.isFile())
		{
	
			command = pathToJavaCLI + "/ds3_java_cli -e " + endpoint + " -a " 
				+ access_key + " --output-format json";
	
			if(!isSecure)
			{
				command += " --http";
			}
		}
		else
		{
			System.out.println("File path for java_cli client is invalid. Commands will fail. Please verify file path: " + pathToJavaCLI + "/ds3_java_cli");
			command = "ERROR";
			isValid = false;
		}
			
		return command;
	}

	public String buildCommandPathWindows(String pathToJavaCLI, boolean isSecure, String endpoint, String access_key)
	{
		File eFile = new File(pathToJavaCLI + "/ds3_java_cli.bat");
		String command;

		if(eFile.isFile())
		{
			command = pathToJavaCLI + "/ds3_java_cli.bat -e " + endpoint + " -a "
				+ access_key + " --output-format json";

			if(!isSecure)
			{
				command += " --http";
			}
		}
		else
		{
			System.out.println("File path for java_cli client is invalid. Commands will fail. Please verify file path: " + pathToJavaCLI + "/ds3_java_cli.bat");
			command = "ERROR";
			isValid = false;
		}

		return command;
	}

	public String executeProcess(String command)
	{
		String returnJson = "none";

		if(WINDOWS)
		{
			processor.command("powershell.exe", "/c", command);
		}
		else // linux/mac os
		{
			processor.command("bash", "-c", command);
		}

		if(isValid)
		{
			try
			{
				Process process = processor.start();
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
				StringBuilder outJson = new StringBuilder();
				
				String output = null;

				while((output = stdInput.readLine()) != null)
				{
					outJson.append(output + "\n");
				}

				returnJson = outJson.toString();
				returnJson = returnJson.substring(0, returnJson.length()-1);
			}
			catch(IOException e)
			{
				System.out.println(e.getMessage());
			}
	
		}

		return returnJson;
	}

	public String getCommand() { return cmd; }
	public boolean instanceValid() { return isValid; }
}
