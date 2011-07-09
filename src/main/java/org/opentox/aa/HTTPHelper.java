package org.opentox.aa;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class HTTPHelper {
	protected HttpURLConnection connection;
	protected static final String POST = "POST";
	protected static final String mime_wwwform = "application/x-www-form-urlencoded";
	
	public void postWWWForm(String targetURL, String[][] form) throws Exception {
		StringBuilder urlParameters = new StringBuilder();
		String delimiter  ="";
		for (String[] params: form) {
			if (params.length<2) continue;
			urlParameters.append(delimiter);
			urlParameters.append(String.format("%s=%s",params[0], URLEncoder.encode(params[1], "UTF-8")));
			delimiter = "&";
		}
		postWWWForm(targetURL, urlParameters.toString(),mime_wwwform);
	}
	
	public int getStatus() throws IOException {
		return connection==null?-1:connection.getResponseCode();
	}
	public String getStatusMessage() throws IOException {
		return connection==null?null:connection.getResponseMessage();
	}	
	public InputStream getInputStream() throws IOException {
		return connection==null?null:connection.getInputStream();
	}
	
	public String getText() throws IOException {

	      //Get Response	
	      InputStream is = connection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	      String line;
	      StringBuffer response = new StringBuffer(); 
	      while((line = rd.readLine()) != null) {
	        response.append(line);
	        response.append("\r\n");
	      }
	      rd.close();
	      return response.toString();

	}		
	/**
	 * 
	 * @param targetURL
	 * @param urlParameters  URL encoded string
	 * @return
	 * @throws Exception
	 */
	public void postWWWForm(String targetURL, String urlParameters, String mime) throws Exception {
	    URL url;
	    HttpURLConnection connection = null;  
	    try {
	    
	      url = new URL(targetURL);
	      connection = (HttpURLConnection)url.openConnection();
	      connection.setRequestMethod(POST);
	      connection.setRequestProperty("Content-Type",mime);
				
	      connection.setRequestProperty("Content-Length", "" + 
	               Integer.toString(urlParameters.getBytes().length));
	      connection.setRequestProperty("Content-Language", "en-US");  
				
	      connection.setUseCaches (false);
	      connection.setDoInput(true);
	      connection.setDoOutput(true);

	      //Send request
	      DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
	      wr.writeBytes (urlParameters);
	      wr.flush ();
	      wr.close ();
	      

	    } catch (Exception e) {

	      throw e;

	    } finally {
	    
	    }
	  }
	
	protected void close() {
		if (connection != null) try {
			if (connection.getInputStream()!=null)
				connection.getInputStream().close();
			connection.disconnect();			
		} catch (Exception x) {
			
		}
	}
}
