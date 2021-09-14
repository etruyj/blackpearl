//===================================================================
// DS3Interface.java
//	This is the command line utility that cals the ds3_java_cli
//	and returns the value.
//===================================================================

package com.socialvagrancy.blackpearl.ds3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.lang.ProcessBuilder;
import java.lang.Process;
import java.lang.StringBuilder;

public class DS3Interface
{
	ProcessBuilder processor;
	String cmd;

	public DS3Interface(String pathToJavaCLI, boolean isSecure, String endpoint, String access_key, String secret_key, boolean isWindows)
	{
		processor = new ProcessBuilder();

		if(isWindows)
		{
			cmd = buildCommandPathWindows(pathToJavaCLI, isSecure, endpoint, access_key, secret_key);
		}
		else
		{
			cmd = buildCommandPathLinux(pathToJavaCLI, isSecure, endpoint, access_key, secret_key);
		}
			
	}

	public String buildCommandPathLinux(String pathToJavaCLI, boolean isSecure, String endpoint, String access_key, String secret_key)
	{
		String command = pathToJavaCLI + "/ds3_java_cli -e " + endpoint + " -a " 
			+ access_key + " -k " + secret_key + " --output-format json";

		if(!isSecure)
		{
			command += " --http";
		}

		return command;
	}

	public String buildCommandPathWindows(String pathToJavaCLI, boolean isSecure, String endpoint, String access_key, String secret_key)
	{
		String command = pathToJavaCLI + "/ds3_java_cli.bat -e " + endpoint + " -a "
			+ access_key + " -k " + secret_key + " --output-format json";

		if(!isSecure)
		{
			command += " --http";
		}

		return command;
	}

	public String executeProcess(String command, boolean isWindows)
	{
		String returnJson = "none";

		if(isWindows)
		{
			processor.command("powershell.exe", "/c", command);
		}
		else // linux/mac os
		{
			processor.command("bash", "-c", command);
		}

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

		return returnJson;
	}

	public String getCommand() { return cmd; }
}
