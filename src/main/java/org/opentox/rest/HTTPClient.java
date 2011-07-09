package org.opentox.rest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class HTTPClient {
	protected String[][] headers;
	protected String targetURL;
	protected HttpURLConnection connection;
	protected static final String POST = "POST";
	protected static final String GET = "GET";
	protected static final String DELETE = "DELETE";
	protected static final String PUT = "PUT";
	protected static final String mime_wwwform = "application/x-www-form-urlencoded";
	
	public HTTPClient(String uri) {
		super();
		this.targetURL = uri;
	}
	public void postWWWForm(String[][] form) throws RestException, UnsupportedEncodingException {
		postWWWForm(targetURL, form);
	}
	protected void postWWWForm(String targetURL, String[][] form) throws RestException , UnsupportedEncodingException {
		StringBuilder urlParameters = new StringBuilder();
		String delimiter  ="";
		for (String[] params: form) {
			if (params.length<2) continue;
			urlParameters.append(delimiter);
			urlParameters.append(String.format("%s=%s",params[0], URLEncoder.encode(params[1], "UTF-8")));
			delimiter = "&";
		}
		post(targetURL, urlParameters.toString(),mime_wwwform);
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
	public void post(String body, String mime) throws RestException {
		post(targetURL,body,mime);
	}
	public void delete() throws RestException {
		send(DELETE, targetURL, null, null);
	}	
	public void get() throws RestException {
		send(GET, targetURL, null,null);
	}		
	protected void post(String targetURL, String body, String mime) throws RestException {
		send(POST, targetURL, body, mime);
	}
	protected void put(String targetURL, String body, String mime) throws RestException {
		send(PUT, targetURL, body, mime);
	}	
	/**
	 * 
	 * @param targetURL
	 * @param urlParameters  URL encoded string
	 * @return
	 * @throws Exception
	 */
	protected void send(String method, String targetURL, String body, String mime) throws RestException {

	    URL url;
	    if (connection !=null) try { connection.disconnect();} catch (Exception x) {} 
	    	
	    connection = null;  
	    int code = -1;
	    String msg = "";
	    try {
	    
	      url = new URL(targetURL);
	      connection = (HttpURLConnection)url.openConnection();
	      connection.setRequestMethod(method);

	      if (mime != null)
	    	  connection.setRequestProperty("Content-Type",mime);
	      
		  if (body!=null)	
			  connection.setRequestProperty("Content-Length", "" +  Integer.toString(body.getBytes().length));
	     // connection.setRequestProperty("Content-Language", "en-US");  
	      if (headers!=null)
	      for (String[] header:headers) 
	    	  if ((header!=null) && header.length>=2) 
	    		  connection.addRequestProperty(header[0],header[1]); 
	      
	      connection.setUseCaches (false);
	      if (!GET.equals(method)) 
	    	  connection.setDoInput(true);
	      
	      if (body != null) {
		      connection.setDoOutput(true);
		      	  
		      //Send request
		      DataOutputStream wr = new DataOutputStream (connection.getOutputStream ());
		      wr.writeBytes (body==null?"":body);
		      wr.flush ();
		      wr.close ();
		      
		      code = getStatus();
		      msg = getStatusMessage();
	      } else 
	    	  connection.connect();
	      
	      code = getStatus();
	      msg = getStatusMessage();
	    } catch (IOException e) {
	      
	      throw new RestException(code,msg,e);
  
	    } finally {
	    
	    }
	  }
	
	public void setHeaders(String[][] headers) {
		this.headers = headers;
	}
	
	public void release() {
		if (connection != null) {
			try {if (connection.getInputStream()!=null)	connection.getInputStream().close();} catch (Exception x) {}
			try { connection.disconnect();} catch (Exception x) {}
		}
		connection = null;
	}
}
