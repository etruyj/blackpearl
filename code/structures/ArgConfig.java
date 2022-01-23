//===================================================================
// ArgConfig.java
// 	This file contains the variables that can be defined by a
// 	configuration file. 
//===================================================================

package com.socialvagrancy.blackpearl.structures;

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
	public String save_path;
	public String secret_key;

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

	public long getFileSize()
	{
		if(file_size==null)
		{
			return 0;
		}
		else
		{
			long file_size_bytes = 1;
			String file_unit = file_size.substring(file_size.length()-2, file_size.length()-1);

			while(!file_unit.equals("B"))
			{
				switch(file_unit)
				{
					case "P":
						file_unit = "T";
						break;
					case "T":
						file_unit = "G";
						break;
					case "G":
						file_unit = "M";
						break;
					case "M":
						file_unit = "K";
						break;
					case "K":
						file_unit = "B";
						break;
					default:
						file_unit = "B";
						break;
				}

				file_size_bytes = file_size_bytes * 1024;
			}

			file_size_bytes = Integer.valueOf(file_size.substring(0, file_size.length()-2)) * file_size_bytes;

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

	public String getSavePath()
	{
		if(save_path==null)
		{
			return ".";
		}
		else
		{
			return save_path;
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

}
