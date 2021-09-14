//===================================================================
// ObjectsCall.java
// 	This class holds the structure for the translated get_buckets
// 	ds3 java cli call.
//===================================================================

package com.socialvagrancy.blackpearl.jsonObject;

public class ObjectsCall extends APICall
{
	Response Data;

	public int getNumObjects() { return Data.getNumObjects(); }
	public String getObjectName(int o) { return Data.getObjectName(o); }


	private class Response
	{
		BucketDetails bucket;
		ObjectDetails[] result;
	
		public int getNumObjects() { return result.length; }
		public String getObjectName(int o) { return result[o].getObjectKey(); }
	}
}
