package org.opentox.aa.policy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;

public class PolicyArchiveHandler extends PolicyHandler {
	protected OpenSSOPolicy policy;
	protected File backupDir;
	protected File errorDir;
	protected String filePrefix = "policy_";
	protected File identifiers;
	protected FileWriter idwriter;
	
	public PolicyArchiveHandler(OpenSSOPolicy policy, File backupDir) throws IOException {
		super();
		this.policy = policy;
		this.backupDir = backupDir;
		this.errorDir = new File(backupDir,"error.txt");
		if (errorDir.exists()) errorDir.delete();
		identifiers = new File(backupDir,"policyids.txt");
		if (identifiers.exists()) identifiers.delete();
		idwriter = new FileWriter(identifiers);
		
	}
	
	public void close() throws IOException {
		try {
			idwriter.close();
		} finally {
		log("archive",String.format("%d policy identifiers written into %s",getProcessed(),identifiers.getAbsoluteFile()));
		}
	}
	
	public void backupXML(OpenSSOToken ssotoken,OpenSSOPolicy policy) throws Exception {
		FileWriter w=null ;
		try {
			w = new FileWriter(new File(backupDir,"servers.txt"));
			w.write(ssotoken.getAuthService());
			w.write("\n");
			w.write(policy.getPolicyService());
			w.write("\n");
		} catch (Exception x) {
			log("error",x.getMessage());
		} finally {
			try { w.flush();w.close();} catch (Exception x) {}
		}
		processed = 0;
		FileReader idreader = new FileReader(identifiers);
		try {
			BufferedReader reader = new BufferedReader(idreader);
			String line = null;

			while ((line = reader.readLine())!=null) {
				String policyID = line.trim();
				if ("".equals(policyID)) continue;
				long now = System.currentTimeMillis();
				try {
					policy.listPolicy(ssotoken, policyID, this);
					now = System.currentTimeMillis() - now;
					log("archive",String.format("Policy '%s' retrieved and written in [%s ms]",policyID,now));
				} catch (Exception x) {
					log("archive",String.format("ERROR retrieving policy %s",policyID));
				}
			}
		} catch (Exception x) {
			throw x;
		} finally {
			idreader.close();
			log("archive",String.format("%d policy XML files written into %s directory",getProcessed(),
					backupDir.getAbsoluteFile()));
		}
	}
	
	protected void log(String command, String message) {
		System.out.println(String.format("%s> %s",command, message));
	}
		@Override
		public boolean handleOwner(String owner) throws Exception {
			super.handleOwner(owner);
			filePrefix = String.format("policy_%s", owner);
			return true;				
		}				

		@Override
		public boolean handlePolicy(String policyID) throws Exception {
			boolean ok = super.handlePolicy(policyID);
			idwriter.write(policyID);
			idwriter.write("\n");
			if ((getProcessed()%100)==0) idwriter.flush();
			return ok;
		}
		@Override
		public boolean handlePolicy(String policyID, String content)
				throws Exception {
			processed++;
			if (content==null) return true;
			if ("".equals(content.trim())) return true;
			File file = new File(backupDir,String.format("%s_%d.xml", filePrefix,getProcessed()));
			System.out.println(file);
			if (file.exists()) file.delete();
			FileWriter writer = new FileWriter(file);
			writer.write(content);
			writer.flush();
			writer.close();
			return true;
		}
				
		@Override
		public boolean handleError(String policyID, String content, Exception x)
				throws Exception {
			try { if (!errorDir.exists()) this.errorDir.mkdirs(); } catch (Exception xx) {}
			FileWriter writer = new FileWriter(errorDir,true);
			writer.write(policyID);
			writer.close();
			return true;
		}
}
