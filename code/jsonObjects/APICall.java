//===================================================================
// APICall.java
// 	This holds the outline of the APICall Info. To be filled in
// 	with extensions for the specific call.
//===================================================================

package com.socialvagrancy.blackpearl.jsonObject;

public class APICall
{
	Metadata Meta;
	String Status;
	String Message;

	public String getAPICallDate() { return Meta.getDate(); }
	public String getAPICallMessage() { return Message; }
	public String getAPICallStatus() { return Status; }
	public String getAPICallTime() { return Meta.getTime(); }

}
