//===================================================================
// ArgConfig.java
// 	This file contains the variables that can be defined by a
// 	configuration file. 
//===================================================================

package com.socialvagrancy.blackpearl.ds3;

public class ArgConfig
{
	private String access_key;
	private String bucket;
	private Boolean debug;
	private String ds3_path;
	private String endpoint;
	private String file_prefix;
	private String file_size;
	private Boolean https;
	private String input_file;
	private Integer entry_exit_slots;
	private Boolean print_output;
	private String save_path;
	private String secret_key;

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
