package org.opentox.aa.policy;

public class PolicyHandler implements IPolicyHandler {
	protected int processed = 0;
	@Override
	public int getProcessed() {
		return processed;
	}

	@Override
	public boolean handlePolicy(String policyID) throws Exception {
		processed++;
		return true;
	}

	@Override
	public boolean handlePolicy(String policyID, String content) throws Exception {
		processed++;
		return true;
	}

	@Override
	public boolean handleOwner(String owner) throws Exception {
		return true;
	}

	@Override
	public boolean handleError(String policyID, String content, Exception x) throws Exception {
		throw x;
	}
}
