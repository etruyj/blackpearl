//===================================================================
// Metadata.java
// 	This class holds the metadata that is returned by the BlackPearl
// 	ds3 api calls. From my experience, this is only a timestamp string.
//===================================================================

package com.socialvagrancy.blackpearl.jsonObject;

public class Metadata
{
	String Date;

	public String getDate()
	{
		String[] info = Date.split("T");
		return info[0];
	}

	public String getTime()
	{
		String[] info = Date.split("T"); // Get the time section of the timestamp.
		info = info[1].split("."); // Cut off the .xxxZ suffix.
		return info[0];
	}
}
