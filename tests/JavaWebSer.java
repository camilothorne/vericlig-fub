package tests;



// class to run the *remote* MetaMap annotator
public class JavaWebSer extends MMTagger{

	
   // Dynamic field(s)
   public String[] samples;

   
   // Constructor
   public JavaWebSer(String[] samples){
	   MMTagger tag = new MMTagger();
	   this.samples = samples;
	   for (int i=0; i<samples.length; i++){
		   tag.processString(samples[i],i);
	   }
	   
   }
 
   
   // Main method  
   public static void main(String[] args){

	   // breast cancer  
       String mysent  = "A spinal tap was performed and oligoclonal" + 
    		   			" bands were detected in the cerebrospinal fluid.";
       String mysent0 = "spinal tap";
       String mysent3 = "If adjuvant chemotherapy is indicated following breast-conserving "+
    		    		"surgery, RT should typically be given after chemotherapy is completed.";
       
       // type II diabetes (simple)
       String mysent1 = "Emphasise advice on healthy balanced eating that is applicable to "+
    		    		"the general population when providing advice to people with type 2 diabetes.";
       String mysent2 = "Encourage high-fibre, low-glycaemic-index sources of "+
    		    		"carbohydrate in the diet, such as fruit, vegetables, wholegrains and "+
    		    		"pulses; include low-fat dairy products and oily fish; and control the "+
    		    		"intake of foods containing saturated and trans fatty acids.";
       
       // type II diabetes (complex)
       String example = "Continue with metmorfin if blood glucose control remains or "+
    		   			"becomes inadequate and another oral glucose-lowering "+
    		   			"medication is added.";       
     
       // set samples     
       String[] buff2 = {"Emphasise", "advice", "on", "healthy", "balanced", "eating"};
       String[] buff = {mysent,mysent0,mysent1,mysent2,mysent3};
       String[] ex = {example};

       
       // call annotation service
       new JavaWebSer(ex);
       new JavaWebSer(buff2);
       new JavaWebSer(buff);
       
   }
 
   
} 

