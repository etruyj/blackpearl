//===================================================================
// BucketDetails.java
// 	This houses more information than the Bucket Class. The Bucket
// 	class is just the summary of the bucket, creation date + name.
// 	This includes more information on the specified bucket.
//===================================================================

package com.socialvagrancy.blackpearl.jsonObject;

public class BucketDetails extends BucketSummary
{
	String DataPolicyId;
	boolean Empty;
	String Id;
	long LastPreferedChunkSizeInBytes;
	long LogicalUsedCapacity;
	String UserId;

	public String getBucketID() { return Id; }
	public String getDataPolicyID() { return DataPolicyId; }
	public boolean getIsEmpty() { return Empty; }
	public long getLastPreferedChunkSizeInBytes() { return LastPreferedChunkSizeInBytes; }
	public long getLogicalUsedCapacity() { return LogicalUsedCapacity; }
	public String getOwnerID() { return UserId; }

}
