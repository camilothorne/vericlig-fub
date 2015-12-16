package tolearn;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.http.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.codesnippets4all.json.parsers.JSONParser;
import com.codesnippets4all.json.parsers.JsonParserFactory;
 

/**
 * A simple Java REST GET example using the Apache HTTP library
 * to query the TagMe service
 */
public class RestTagMe {
	
	
	// API key
    public static final String KEY = "12340987ADcsWLKio77dc8377b";
    
    
    // redirect stout to file
	private static FileOutputStream fos;
	private static TeeOutputStream myOut;
	private static PrintStream ps;


	/**
	 * tagger method
	 * 
	 * @param inputText
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public static void taggerTagMe(String inputText) throws Exception{
		
		// normalize input
		String norm = inputText.replaceAll(" ", "+");;
		
		// send stout to file
		fos 	= 	new FileOutputStream("/home/camilo/tagme.txt");
		myOut	=	new TeeOutputStream(System.out, fos);
		ps 		= 	new PrintStream(myOut,true,"UTF-8");
		System.setOut(ps);
		
	    DefaultHttpClient httpclient = new DefaultHttpClient();
	    JsonParserFactory factory	=	JsonParserFactory.getInstance();
	    JSONParser parser			=	factory.newJsonParser();
	    
	    try {
	    	
	    	// specify the host, protocol, and port	       
	    	HttpHost target = new HttpHost("tagme.di.unipi.it", 80, "http");	
	    	
	    	// specify the get request
	    	String stringRequest = "/tag?text=" + norm + "&key=" + KEY;
	    	
	    	// create request
	    	HttpGet getRequest = new HttpGet(stringRequest);
	 
	    	System.out.println("----------------------------------------");
	    	System.out.println("executing request to " + target);
	 
	    	// launch request and retrieve response
	    	HttpResponse httpResponse = httpclient.execute(target, getRequest);
	    	HttpEntity entity = httpResponse.getEntity();
	 
	    	// uncomment to print response headers
//	    	System.out.println("----------------------------------------");
//	    	System.out.println(httpResponse.getStatusLine());
//	    	Header[] headers = httpResponse.getAllHeaders();
//	    	for (int i = 0; i < headers.length; i++) {
//	    		System.out.println(headers[i]);
//	    	}
//	    	System.out.println("----------------------------------------");
	    	
	    	// print JSON response object
	    	if (entity != null) {
	    		
	    		String data = EntityUtils.toString(entity);
	    		
	    		// uncomment to print raw JSON response
//	    		System.out.println("JSON: " + data);
	    		
	    	    Map jsonData = parser.parseJson(data);
	    	    ArrayList<Map> annotations =  (ArrayList<Map>)jsonData.get("annotations");
		    	System.out.println("----------------------------------------");
		    	System.out.println("input: " + inputText);
	    	    for (Map map : annotations){
	    	    	System.out.println("----------------------------------------");
	    	    	System.out.println("phrase: " 		+ map.get("spot"));	    	    	
	    	    	System.out.println("sense: " 		+ map.get("title"));
	    	    	System.out.println("confidence: " 	+ map.get("rho"));
	    	    }
		    	System.out.println("----------------------------------------");
	    	    
	    	}
	 
	    } 
	    catch (Exception e) {
	    		e.printStackTrace();
	    } 
	    finally {
	    		// When HttpClient instance is no longer needed,
	    		// shut down the connection manager to ensure
	    		// immediate deallocation of all system resources
	    		httpclient.getConnectionManager().shutdown();
	    }	
		
    }
	
	
	/**
	 * main method
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		// input sentence
		String input = "Strategies for preclinical evaluation of "
				+ "dendritic cell subsets for promotion of transplant tolerance in the nonhuman primate.";
	  
		// call remote tagger
		taggerTagMe(input);
	    
  	}
	
}
