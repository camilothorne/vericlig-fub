package corpora;


// java packages
import edu.stanford.nlp.ling.CoreLabel;
import gov.nih.nlm.nls.metamap.MetaMapApi;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import mymetamap.DepenParse;
import mymetamap.GuidAnn;
import mymetamap.MyFeature;
import mymetamap.myNounPhrase;
import mymetamap.mySentence;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


// my packages
import rw.Append;


public class ReadXMLFile implements MyFeature{
	
		
	// path of file annotated corpus
	//public static final String sample = "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/test.xml";
	
	
	// MetaMap server host
	private static final String serverhost = MetaMapApi.DEFAULT_SERVER_HOST; // default host
	// MetaMap server port
	private static final int serverport = MetaMapApi.DEFAULT_SERVER_PORT; 	// default port
	// MetaMap timeout
	private static int timeout = -1; // use default timeout
	// MetaMap
	private static GuidAnn ann;
	
	
	// file to annotate
	public String sample;
	
	
	// to store sentence annotations
	public ArrayList<mySentence> sentences;
	// path of file with the stripped-off corpus
	public String mypath;
	// path of file with the stripped-off raw corpus
	public String sents;	
	
	
	// constructor
	public ReadXMLFile(String sample) throws Exception{
		long startTime = System.currentTimeMillis(); // init time of FExt procedure
		this.sample = sample;
		// we initialize the MetaMap annotator
		ann = new GuidAnn(serverhost, serverport, "");
		// we init the sentences
		this.sentences = new ArrayList<mySentence>();
		// we harvest the annotations
		mineNPs(ann);
		// we print the corpus
		//printAnnotations();
		if (timeout > -1) {
			ann.setTimeout(timeout);
		}
		long stopTime = System.currentTimeMillis(); // end time of FExt procedure
		System.out.println("Time elapsed = " + (stopTime - startTime)); // print diff
	}
	
		
	//---------------------------
	// A. Auxiliary methods
	//---------------------------
	
	
	// looping recursively over the XML tree structure
	// to collect *all* the descendants of a XML node n
	public static List<Node> getAllDescendants(Node n){
		List<Node> res = new ArrayList<Node>();
		if (n.getChildNodes()==null){
			res.add(n);
			return res;
		}
		else{
			for (int temp = 0; temp < n.getChildNodes().getLength(); temp++) {
				Node m = n.getChildNodes().item(temp);
				res.addAll(getAllDescendants(m));
				res.add(m);
				}
			return res;
		}
	}
	
	
	// checking for sentences with
	// no children and no annotations
	public static Boolean checkEmpty(Node n){
		if (n.getChildNodes().getLength()>0){
			return false;
		}
		else{
			return true;
		}
	}
	
	
	// saving result to file
	public static void saveResult(String res, String path){
		new Append(path,res);
	}

	
	// getting the date
	public static String getMyDate(){
		String mydate = "";
		Date tm = new Date();
		mydate = tm.toString();
		return mydate;
	}
	

	//---------------------------
	// B. Feature extraction
	//---------------------------


	// method harvesting the annotations
	public void mineNPs(GuidAnn ann) {			
		// read the file
	    try {
	    	// read XML file
	    	File fXmlFile = new File(this.sample);
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(fXmlFile);	 
	    	// optional, but recommended
	    	doc.getDocumentElement().normalize();
	    	// to count sentences that *are* annotated
    		int count = 0;
    		// to measure size
    		int size = 0;
    		// set MetaMap options
			List<String> myoptions = new ArrayList<String>();
			myoptions.add(ann.optssim);	  
			myoptions.add(ann.opts);	
    		// get sentences
	    	NodeList nList = doc.getElementsByTagName("Sentence");
	    	// scan sentences
	    	for (int temp = 0; temp < nList.getLength(); temp++) {
	    		Node nNode = nList.item(temp);
    			// if the node is not empty (has annotations), we
    			// proceed with the processing
	    		if (!checkEmpty(nNode)){
	    			// get sentence
	    			String sen = ((Element) nNode).getAttribute("text");
	    			// call tokenizer
	    			List<CoreLabel> tokens = ann.myTokenize(sen);// call tokenizer
	    			// update size
	    			size = size + tokens.size();
	    			// set/call parser
	    			ann.parse = ann.lp.apply(tokens); // set/call parser	
	    			// call dependency parser
	    			ann.gf = ann.gsf.newGrammaticalStructure(ann.parse); // call dependency parser			   
	    			// transform trees into dependency lists
	    			ann.tdl = ann.gf.typedDependenciesCCprocessed();
	    			// init sentence
	    			mySentence mysen 	= new mySentence();
	    			mysen.number 		= count;
	    			mysen.sent 			= sen;
	    			mysen.noun_phrases 	= new ArrayList<myNounPhrase>();
	    			mysen.sentokens 	= tokens;
	    			mysen.parsing		= ann.parse;
	    			mysen.sendeps		= ann.tdl;
	    			// explore the sentence annotations
	    			List<Node> desc = getAllDescendants(nNode);	    			
    				// sense of subject NP
    				String sense1 = "";
    				// subject NP yield (text)
    				String nptext1 = "";
    				// sense of object NP
    				String sense2 = "";
    				// object NP yield (text)
    				String nptext2 = "";
    				// relation/event in which NP participates
    				String rel_event = "";
	    			for (Node n : desc){
	    				// recover relation
	    				//if (n.getNodeName() == "Predicate" && n.getNextSibling().getNodeName() == "Subject"){
	    				if (n.getNodeName() == "Predicate"){
	    					Element eElement = (Element) n;    					
	    					rel_event = eElement.getAttribute("type");	    					
	    				}
	    				// recover sense of subject
	    				if (n.getNodeName() == "RelationSemanticType" && n.getParentNode().getNodeName() == "Subject"){
	    					Element eElement = (Element) n;
	    					sense1 = eElement.getTextContent(); 
	    				}
	    				// recover subject text
	    				if (n.getNodeName() == "Subject"){
	    					Element eElement = (Element) n;    					
	    					nptext1 = eElement.getAttribute("text");
	    				}	    				
    					// recover sense of object
	    				if (n.getNodeName() == "RelationSemanticType" && n.getParentNode().getNodeName() == "Object"){
	    					Element eElement = (Element) n;    					
	    					sense2 = eElement.getTextContent();    	    			
	    				}	    				
	    				// recover object text
	    				if (n.getNodeName() == "Object"){
	    					Element eElement = (Element) n;    					
	    					nptext2 = eElement.getAttribute("text");
		    			}
	    			}	    			
	    			// harvest NP
	    			ann.mineSelTree(ann.parse, mysen, myoptions, nptext1, sense1, rel_event,0);	    			
    				//
	    			// harvest NP
	    			ann.mineSelTree(ann.parse, mysen, myoptions, nptext2, sense2, rel_event, 1);    			
	    			//
	    			// we augment the counter
	    			count = count + 1;
	    			// we set the sentence labels
	    			mysen.senlabels = ann.mySenLabels(mysen);
	    			// we save the sentence with its two NPs
	    			System.out.println("SEN " + mysen.number + " : " + mysen.sent);
	    			System.out.println("===============================================");
	    			this.sentences.add(mysen);
	    		}
	    	}    	
    		System.out.println("Tot. ann. sentences : "+ count + "/" + nList.getLength());
    		System.out.println("===============================================");
    		System.out.println("Tot. tokens in corpus : "+ size);
    		System.out.println("===============================================");
	    }     
	    // catch IO exception
	    catch (Exception e) {
	    		e.printStackTrace();
	    }  
	 }
	
	
	//------------------------------
	// D. Sentence-level annotation
	//------------------------------
	
	 	
	// extract simple annotations from sentence (without concept annotations)
	public static void noConAnnotate(String sentence, GuidAnn ann) throws Exception{
		// we use the base feature extraction methods  
		ann.mineLine(sentence);
	}
	
	
	//----------------------------------
	// C. Print the corpus
	//----------------------------------
	
	
	// method stripping the annotations
	public void printAnnotations() {
		// setting files to save results
		this.mypath = "/home/camilo/Desktop/mmcorpus-results-"+getMyDate()+".txt";
		this.sents = "/home/camilo/Desktop/mmcorpus-raw"+getMyDate()+".txt";		
		// saving the results in strings
    	String result = "";
    	String result2 = "";   	
    	// calling the parser
		DepenParse myparse = new DepenParse();		
		// read the file
	    try { 
	    	File fXmlFile = new File(this.sample);
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(fXmlFile);	 
	    	// optional, but recommended
	    	doc.getDocumentElement().normalize();
	    	// to count sentences that *are* annotated
    		int count = 0;	 
	    	System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	    	result = result + "Root element :" + doc.getDocumentElement().getNodeName() + "\n";
	    	NodeList nList = doc.getElementsByTagName("Sentence");
	    	for (int temp = 0; temp < nList.getLength(); temp++) {
	    		Node nNode = nList.item(temp);
    			// if the node is not empty (has annotations), we
    			// proceed with the processing
	    		if (!checkEmpty(nNode)){
	    			// we augment the counter
	    			count = count + 1;
	    			System.out.println("=========================================");
	    			result = result + "=========================================\n";
	    			// printing the sentence to stdout
	    			System.out.println("Sentence : " + ((Element) nNode).getAttribute("text"));	    			
	    			// parsing the sentence to stdout
	    			myparse.demoAPI(((Element) nNode).getAttribute("text"), "Depen");	    			
	    			// save all
	    			result = result + "Sentence : " + ((Element) nNode).getAttribute("text") + "\n";
	    			// save only text
	    			result2 = result2 + ((Element) nNode).getAttribute("text") + "\n\n";
	    			System.out.println("=========================================");
	    			result = result + "=========================================\n";
	    			List<Node> desc = getAllDescendants(nNode);
	    			for (Node n : desc){
	    				if (n.getNodeName() == "RelationSemanticType" && n.getParentNode().getNodeName() == "Sentence"){
	    					Element eElement = (Element) n;
	    					System.out.println("Sense : " + eElement.getTextContent());
	    					// save
	    					result = result + "Sense : " + eElement.getTextContent() + "\n";
	    				}
	    				if (n.getNodeName() == "Subject"){
	    					Element eElement = (Element) n;
	    					System.out.println("Subject : " + eElement.getAttribute("text"));
	    					// save
	    					result = result + "Subject : " + eElement.getAttribute("text") + "\n";
	    					System.out.println("-----------------------------------------");
	    					// save
	    					result = result + "-----------------------------------------\n";
	    				}
	    				if (n.getNodeName() == "RelationSemanticType"){
	    					System.out.println("Sense : " + n.getTextContent());
	    					// save
	    					result = result + "Sense : " + n.getTextContent() + "\n";
	    				}
	    				if (n.getNodeName() == "Object"){
	    					Element eElement = (Element) n;
	    					System.out.println("Object : " + eElement.getAttribute("text"));
	    					// save
	    					result = result + "Object : " + eElement.getAttribute("text") + "\n";
	    					System.out.println("-----------------------------------------");
	    					// save
	    					result = result + "-----------------------------------------\n";
		    			}
	    				if (n.getNodeName() == "Predicate"){
	    					Element eElement = (Element) n;
	    					System.out.println("Relation : " + eElement.getAttribute("text"));
	    					// save
	    					result = result + "Relation : " + eElement.getAttribute("text") + "\n";
	    					System.out.println("Indicator : " + eElement.getAttribute("indicatorType"));
	    					// save
	    					result = result + "Indicator : " + eElement.getAttribute("indicatorType") + "\n";
	    					System.out.println("Sense : " + eElement.getAttribute("type"));
	    					// save
	    					result = result + "Sense : " + eElement.getAttribute("type") + "\n";
	    					System.out.println("*****************************************");
	    					// save
	    					result = result + "*****************************************\n";
	    				}
	    			}
	    		}
	    	}    	
    		System.out.println("=========================================");
    		// save
    		result = result + "=========================================\n";
    		System.out.println("Tot. ann. sentences : "+ count + "/" + nList.getLength());
    		// save
    		result = result + "Tot. ann. sentences : " + count + "/" + nList.getLength();
	    } 
	    // catch IO exception
	    catch (Exception e) {
	    		e.printStackTrace();
	    }	    
	    // saving the results
	    saveResult(result, this.mypath);
	    saveResult(result2, this.sents);    
	 }
	 
}
