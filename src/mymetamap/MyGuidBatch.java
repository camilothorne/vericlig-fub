package mymetamap;


// java
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


// Stanford NLP
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreebankLanguagePack;


// NLM
import gov.nih.nlm.nls.skr.GenericObject;



// Class for accessing the NLM 
// MetaMap remote web service;
// it annotates raw clinical texts with UMLS
// concepts and prints the results to stout
// 
public class MyGuidBatch {
	
	
	   // Private static fields
	   private static String myusern 	= "camilothorne";
	   private static String mypwd		= "Ca10265224";
	   private static String myemail	= "cthorne@inf.unibz.it";
	   private static String ksource	= "1011";
	   
	   
	   // Public static fields
	   public static TreebankLanguagePack tlp;
	   public static GrammaticalStructureFactory gsf;
	   public static Tree parse;
	   public static LexicalizedParser lp;
	   
	   
	   // Dynamic field(s)
	   public BufferedReader file;
	   public ArrayList<String> sents;
	   public String path;
	   
	   
	   // Constructor
	   public MyGuidBatch(String path){
		   // new buffer
		   file = null;
		   // Stanford tagger
		   tlp = new PennTreebankLanguagePack();
		   gsf = tlp.grammaticalStructureFactory();
		   // loading language model + parsing options
		   lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		   lp.setOptionFlags("-maxLength", "80", "-outputFormat", "typedDependenciesCollapsed");
		   // raw guideline
		   this.path = path;
		   sents = readFile(path,file);
		   processLines(sents);
	   }
	   
		   
	   // Converting file to list of sentences
	   public ArrayList<String> readFile(String filepath, BufferedReader file){
		   ArrayList<String> result = new ArrayList<String>();
		   try {
				String sCurrentLine;
				file = new BufferedReader(new FileReader(filepath));
				while ((sCurrentLine = file.readLine()) != null) {
					//System.out.println(sCurrentLine);
					result.add(sCurrentLine);
				}	 
			} 
		   catch (IOException ex) {
				ex.printStackTrace();
			} 
		   finally {
				try {
					if (file != null)
						file.close();
				} 
				catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		   return result;
	   }
	   
	   
	   // Tokenizer
	   public List<CoreLabel> myTokenize(String sent){
			TokenizerFactory<CoreLabel> tokenizerFactory = 
					PTBTokenizer.factory(new CoreLabelTokenFactory(), "");// initialize tokenizer
			List<CoreLabel> rawWords = 
					tokenizerFactory.getTokenizer(new StringReader(sent)).tokenize(); // run tokenizer
			return rawWords;
	   }
	   
	   
	   // Tag single words
	   public void processLines(ArrayList<String> samples){
		   for (String samp: samples){
			   List<CoreLabel> tokens = myTokenize(samp);// call tokenizer
			   //parse = lp.apply(tokens);
			   //System.out.println(tokens);
			   for (CoreLabel tok: tokens){
				   System.out.println(tok.originalText());
				   processString(tok.originalText());// call MetaMap
			   }
		   }
	   }
		
	   
	   // MetaMap request method 
	   public void processString(String s){
		   
		   System.out.println("metamap");
		   
		   // request	   
	       GenericObject myIntMMObj = new GenericObject(100, myusern, mypwd);
	       
	       // request fields
	       myIntMMObj.setField("Email_Address", myemail);
	       //StringBuffer buffer = new StringBuffer(s);
	       //String bufferStr = buffer.toString();
	       myIntMMObj.setField("APIText", s);
	       myIntMMObj.setField("KSOURCE", ksource);
	       
	       //options
	       String opts = "--ignore_word_order --all_derivational_variants --threshold 5" + 
	    		   		 " --all_acros_abbrs --word_sense_disambiguation --WSD localhost --silent" +
	    		   		 " --ignore_stop_phrases --composite_phrases 3 --tagger localhost";
	       myIntMMObj.setField("COMMAND_ARGS", opts);
	             
	       // submit the request
	       try{
	          String results = myIntMMObj.handleSubmission();
	          System.out.println(results+"empty?");
	          prettyPrintA(results);
	       } 
	       catch (RuntimeException ex) {
	          System.err.println("");
	          System.err.print("An ERROR has occurred while processing your");
	          System.err.println(" request, please review any");
	          System.err.print("lines beginning with \"Error:\" above and the");
	          System.err.println(" trace below for indications of");
	          System.err.println("what may have gone wrong.");
	          System.err.println("");
	          System.err.println("Trace:");
	          ex.printStackTrace();
	       }
	       
	   }
		  
	   
	   // Pretty prints results 
	   public static void prettyPrintA(String s){	   
		   String[] lines = s.split("\n");
		   for (String lin: lines){
			   if (lin.contains("Phrase")){
				   System.out.println(lin);
			   }
			   if (lin.contains("Candidates")){
				   System.out.println(lin);
			   }
			   if (lin.contains("Mapping")){
				   System.out.println(lin);
			   }
			   if (lin.endsWith("]")){
				   System.out.println(lin);
			   }
			   if (lin==""){
				   System.out.println(lin);
			   }
		   }
		   
	   }

}
