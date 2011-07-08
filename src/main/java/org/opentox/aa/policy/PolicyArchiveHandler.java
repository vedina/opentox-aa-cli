package org.opentox.aa.policy;

import java.io.File;
import java.io.FileWriter;

import org.opentox.aa.opensso.OpenSSOPolicy;

public class PolicyArchiveHandler extends PolicyHandler {
	protected OpenSSOPolicy policy;
	protected File backupDir;
	protected String filePrefix = "policy_";
	
	public PolicyArchiveHandler(OpenSSOPolicy policy, File backupDir) {
		super();
		this.policy = policy;
		this.backupDir = backupDir;
	}
		@Override
		public void handleOwner(String owner) throws Exception {
			super.handleOwner(owner);
			filePrefix = String.format("policy_%s", owner);
				
		}				

		@Override
		public void handlePolicy(String policyID, String content)
				throws Exception {
			if (content==null) return;
			if ("".equals(content.trim())) return;
			File file = new File(backupDir,String.format("%s_%d.xml", filePrefix,getProcessed()));
			System.out.println(file);
			if (file.exists()) file.delete();
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.flush();
			writer.close();
		}
				
}
