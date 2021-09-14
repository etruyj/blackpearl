//===================================================================
// TapeObject.java
// 	This class holds the information in returned in the result
// 	field of the get_objects_on_tape command.
//===================================================================

package com.socialvagrancy.blackpearl.jsonObject;

public class TapeObject
{
	private String bucket;
	private String id;
	private boolean inCache;
	private boolean latest;
	private long length;
	private String name;
	private long offset;
	private String versionId;
	private String PhysicalPlacement;

	public TapeObject()
	{
		bucket = "none";
		id = "none";
		inCache = false;
		latest = false;
		length = 0;
		name = "none";
		offset = 0;
		versionId = "0";
		PhysicalPlacement = "none";
	}

	public String getBucket() { return bucket; }
	public String getId() { return id; }
	public boolean isInCache() { return inCache; }
	public boolean isLatest() { return latest; }
	public long getLength() { return length; }
	public String getName() { return name; }
	public long getOffset() { return offset; }
	public String getVersionId() { return versionId; }
	public String getPhysicalPlacement() { return PhysicalPlacement; }

}
