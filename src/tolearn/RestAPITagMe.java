package tolearn;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
//import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * CLI RESTFul client to TagMe
 * 
 * @author Camilo Thorne
 *
 */
public class RestAPITagMe {
 
	
	// fields
    private static final String TAGME_URL = "http://tagme.di.unipi.it/tag";
    public static String uniqueAccessKey = "12340987ADcsWLKio77dc8377b";		
    private String input;
    private File output;
    private HttpClient client;
 
    
    // POST method
    private PostMethod createPostMethod() {
    	
        PostMethod method = new PostMethod(TAGME_URL);
        
        // Set mandatory parameters
        method.setRequestHeader("X-AG-Access-Token", uniqueAccessKey);
        
        // Set input content type
        method.setRequestHeader("Content-Type", "text/raw");
        
        // Set response/output format
        method.setRequestHeader("outputformat", "application/json");
        return method;
        
    }
 
    
    // run the client
    private void run() {
    	
    	try {
    		
//            if (input.isFile()) {
//                postFile(input, createPostMethod());
//            } else if (input.isDirectory()) {
//                System.out.println("working on all files in " + input.getAbsolutePath());
//                for (File file : input.listFiles()) {
//                    if (file.isFile())
//                        postFile(file, createPostMethod());
//                    else
//                        System.out.println("skipping "+file.getAbsolutePath());
//                }
//            }
    		
    		// send CLI input to POST method 
    		postInput(input, createPostMethod());
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    }
 
    
    // launch query (POST)
    private void doRequest(String text, PostMethod method) {
    	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date date = new Date();
        try {
            int returnCode = client.executeMethod(method);
            if (returnCode == HttpStatus.SC_NOT_IMPLEMENTED) {
                System.err.println("The Post method is not implemented by this URI");
                // still consume the response body
                method.getResponseBodyAsString();
            } else if (returnCode == HttpStatus.SC_OK) {
                System.out.println("File post succeeded: " + text);
                saveResponse("tagme" + dateFormat.format(date), method);
            } else {
                System.err.println("File post failed: " + text);
                System.err.println("Got code: " + returnCode);
                System.err.println("response: " + method.getResponseBodyAsString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
    }
 
    
    // save response (POST)
    private void saveResponse(String name, PostMethod method) throws IOException {
        PrintWriter writer = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    method.getResponseBodyAsStream(), "UTF-8"));
            File out = new File(output, name + ".xml");
            writer = new PrintWriter(new BufferedWriter(new FileWriter(out)));
            String line;
            while ((line = reader.readLine()) != null) {
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) try {writer.close();} catch (Exception ignored) {}
        }
    }
 
    
    // configure query (POST)
    @SuppressWarnings("deprecation")
	private void postInput(String text, PostMethod method) throws IOException {
        method.setRequestEntity(new StringRequestEntity(text));
        doRequest(text, method); // launch
    }
 
    
    // CLI main
    public static void main(String[] args) {
        verifyArgs(args);
        RestAPITagMe httpClientPost = new RestAPITagMe();
        httpClientPost.input = args[0];
        httpClientPost.output = new File(args[1]);
        httpClientPost.client = new HttpClient();
        httpClientPost.client.getParams().setParameter("http.useragent", "TagMe Rest Client");
        httpClientPost.run();
    }
 
    
    // verify args in CLI
    private static void verifyArgs(String[] args) {
        if (args.length==0) {
            usageError("no params supplied");
        } else if (args.length < 3) {
            usageError("3 params are required");
        } else {
            //if (!new File(args[0]).exists())
            //    usageError("file " + args[0] + " doesn't exist");
            File outdir = new File(args[1]);
            if (!outdir.exists() && !outdir.mkdirs())
                usageError("couldn't create output dir");
        }
        uniqueAccessKey = args[2];
    }
 
    
    // CLI help
    private static void usageError(String s) {
        System.err.println(s);
        System.err.println("Usage: java " + (new Object() { }.getClass().getEnclosingClass()).getName() + " input output_dir");
        System.exit(-1);
    }
}
