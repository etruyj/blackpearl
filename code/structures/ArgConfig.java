//===================================================================
// ArgConfig.java
// 	This file contains the variables that can be defined by a
// 	configuration file. 
//===================================================================

package com.socialvagrancy.blackpearl.structures;

import com.socialvagrancy.utils.storage.UnitConverter;

import java.math.BigInteger;

public class ArgConfig
{
	public String access_key;
	public String bucket;
	public Boolean debug;
	public String ds3_path;
	public String endpoint;
	public String file_prefix;
	public String file_size;
	public Boolean https;
	public String input_file;
	public Integer entry_exit_slots;
	public Boolean print_output;
	public Integer restores_per_tape;
	public String output_path;
	public String secret_key;

	// Logging
	public String log_path;
	public Integer log_size;
	public Integer log_count;
	public Integer log_level;

	public String getAccessKey()
	{
		if(access_key==null)
		{
			return "none";
		}
		else
		{
			return access_key;
		}
	}

	public String getBucket()
	{
		if(bucket==null)
		{
			return "none";
		}
		else
		{
			return bucket;
		}
	}

	public boolean getDebugging()
	{
		if(debug==null)
		{
			return false;
		}
		else
		{
			return debug;
		}
	}

	public String getDS3Path()
	{
		if(ds3_path==null)
		{
			return "none";
		}
		else
		{
			return ds3_path;
		}
	}

	public String getEndpoint()
	{
		if(endpoint==null)
		{
			return "none";
		}
		else
		{
			return endpoint;
		}
	}

	public String getFilePrefix()
	{
		if(file_prefix==null)
		{
			return "none";
		}
		else
		{
			return file_prefix;
		}
	}

	public BigInteger getFileSize()
	{
		if(file_size==null)
		{
			return new BigInteger("0");
		}
		else
		{
			BigInteger file_size_bytes = UnitConverter.humanReadableToBytes(file_size);
			
			return file_size_bytes;
		}
	}

	public boolean getIsSecure()
	{
		if(https==null)
		{
			return true;
		}
		else
		{
			return https;
		}
	}

	public String getInputFile()
	{
		if(input_file==null)
		{
			return "none";
		}
		else
		{
			return input_file;
		}
	}

	public int getEESlots()
	{
		if(entry_exit_slots==null)
		{
			return 0;
		}
		else
		{
			return entry_exit_slots;
		}
	}

	public String getOutputFile()
	{
		if(output_path==null)
		{
			return "../output/";
		}
		else
		{
			return output_path;
		}
	}

	public boolean getPrint()
	{
		if(print_output==null)
		{
			return false;
		}
		else
		{
			return print_output;
		}
	}

	public int getRestoreCount()
	{
		if(restores_per_tape==null)
		{
			return 0;
		}
		else
		{
			return restores_per_tape;
		}
	}

	public String getSecretKey()
	{
		if(secret_key==null)
		{
			return "none";
		}
		else
		{
			return secret_key;
		}
	}

	public String getLogPath()
	{
		if(log_path==null)
		{
			return "none";
		}
		else
		{
			return log_path;
		}
	}

	public int getLogCount()
	{
		if(log_count==null)
		{
			return 0;
		}
		else
		{
			return log_count;
		}
	}

	public int getLogSize()
	{
		if(log_size==null)
		{
			return 0;
		}
		else
		{
			return log_size;
		}
	}

	public int getLogLevel()
	{
		if(log_level==null)
		{
			return 0;
		}
		else
		{
			return log_level;
		}
	}
}
