package tests;

import java.io.*;
import gov.nih.nlm.nls.skr.*;


public class MetaMap
{
   public static void main(String args[])
   {
        GenericObject myIntMMObj = new GenericObject(100, "camilothorne", "Ca10265224");

        // REQUIRED FIELDS:
        //    -- Email_Address
        //    -- APIText
        //    -- KSOURCE
        //         valid KSOURCE: 99, 06, 09, 0910, 10, 1011
        //         respectively, UMLS 1999, 2006AA, 2009AA, 2009AB,
        //                       2010AA, and 2010AB
        //
        // NOTE: The maximum length is 10,000 characters for APIText.  The
        //       submission script will reject your request if it is larger.

        myIntMMObj.setField("Email_Address", "cthorne@inf.unibz.it");

        StringBuffer buffer = new StringBuffer("A spinal tap was performed and oligoclonal bands were detected in the cerebrospinal fluid.\n");
        
        String b = 	"The principal autoantigens that have been suggested as potential "+ 
        			"triggers of autoimmune responses in ather "+
        			"osclerosis are modified forms of low-density lipoproteins, heat "+ 
        			"shock proteins and beta2 glycoprotein I.\n";
        
        String c = "Continue with metformin if blood glucose control remains inadequate "+ 
    			   "and another oral glucose-lowering medication is added.\n";
        
        StringBuffer buffer2 = new StringBuffer(b);
        
        //String bufferStr = buffer.toString();
        //myIntSRObj.setField("APIText", bufferStr);

        String bufferStr2 = buffer.toString();
        
        myIntMMObj.setField("APIText", bufferStr2);

        myIntMMObj.setField("KSOURCE", "99");

        // Optional field, program will run default MetaMap if not specified

        String opts = "--ignore_word_order --all_derivational_variants --threshold 5" + 
		   		 " --all_acros_abbrs --word_sense_disambiguation --WSD localhost --silent" +
		   		 " --ignore_stop_phrases --composite_phrases 3 --tagger localhost";
        myIntMMObj.setField("COMMAND_ARGS", opts);

        // Submit the job request

        try
        {
           String results = myIntMMObj.handleSubmission();
           System.out.print(results);

        } catch (RuntimeException ex) {
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
   } // main
} // class MMInteractiveUser

