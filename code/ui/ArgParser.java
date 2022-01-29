//===================================================================
// ArgParser.java
// 	Uses a switch statement to parse input flags.
//===================================================================

package com.socialvagrancy.blackpearl.ui;

import com.socialvagrancy.blackpearl.structures.ArgConfig;
import com.socialvagrancy.utils.storage.UnitConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;

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
	private boolean isWindows;
	// script vars
	private String command;
	private boolean print;
	private boolean debugging;
	private boolean ignoreUsedTapes;
	private String bucket;
	private int max_moves;
	private String filePrefix; // for naming log files and database backups.
	private int restore_count;
	private String inputFilePath;
	private String outputFilePath;
	private boolean printHelp;
	private boolean clear_cache;
	private BigInteger max_file_size;
	// Logging Vars
	private String logPath;
	private int log_count;
	private int log_level;
	private int log_size;

	public ArgParser()
	{
		// Constructor without config file.
		ds3Path = ".";
		endpoint_ip = "127.0.0.1";
		bucket = "Spectra";
		access_key = "none_provided";
		secret_key = "none_provided";
		isWindows = false;
		https = true;
		print = false;
		filePrefix = "blackpearl";
		debugging = false;
		logPath = "../logs/ds3.log";
		inputFilePath = "./nothing.here";
		outputFilePath = "./output";
		max_moves = 0;
		max_file_size = new BigInteger("0");
		restore_count = 1;
		ignoreUsedTapes = false;
		printHelp = false;
		clear_cache = false;

		logPath = "../logs/ds3_main.log";
		log_count = 2;
		log_size = 10240;
		log_level = 1;
	}

	public ArgParser(String configFile)
	{
		// Constructor with a config file.
		ArgConfig config = loadConfiguration(configFile);


		ds3Path = config.getDS3Path();
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
		outputFilePath = config.getOutputFile();
		max_moves = config.getEESlots();
		max_file_size = config.getFileSize();
		ignoreUsedTapes = false;
		restore_count = config.getRestoreCount();
		printHelp = false;
		clear_cache = false;
	
		// Logging
		logPath = config.getLogPath();
		log_count = config.getLogCount();
		log_level = config.getLogLevel();
		log_size = config.getLogSize();
	}

	public String getAccessKey() { return access_key; }
	public String getBucket() { return bucket; }
	public String getCommand() { return command; }
	public boolean getConnectionState() { return https; }
	public boolean getDebugging() { return debugging; }
	public String getDS3Path() { return ds3Path; }
	public String getEndpoint() { return endpoint_ip; }
	public String getFilePrefix() { return filePrefix; }
	public boolean getIgnoreUsedTapes() { return ignoreUsedTapes; }
	public String getInputFile() { return inputFilePath; }
	public boolean getIsWindows() { return isWindows; }
	public String getLogPath() { return logPath; }
	public Integer getLogCount() { return log_count; }
	public Integer getLogLevel() { return log_level; }
	public Integer getLogSize() { return log_size; }
	public int getMaxMoves() { return max_moves; }
	public BigInteger getMaxFileSize() { return max_file_size; }
	public String getOutputFile() { return outputFilePath; }
	public boolean printHelp() { return printHelp; }
	public boolean getPrintToShell() { return print; }
	public int getRestoreCount() { return restore_count; }
	public String getSecretKey() { return secret_key; }

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
				case "-c":
				case "--command":
					if(i+1<args.length)
					{
						command = args[i+1];
						i++;
					}
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
					if(i+2<args.length)
					{
						max_file_size = UnitConverter.humanReadableToBytes(args[i+1] + " " + args[i+2]);
					}
					break;
				case "--output-file":
				case "--output":
					if(i+1<args.length)
					{
						outputFilePath = args[i+1];
						i++;
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
				case "--log-count":
					if(i+1<args.length)
					{
						log_count=Integer.valueOf(args[i+1]);
						i++;
					}
					break;
				case "--log-path":
					if(i+1<args.length)
					{
						logPath=args[i+1];
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
