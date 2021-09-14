//===================================================================
// APIInterface.java
// 	Description: Calls the java cli and translates that information
// 	into a java variable.
//===================================================================

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.lang.ProcessBuilder;
import java.lang.Process;
import java.lang.StringBuilder;

public class APIInterface
{
	ProcessBuilder processor;	
	Gson gson;

	public APIInterface()
	{
		processor = new ProcessBuilder();
		gson = new Gson();
	}

	public String buildCommandGetObject(String cmd, String bucket, String object, String savePath)
	{
		return cmd + " -c get_object -b " + bucket  + " -o " + object + " -d " + savePath;
	}

	public String buildCommandListObjects(String cmd, String bucket)
	{
		return cmd + " -c get_bucket -b " + bucket;
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

	public String buildCommandRenameFileLinux(String path, String fileName, String prefix)
	{
		String[] splitFile = fileName.split("_");
	       	String newFile = prefix + "-" + splitFile[2] + ".tar.xz";
		String command = "mv " + path + "/" + fileName + " " + path + "/" + newFile;

		return command;
	}

	public String buildCommandRenameFileWindows(String path, String fileName, String prefix)
	{
		String[] splitFile = fileName.split("_");
	       	String newFile = prefix + "-" + splitFile[2] + ".tar.xz";
		String command = "Rename-Item -Path " + path + "/" + fileName + " -NewName " + newFile;

		return command;
	}



	public String executeProcess(String command, boolean isWINDOWS)
	{
		String returnJson = "none";
		ProcessBuilder processor = new ProcessBuilder();
		
		if(isWINDOWS)
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
		}
		catch(IOException e)
		{
			System.out.println(e.getMessage());
		}

		return returnJson;
	}

}
