//===================================================================
// BucketSummary.java
// 	This class houses bucket information returned from the ds3
// 	java cli commands.
//===================================================================

package com.socialvagrancy.blackpearl.jsonObject;

public class BucketSummary
{
	String CreationDate; // Using the metadata class as it has the same variable format.
	String Name; // bucket name.

	public String getDate() 
	{
	       String[] info = CreationDate.split("T");	
		return info[0]; 
	}
	public String getName() { return Name; }
	public String getTime() 
	{ 
		String[] info = CreationDate.split("T");
		info = info[1].split(".");
		return info[0]; 
	}
}
