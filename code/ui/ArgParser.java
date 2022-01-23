//===================================================================
// ArgParser.java
// 	Uses a switch statement to parse input flags.
//===================================================================

package com.socialvagrancy.blackpearl.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.Yaml;

public class ArgParser
{
	// ds3_java_cli credential vars
	private String ds3Path;
	private String endpoint_ip;
	private String access_key;
	private String secret_key;
	private boolean https;
	// ds2_java_cli vars
	private String bucket;
	// script vars
	private boolean debugging;
	private String filePrefix;
	private boolean ignoreUsedTapes;
	private String inputFilePath;
	private boolean isWindows;
	private int max_moves;
	private String output_path;
	private boolean print;
	private int restore_count;
	private String savePath;
	private boolean printHelp;
	private boolean clear_cache;
	private long max_file_size;

	public ArgParser()
	{
		// Constructor without config file.
		ds3Path = ".";
		savePath = ".";
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
		max_file_size = 0;
		restore_count = 1;
		ignoreUsedTapes = false;
		printHelp = false;
		clear_cache = false;
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
		max_file_size = config.getFileSize();
		ignoreUsedTapes = false;
		restore_count = config.getRestoreCount();
		printHelp = false;
		clear_cache = false;

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
	public long getMaxFileSize() { return max_file_size; }
	public boolean printHelp() { return printHelp; }
	public int getRestoreCount() { return restore_count; }
	public boolean getClearCache() { return clear_cache; }

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

	private long convertUnitToBytes(String unit)
	{
		long conversion = 1;

		switch(unit)
		{
			case "P":
			case "p":
				conversion *= 1024;
			case "T":
			case "t":
				conversion *= 1024;
			case "G":
			case "g":
				conversion *= 1024;
			case "M":
			case "m":
				conversion *= 1024;
			case "K":
			case "k":
				conversion *= 1024;
				break;
		}

		return conversion;
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
				case "--clear-cache":
					clear_cache = true;
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
				case "--max-size":
				case "--size":
					if(i+1<args.length)
					{
						max_file_size = Integer.valueOf(args[i+1].substring(0, args[i+1].length()-1));
						String unit = args[i+1].substring(args[i+1].length()-1, args[i+1].length());
						max_file_size = max_file_size * convertUnitToBytes(unit);
					}
					break;
				case "-p":
				case "--print":
					print = true;
					break;
				case "--restores":
				case "--restores-per-tape":
					if(i+1<args.length)
					{
						restore_count = Integer.valueOf(args[i+1]);
						i++;
					}
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
