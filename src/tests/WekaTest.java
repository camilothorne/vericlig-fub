package tests;


import evaluation.WekaEval;


public class WekaTest extends WekaEval {

	
	// constructor
	public WekaTest(String train_file, String eval_file) throws Exception {
		super(train_file, eval_file);
	}
	
	
    // main method
    public static void main (String[] args){
    	
    	String train1 = "/home/camilo/meta-map-exp/semrepB-small.arff";
    	String eval1  = "/home/camilo/meta-map-exp/semrepB.arff";
    	
    	try{
    		System.out.println("###====================================###");	
    		System.out.println("### 'unigram' corpus");
    		System.out.println("###====================================###");
    		new WekaTest(train1,eval1);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    

}
