//===================================================================
// TapeDetails.java
// 	This class holds the tape detailed information.
//===================================================================

package com.socialvagrancy.blackpearl.jsonObject;

public class TapeDetails
{
	private boolean AssignedToStorageDomain;
	private long AvailableRawCapacity;
	private String BarCode;
	private String BucketId;
	private String DescriptionForIdentification;
	private String EjectDate;
	private String EjectLabel;
	private String EjectLocation;
	private String EjectPending;
	private boolean FullOfData;
	private String Id;
	private String LastAccessed;
	private String LastCheckpoint;
	private String LastModified;
	private String LastVerified;
	private String PartiallyVerifiedEndOfTape;
	private String PartitionId;
	private String PreviousState;
	private String SerialNumber;
	private String State;
	private String StorageDomainMemberId;
	private boolean TakeOwnershipPending;
	private long TotalRawCapacity;
	private String Type;
	private boolean VerifyPending;
	private boolean WriteProtected;

	public TapeDetails()
	{
		AssignedToStorageDomain = true;
		AvailableRawCapacity = 0;
		BarCode = "none";
		BucketId = "none";
		DescriptionForIdentification = "none";
		EjectDate = "none";
		EjectLabel = "none";
		EjectLocation = "none";
		EjectPending = "none";
		FullOfData = false;
		Id = "none";
		LastAccessed = "never";
		LastCheckpoint = "never";
		LastModified = "never";
		LastVerified = "never";
		PartiallyVerifiedEndOfTape = "never";
		PartitionId = "none";
		PreviousState = "none";
		SerialNumber = "none";
		State = "none";
		StorageDomainMemberId = "none";
		TakeOwnershipPending = false;
		TotalRawCapacity = 0;
		Type = "none";
		VerifyPending = false;
		WriteProtected = false;
	}

	public boolean getAssignedToStorageDomain() { return AssignedToStorageDomain; }
	public long getAvailableRawCapacity() { return AvailableRawCapacity; }
	public String getBarCode() { return BarCode; }
	public String getBucketId() { return BucketId; }
	public String getDescriptionForIdentification() { return DescriptionForIdentification; }
	public String getEjectDate() { return EjectDate; }
	public String getEjectLabel() { return EjectLabel; }
	public String getEjectLocation() { return EjectLocation; }
	public String getEjectPending() { return EjectPending; }
	public boolean getFullOfData() { return FullOfData; }
	public String getId() { return Id; }
	public String getLastAccessed() { return LastAccessed; }
	public String getLastCheckpoint() { return LastCheckpoint; }
	public String getLastModified() { return LastModified; }
	public String getLastVerified() { return LastVerified; }
	public String getPartiallyVerifiedEndOfTape() { return PartiallyVerifiedEndOfTape; }
	public String getPartitionId() { return PartitionId; }
	public String getPreviousState() { return PreviousState; }
	public String getSerialNumber() { return SerialNumber; }
	public String getState() { return State; }
	public String getStorageDomainMemberId() { return StorageDomainMemberId; }
	public boolean getTakeOwnershipPending() { return TakeOwnershipPending; }
	public long getTotalRawCapacity() { return TotalRawCapacity; }
	public String getType() { return Type; }
	public boolean getVerifyPending() { return VerifyPending; }
	public boolean getWriteProtected() { return WriteProtected; }

}
