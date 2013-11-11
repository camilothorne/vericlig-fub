package tests;

import java.io.*;
import gov.nih.nlm.nls.skr.*;

public class RelMap
{
   public static void main(String args[])
   {
        GenericObject myIntSRObj = new GenericObject(200,"camilothorne", "Ca10265224");

        // REQUIRED FIELDS:
        //    -- Email_Address
        //    -- APIText
        //
        // NOTE: The maximum length is 10,000 characters for APIText.  The
        //       submission script will reject your request if it is larger.


        myIntSRObj.setField("Email_Address", "cthorne@inf.unibz.it");
        //myIntSRObj.setField("Username", "camilothorne");
        //myIntSRObj.setField("password", "Ca10265224");

        StringBuffer buffer = new StringBuffer("A spinal tap was performed and oligoclonal bands were detected in the cerebrospinal fluid.\n");
        
        String b = 	"The principal autoantigens that have been suggested as potential "+ 
        			"triggers of autoimmune responses in ather "+
        			"osclerosis are modified forms of low-density lipoproteins, heat "+ 
        			"shock proteins and beta2 glycoprotein I.\n";
        
        String c = "Continue with metformin if blood glucose control remains inadequate "+ 
    			   "and another oral glucose-lowering medication is added.\n";
        
        StringBuffer buffer2 = new StringBuffer(c);
        
        //String bufferStr = buffer.toString();
        //myIntSRObj.setField("APIText", bufferStr);

        String bufferStr2 = buffer2.toString();
        myIntSRObj.setField("APIText", bufferStr2);
        
        // Optional field, program will run default SemRep if not specified

        myIntSRObj.setField("COMMAND_ARGS", "-D");

        // Submit the job request

        try
        {
           String results = myIntSRObj.handleSubmission();
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
} // class SRInteractive

