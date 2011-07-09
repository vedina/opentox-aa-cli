package org.opentox.aa.policy;

public interface IPolicyHandler {
	int getProcessed();
	boolean handlePolicy(String policyID) throws Exception ;
	boolean handlePolicy(String policyID,String content) throws Exception ;
	boolean handleOwner(String owner) throws Exception;
	boolean handleError(String policyID, String content, Exception x) throws Exception ;
}
