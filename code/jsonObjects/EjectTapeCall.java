//===================================================================
// EjectTapeCall.java
// 	This class holds the json results to the ds3_java_cli 
// 	eject_tape command..
//===================================================================

package com.socialvagrancy.blackpearl.jsonObject;

public class EjectTapeCall extends APICall
{
	private Details Data;
	
	public EjectTapeCall()
	{
		Data = new Details();
	}

	public boolean getAssignedToStorageDomain() { return Data.getAssignedToStorageDomain(); }
	public long getAvailableRawCapacity() { return Data.getAvailableRawCapacity(); }
	public String getBarCode() { return Data.getBarCode(); }
	public String getBucketId() { return Data.getBucketId(); }
	public String getDescriptionForIdentification() { return Data.getDescriptionForIdentification(); }
	public String getEjectDate() { return Data.getEjectDate(); }
	public String getEjectLabel() { return Data.getEjectLabel(); }
	public String getEjectLocation() { return Data.getEjectLocation(); }
	public String getEjectPending() { return Data.getEjectPending(); }
	public boolean getFullOfData() { return Data.getFullOfData(); }
	public String getId() { return Data.getId(); }
	public String getLastAccessed() { return Data.getLastAccessed(); }
	public String getLastCheckpoint() { return Data.getLastCheckpoint(); }
	public String getLastModified() { return Data.getLastModified(); }
	public String getLastVerified() { return Data.getLastVerified(); }
	public String getPartiallyVerifiedEndOfTape() { return Data.getPartiallyVerifiedEndOfTape(); }
	public String getPartitionId() { return Data.getPartitionId(); }
	public String getPreviousState() { return Data.getPreviousState(); }
	public String getSerialNumber() { return Data.getSerialNumber(); }
	public String getState() { return Data.getState(); }
	public String getStorageDomainMemberId() { return Data.getStorageDomainMemberId(); }
	public boolean getTakeOwnershipPending() { return Data.getTakeOwnershipPending(); }
	public long getTotalRawCapacity() { return Data.getTotalRawCapacity(); }
	public String getType() { return Data.getType(); }
	public boolean getVerifyPending() { return Data.getVerifyPending(); }
	public boolean getWriteProtected() { return Data.getWriteProtected(); }

	private class Details
	{
		private TapeDetails result;

		private Details()
		{
			result = new TapeDetails();
		}

		public boolean getAssignedToStorageDomain() { return result.getAssignedToStorageDomain(); }
		public long getAvailableRawCapacity() { return result.getAvailableRawCapacity(); }
		public String getBarCode() { return result.getBarCode(); }
		public String getBucketId() { return result.getBucketId(); }
		public String getDescriptionForIdentification() { return result.getDescriptionForIdentification(); }
		public String getEjectDate() { return result.getEjectDate(); }
		public String getEjectLabel() { return result.getEjectLabel(); }
		public String getEjectLocation() { return result.getEjectLocation(); }
		public String getEjectPending() { return result.getEjectPending(); }
		public boolean getFullOfData() { return result.getFullOfData(); }
		public String getId() { return result.getId(); }
		public String getLastAccessed() { return result.getLastAccessed(); }
		public String getLastCheckpoint() { return result.getLastCheckpoint(); }
		public String getLastModified() { return result.getLastModified(); }
		public String getLastVerified() { return result.getLastVerified(); }
		public String getPartiallyVerifiedEndOfTape() { return result.getPartiallyVerifiedEndOfTape(); }
		public String getPartitionId() { return result.getPartitionId(); }
		public String getPreviousState() { return result.getPreviousState(); }
		public String getSerialNumber() { return result.getSerialNumber(); }
		public String getState() { return result.getState(); }
		public String getStorageDomainMemberId() { return result.getStorageDomainMemberId(); }
		public boolean getTakeOwnershipPending() { return result.getTakeOwnershipPending(); }
		public long getTotalRawCapacity() { return result.getTotalRawCapacity(); }
		public String getType() { return result.getType(); }
		public boolean getVerifyPending() { return result.getVerifyPending(); }
		public boolean getWriteProtected() { return result.getWriteProtected(); }
	}
}
