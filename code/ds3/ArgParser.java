//===================================================================
// ArgParser.java
// 	Uses a switch statement to parse input flags.
//===================================================================

package com.socialvagrancy.blackpearl.ds3;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.Yaml;

public class ArgParser
{
	// ds3_java_cli credential vars
	String ds3Path;
	String endpoint_ip;
	String access_key;
	String secret_key;
	boolean https;
	// ds2_java_cli vars
	String bucket;
	// script vars
	boolean debugging;
	String filePrefix;
	boolean ignoreUsedTapes;
	String inputFilePath;
	boolean isWindows;
	int max_moves;
	boolean print;
	String savePath;
	boolean printHelp;

	public ArgParser()
	{
		// Constructor without config file.
		ds3Path = "./";
		savePath = "./";
		endpoint_ip = "127.0.0.1";
		bucket = "Spectra";
		access_key = "none_provided";
		secret_key = "none_provided";
		isWindows = false;
		https = true;
		print = false;
		filePrefix = "blackpearl_database_backup-";
		debugging = false;
		inputFilePath = "./nothing.here";
		max_moves = 0;
		ignoreUsedTapes = false;
		printHelp = false;
	}

	public ArgParser(String configFile)
	{
		// Constructor with a config file.
		ArgConfig config = loadConfiguration(configFile);


		ds3Path = config.getDS3Path();
		savePath = config.getSavePath();
		endpoint_ip = config.getEndpoint();
		bucket = config.getBucket();
		access_key = config.getAccessKey();
		secret_key = config.getSecretKey();
		isWindows = false;
		https = config.getIsSecure();
		print = config.getPrint();
		filePrefix = config.getFilePrefix();
		debugging = config.getDebugging();
		inputFilePath = config.getInputFile();
		max_moves = config.getEESlots();
		ignoreUsedTapes = false;
		printHelp = false;

	}

	public boolean getDebugging() { return debugging; }
	public String getDS3Path() { return ds3Path; }
	public String getSavePath() { return savePath; }
	public String getEndpoint() { return endpoint_ip; }
	public String getFilePrefix() { return filePrefix; }
	public String getBucket() { return bucket; }
	public String getAccessKey() { return access_key; }
	public String getSecretKey() { return secret_key; }
	public boolean getConnectionState() { return https; }
	public boolean getIsWindows() { return isWindows; }
	public boolean getPrintToShell() { return print; }
	public boolean getIgnoreUsedTapes() { return ignoreUsedTapes; }
	public String getInputFile() { return inputFilePath; }
	public int getMaxMoves() { return max_moves; }
	public boolean printHelp() { return printHelp; }

	public ArgConfig loadConfiguration(String filePath)
	{
		try
		{
			Yaml yaml = new Yaml(new Constructor(ArgConfig.class));

			InputStream iStream = new FileInputStream(new File(filePath));
			
			ArgConfig configuration = yaml.load(iStream);

			return configuration;

		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			return new ArgConfig();
		}
	}

	public void parseInputs(String[] args)
	{
		for(int i=0; i<args.length; i++)
		{
			switch(args[i])
			{
				case "-a":
				case "--access-key":
					if(i+1<args.length)
					{
						access_key = args[i+1];
						i++;
					}
					break;	
				case "-b":
				case "--bucket":
					if(i+1<args.length)
					{
						bucket = args[i+1];
						i++;
					}
					break;
				case "--debug":
					debugging = true;
					break;	
				case "--ds3":
				case "--java-cli":
					if(i+1<args.length)
					{
						ds3Path = args[i+1];
						i++;
					}
					break;
				case "-e":
				case "--endpoint":
					if(i+1<args.length)
					{
						endpoint_ip = args[i+1];
						i++;
					}
					break;
				case "--file-prefix":
					if(i+1<args.length)
					{
						filePrefix = args[i+1];
						i++;
					}
					break;
				case "-h":
				case "--help":
					printHelp = true;
					break;
				case "--https":
					https = true;
					break;
				case "--http":
				case "--insecure":
					https = false;
					break;
				case "--ignore-used-tapes":
					ignoreUsedTapes=true;
					break;
				case "--input-file":
					if(i+1<args.length)
					{
						inputFilePath = args[i+1];
						i++;
					}
					break;
				case "-k":
				case "--secret-key":
					if(i+1<args.length)
					{
						secret_key = args[i+1];
						i++;
					}	
					break;
				case "-m":
				case "--moves":
				case "--max-moves":
					if(i+1<args.length)
					{
						max_moves = Integer.valueOf(args[i+1]);
						i++;
					}
					break;
				case "-p":
				case "--print":
					print = true;
					break;
				case "--save-path":
					if(i+1<args.length)
					{
						savePath=args[i+1];
						i++;
					}
					break;
				case "--win":
					isWindows=true;
					break;

			}
		}
	}
}
