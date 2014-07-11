package tests;


import gov.nih.nlm.nls.skr.*;


public class MMTagger{
	
		
   // REQUIRED FIELDS:
   //    -- Email_Address
   //    -- APIText
   //    -- KSOURCE
   //         valid KSOURCE: 99, 06, 09, 0910, 10, 1011
   //         respectively, UMLS 1999, 2006AA, 2009AA, 2009AB,
   //         2010AA, and 2010AB
	
	
   // Static fields
   private static String myusern 	= "camilothorne";
   private static String mypwd		= "Ca10265224";
   private static String myemail	= "cthorne@inf.unibz.it";
   private static String ksource	= "1011";
   private static String opts 		= "--ignore_word_order --all_derivational_variants --threshold 5" + 
	   		 							" --all_acros_abbrs --word_sense_disambiguation --WSD localhost --silent" +
	   		 							" --ignore_stop_phrases --composite_phrases 3 --tagger localhost";
   // NOTE: Run "--help" option to see all options
   // 	 	and run "--XMLf" to get XML output
   
   
   // Dynamic field(s)
   public String[] samples;
   
   
   // Constructor
   public MMTagger(){	   
   }
   
   
   // Tag batch of sentences
   public void processStrings(String[] samples){
	   this.samples = samples;
	   for (int i=0; i<samples.length; i++){
		   processString(samples[i],i+1);
	   }
   }

   
   // Tag single sentence
   public void processSent(String sample){
	   processString(sample,0);
   }   

   
   // Request method 
   public void processString(String s, int n){
	   
	   // Creates one request per buffer	   
       GenericObject myIntMMObj = new GenericObject(100, myusern, mypwd);

       // NOTE: The maximum length is 10,000 characters for APIText.
       // 		The input string can be a whole file, subject to
       //		buffered processing.

       myIntMMObj.setField("Email_Address", myemail);
       StringBuffer buffer = new StringBuffer(s);
       String bufferStr = buffer.toString();
       myIntMMObj.setField("APIText", bufferStr);
       myIntMMObj.setField("KSOURCE", ksource);

       // OPTIONS (MMtx)
       myIntMMObj.setField("COMMAND_ARGS", opts);
             
       // Submit the job request
       try{
          String results = myIntMMObj.handleSubmission();
          if (n != 0){
        	  System.out.print("\n####################################################\n");
          	  System.out.print("###     JOB Nr. "+ n +"\n");
          	  System.out.print("####################################################\n\n");
          	  prettyPrintA(results);
           }
          else{
        	  System.out.print("\n####################################################\n");
        	  prettyPrintA(results);
          }
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
       } // catch
       
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

