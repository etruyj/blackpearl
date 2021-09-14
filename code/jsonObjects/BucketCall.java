//===================================================================
// BucketCall.java
// 	This class creates the structure to translate the json response
// 	from the ds3_java_cli get_bucket api call.
//===================================================================

package com.socialvagrancy.blackpearl.jsonObject;

public class BucketCall extends APICall
{
	Response Data;

	public String getBucketName(int b) { return Data.getBucketName(b); }
	public int getNumBuckets() { return Data.getNumBuckets(); }
	
	private class Response
	{
		BucketSummary[] Buckets;

		public int getNumBuckets() { return Buckets.length; }
		public String getBucketName(int b) { return Buckets[b].getName(); }
	}
}
