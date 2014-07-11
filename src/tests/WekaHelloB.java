package tests;


import Evaluation.WekaHello;


public class WekaHelloB extends WekaHello {

	
	// constructor
	public WekaHelloB(String train_file, String eval_file) throws Exception {
		super(train_file, eval_file);
	}
	
	
    // main method
    public static void main (String[] args){
    	
    	String train1 = "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/semrepB-small.arff";
    	String eval1  = "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/semrepB.arff";
    	
    	try{
    		System.out.println("###====================================###");	
    		System.out.println("### 'unigram' corpus");
    		System.out.println("###====================================###");
    		new WekaHello(train1,eval1);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    

}
