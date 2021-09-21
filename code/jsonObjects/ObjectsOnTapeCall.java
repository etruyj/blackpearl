//===================================================================
// ObjectsOnTapeCall.java
// 	This class holds the json response from the get_objects_on_tape
// 	ds3_java_cli call.
//===================================================================

package com.socialvagrancy.blackpearl.jsonObject;

public class ObjectsOnTapeCall extends APICall
{
	private TapeInfo Data;

	public String getTapeBarcode() { return Data.getBarcode(); }
	public String getObjectBucket(int i) { return Data.getObjectBucket(i); }
	public int getObjectCount() { return Data.getObjectCount(); }
	public String getObjectId(int i) { return Data.getObjectId(i); }
	public boolean getObjectInCache(int i) { return Data.isObjectInCache(i); }
	public boolean getObjectIsLatest(int i) { return Data.isObjectLatest(i); }
	public long getObjectLength(int i) { return Data.getObjectLength(i); }
	public String getObjectName(int i) { return Data.getObjectName(i); }
	public long getObjectOffset(int i) { return Data.getObjectOffset(i); }
	public String getObjectVersionId(int i) { return Data.getObjectVersionId(i); }
	public String getObjectPhysicalPlacement(int i) { return Data.getObjectPlacement(i); }

	public class TapeInfo
	{
		private String tapeId;
		private TapeObject[] result;

		private String getBarcode() { return tapeId; }
		private String getObjectBucket(int i)
		{
			if(i<result.length)
			{
				return result[i].getBucket();
			}
			else
			{
				return "Invalid index selected";
			}
		}
		private int getObjectCount() { return result.length; }
		private String getObjectId(int i)
		{
			if(i<result.length)
			{
				return result[i].getId();
			}
			else
			{
				return "Invalid index selected";
			}
		}
		private boolean isObjectInCache(int i)
		{
			if(i<result.length)
			{
				return result[i].isInCache();
			}
			else
			{
				return false;
			}
		}
		private boolean isObjectLatest(int i)
		{
			if(i<result.length)
			{
				return result[i].isLatest();
			}
			else
			{
				return false;
			}
		}
		private long getObjectLength(int i)
		{
			if(i<result.length)
			{
				return result[i].getLength();
			}
			else
			{
				return 0;
			}
		}
		private String getObjectName(int i)
		{
			if(i<result.length)
			{
				return result[i].getName();
			}
			else
			{
				return "Invalid index selected";
			}
		
		}
		private long getObjectOffset(int i)
		{
			if(i<result.length)
			{
				return result[i].getOffset();
			}
			else
			{
				return 0;
			}
		}
		private String getObjectVersionId(int i)
		{
			if(i<result.length)
			{
				return result[i].getVersionId();
			}
			else
			{
				return "Invalid index selected";
			}
		}
		private String getObjectPlacement(int i)
		{
			if(i<result.length)
			{
				return result[i].getPhysicalPlacement();
			}
			else
			{
				return "none";
			}
		}
		
	}
}
