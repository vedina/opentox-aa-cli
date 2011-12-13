package org.opentox.rest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class HTTPClient {
	protected String[][] headers;
	protected String targetURL;
	protected HttpURLConnection connection;
	public static final String POST = "POST";
	public static final String GET = "GET";
	public static final String DELETE = "DELETE";
	public static final String PUT = "PUT";
	public static final String mime_wwwform = "application/x-www-form-urlencoded";
	
	public HTTPClient(String uri) {
		super();
		this.targetURL = uri;
	}
	public void postWWWForm(String[][] form) throws RestException, UnsupportedEncodingException {
		postWWWForm(targetURL, form);
	}
	protected void postWWWForm(String targetURL, String[][] form) throws RestException , UnsupportedEncodingException {
		
		post(targetURL, getForm(form),mime_wwwform);
	}
	public static String getForm(String[][] form) throws RestException,UnsupportedEncodingException {
		StringBuilder urlParameters = new StringBuilder();
		String delimiter  ="";
		for (String[] params: form) {
			if (params.length<2) continue;
			urlParameters.append(delimiter);
			urlParameters.append(String.format("%s=%s",params[0], params[1]==null?"":URLEncoder.encode(params[1], "UTF-8")));
			delimiter = "&";
		}
		return urlParameters.toString();
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
	public void put(String body, String mime) throws RestException {
		put(targetURL,body,mime);
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
		send(method, targetURL, body, mime, "UTF-8");
	}
	protected void send(String method, String targetURL, String body, String mime,String charset) throws RestException {

	    URL url;
	    if (connection !=null) try { connection.disconnect();} catch (Exception x) {} 
	    	
	    connection = null;  
	    int code = -1;
	    String msg = "";
	    try {
	    
	      url = new URL(targetURL);
	      connection = (HttpURLConnection)url.openConnection();
	      connection.setRequestMethod(method);

	      if (mime != null) {
	    	  String contentType = String.format("%s%s%s",mime,charset==null?"":";charset=",charset==null?"":charset); 
	    	  connection.setRequestProperty("Content-Type",contentType);
	      } 
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
	
	public static synchronized HttpURLConnection getHttpURLConnection(String uri, String method, String mediaType) throws IOException, MalformedURLException {
    	URL url = null;
    	
    	try {
    		url = new URL(uri);
    	} catch (MalformedURLException x) {
    		throw x;
    	}		
    	HttpURLConnection uc = (HttpURLConnection) url.openConnection();
		uc.addRequestProperty("Accept",mediaType);
		uc.setDoOutput(true);
		uc.setRequestMethod(method);    	
		//IAuthToken tokenFactory = ClientResourceWrapper.getTokenFactory();
		//String token = tokenFactory==null?null:tokenFactory.getToken();
		//if (token!=null) uc.addRequestProperty("subjectid", token);
		return uc;
	}	
	
	public static void download(InputStream in, File file) throws IOException  {
		File dir = file.getParentFile();
		if ((dir!=null) && !dir.exists())	dir.mkdirs();		
    	FileOutputStream out = new FileOutputStream(file);		
    	try {
			download(in, out);
    	} catch (IOException x) {
    		throw new IOException(x.getMessage());
    	} finally {
			out.close();
    	}
    }
	
	public static void download(InputStream in, OutputStream out) throws IOException {
		byte[] bytes = new byte[512];
		int len;
		long count = 0;
		while ((len = in.read(bytes, 0, bytes.length)) != -1) {
			out.write(bytes, 0, len);
			count += len;
		}
		out.flush();
}		
}
