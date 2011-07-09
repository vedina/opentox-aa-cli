package org.opentox.aa.opensso;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Hashtable;

import org.opentox.aa.IOpenToxUser;
import org.opentox.aa.OTAAParams;
import org.opentox.aa.OpenToxToken;
import org.opentox.aa.exception.AAException;
import org.opentox.aa.exception.AALogoutException;
import org.opentox.aa.exception.AATokenValidationException;
import org.opentox.aa.exception.AAUnauthorizedException;
import org.opentox.rest.HTTPClient;
import org.opentox.rest.RestException;

/**
 * http://opentox.org/dev/apis/api-1.2/AA
 * 
 * @author Nina Jeliazkova
 *
 */
public class OpenSSOToken extends OpenToxToken {
	//services
	protected static final String authn = "%s/authenticate?uri=service=openldap";
	protected static final String authz = "%s/authorize";
	protected static final String attributes = "%s/attributes";
	protected static final String token_validation = "%s/isTokenValid";
	protected static final String logout = "%s/logout"; 
	//parsing helpers
	private static final String tokenReceived = "token.id=";
	private static final String boolean_true_result = "boolean=true";
	
	public static final String authz_result_ok = "boolean=true";
	public static final String authz_result_bad = "boolean=false";
	
	public OpenSSOToken(String authService) {
		super(authService);
	}

	@Override
	public boolean login(IOpenToxUser user) throws Exception {
		return login(user.getUsername(),user.getPassword());
	}

	@Override
	public boolean login(String username, String password) throws Exception {
		if (username==null) throw new Exception(MSG_EMPTY_USERNAME,null);
		
		String uri = String.format(authn, authService);
		HTTPClient client = new HTTPClient(uri);

		try {
			client.postWWWForm(new String[][] {
					{OTAAParams.username.toString(), username},
					{OTAAParams.password.toString(),password}
			});
			int status = client.getStatus();
			if (HttpURLConnection.HTTP_OK==status) {
				String text = client.getText().trim();
				this.token = text.substring(text.indexOf(tokenReceived)+9);
				return token != null;
			} else if (HttpURLConnection.HTTP_UNAUTHORIZED ==status) {
				throw new AAUnauthorizedException(uri);
			} else  {
				throw new AAException(client.getStatus(),uri,null);
			}
		} catch (RestException x) {	
			if  (HttpURLConnection.HTTP_UNAUTHORIZED == x.getStatus()) return false;
			else throw x;
		} catch (Exception x) {
			if  (HttpURLConnection.HTTP_UNAUTHORIZED == client.getStatus()) return false;
			else throw x;

		} finally {
			try {client.release();} catch (Exception x) {}
		}
	}

	@Override
	public boolean logout() throws Exception {
		if (token==null) throw new Exception(MSG_EMPTY_TOKEN,null);
		
		String logoutService = String.format(logout, authService);
		HTTPClient client = new HTTPClient(logoutService);

		try {
			client.postWWWForm(new String[][] {
					{OTAAParams.subjectid.toString(),token}	
			});
			int status = client.getStatus();
			if (HttpURLConnection.HTTP_OK == status) {
				setToken(null);
				return true;
			} else  {
				throw new AALogoutException(logoutService,new RestException(client.getStatus(),client.getStatusMessage()));
			}
		} catch (RestException x) {
			if  (HttpURLConnection.HTTP_UNAUTHORIZED == x.getStatus()) return false;
			else throw x;
		} catch (Exception x) {
			if  (HttpURLConnection.HTTP_UNAUTHORIZED == client.getStatus()) return false;
			else throw x;
		} finally {
			try {client.release();} catch (Exception x) {}
		}
	}
	
	@Override
	public boolean isTokenValid() throws Exception {
		if (token==null) throw new Exception(MSG_EMPTY_TOKEN,null);
		
		//Form form = new Form();		form.add(OTAAParams.tokenid.toString(),token);
		String tokenValidationService = String.format(token_validation, authService);
		
		String urlParameters = String.format("%s=%s",
				OTAAParams.tokenid.toString(),
				URLEncoder.encode(token, "UTF-8"));
					
		HttpURLConnection uc = null;
		InputStream in = null;
		int code = -1;
		try {
			URL url = new URL(tokenValidationService);
			uc = (HttpURLConnection) url.openConnection();
			uc.setRequestMethod("POST");
			uc.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			uc.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			uc.setUseCaches (false);
		    uc.setDoInput(true);
		    uc.setDoOutput(true);
		     //Send request
		    DataOutputStream wr = new DataOutputStream (uc.getOutputStream ());
		    wr.writeBytes (urlParameters);
		    wr.flush ();
		    wr.close ();		    
		  //Get Response	
		    code = uc.getResponseCode();
		    if  (HttpURLConnection.HTTP_UNAUTHORIZED == code) return false;
		    in = uc.getInputStream();
		    BufferedReader reader = new BufferedReader(new InputStreamReader(in)); 
		    String line;
		    boolean ok = false;
		    while((line = reader.readLine()) != null) {
		    	ok = line.indexOf(boolean_true_result)>=0;
		    	if (ok) break;
		    }
		    return ok;
		} catch (Exception x) {
			if  (HttpURLConnection.HTTP_UNAUTHORIZED == code) return false;
			else throw new AATokenValidationException(tokenValidationService,x);
		} finally {
			try {in.close();} catch (Exception x) {}
			try {uc.disconnect();} catch (Exception x) {}
		}
	}

	@Override
	public boolean authorize(String uri, String action) throws Exception {
		if (token==null) throw new Exception(MSG_EMPTY_TOKEN,null);
		String[][] form = new String[][] {		
			{OTAAParams.subjectid.toString(),token}, //Reference.encode(token));
			{OTAAParams.uri.toString(),uri},
			{OTAAParams.action.toString(),action}
		};
		String authorizationService = String.format(authz, authService);
		
		HTTPClient client = new HTTPClient(authorizationService);
		
		try {
			client.postWWWForm(form);
			String content = client.getText();
			if (HttpURLConnection.HTTP_OK == client.getStatus()) {
				return (content!=null) && authz_result_ok.equals(content.trim());
			} else if (HttpURLConnection.HTTP_UNAUTHORIZED == client.getStatus()) {
				return false;
			} else  {
				throw new AAException(client.getStatus(),authz,null);
			}
		} catch (RestException x) {
			if  (HttpURLConnection.HTTP_UNAUTHORIZED == client.getStatus()) return false;
			else throw new RestException(HttpURLConnection.HTTP_BAD_GATEWAY,
					String.format("%s POST uri=%s subjectid=%s %s %s",
							authorizationService,uri,token,x.getMessage(),x.getStatus()),x);
		} finally {
			try {client.release();} catch (Exception x) {}
		}
	}
	
	/**
	 * http://developers.sun.com/identity/reference/techart/id-svcs.html
	 */
	@Override
	public boolean getAttributes(String[] attributeNames,Hashtable<String, String> results)
			throws Exception {
		if (token==null) throw new Exception(MSG_EMPTY_TOKEN,null);
		
		StringBuilder attrService = new StringBuilder();
		attrService.append(String.format(attributes, authService));
		
		attrService.append(String.format("?%s=%s",OTAAParams.subjectid.toString(), URLEncoder.encode(token,"UTF-8")));
		if (attributeNames!=null)
			for (String aName : attributeNames) 
				attrService.append(String.format("&%s=%s",OTAAParams.attributes_names.toString(), URLEncoder.encode(aName,"UTF-8")));
			
		URL url = null;
    	try {
    		url = new URL(attrService.toString());
    	} catch (MalformedURLException x) {
    		throw x;
    	}
    	HttpURLConnection uc = null;
		InputStream in = null;
		int code = -1;
		try {
			uc = (HttpURLConnection) url.openConnection();
			uc.setDoOutput(true);
			uc.setRequestMethod("GET"); 
			code = uc.getResponseCode();
			if (HttpURLConnection.HTTP_OK == code) {
				in = uc.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				String line = null;
				String name_tag = "userdetails.attribute.name";
				String value_tag = "userdetails.attribute.value";
				String name = null;
				String value = null;
				while ((line = reader.readLine())!=null) {
					if (line.startsWith(name_tag)) {
						name = line.substring(name_tag.length()+1);
					} else if ((name!=null) && line.startsWith(value_tag)) {
						value = line.substring(value_tag.length()+1);
						if (results != null) results.put(name,value);
					}
				}
				return true;
			} else return false;
		} catch (Exception x) {
			if (HttpURLConnection.HTTP_UNAUTHORIZED == code) return false;
			else throw x;
		} finally {
			try {in.close();} catch (Exception x) {}
			try {uc.disconnect();} catch (Exception x) {}
		}
		
	}
}
