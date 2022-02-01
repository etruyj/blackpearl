//===================================================================
// ObjectDetails.java
// 	This holds all the object information returned from the ds3
// 	java cli get_bucket api call.
//===================================================================

package com.socialvagrancy.blackpearl.structures;

public class ObjectDetails
{
	String etag;
	String Etag; // Duplicate of above?
	boolean IsLatest;
	String Key; // Also known as object name;
	String LastModified;
	UserSummary Owner;
	long Size;
	String StorageClass;
	String VersionId;

	public String getetag() { return etag; }
	public String getEtag() { return Etag; }
	public boolean getIsLatest() { return IsLatest; }
	public String getObjectKey() { return Key; }
	
	public String getLastModifiedDate()
	{
		String[] info = LastModified.split("T");
		return info[0];
	}

	public String getLastModifiedTime()
	{
		String[] info = LastModified.split("T");
		info = info[1].split(".");
		return info[0];
	}

	public String getOwnerName() { return Owner.getDisplayName(); }
	public String getOwnerID() { return Owner.getUserID(); }
	public long getSize() { return Size; }
	public String getStorageClass() { return StorageClass; }
	public String getVersionID() { return VersionId; }

}
