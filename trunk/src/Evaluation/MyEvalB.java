package Evaluation;


// java
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;
import java.text.NumberFormat;  


// weka
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes; 				// naive Bayes
import weka.classifiers.functions.Logistic; 			// logistic classifier
import weka.classifiers.functions.MultilayerPerceptron; // neural network
import weka.classifiers.functions.SMO; 					// SVM
import weka.classifiers.trees.J48;						// decision tree
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;


// my own
import rw.Write; // to write stuff on files


//===============================================================================


// Test Class
//
// N.B. This class trains and evaluates the Weka classifiers
// on the SemRep data (NPs, sentences and relations).
// The auxiliary classes build the files used to generate the
// reports.
//
//
public class MyEvalB {

	
	// A.1 Fields
	
	
	// training and eval sets
	private Instances trainset;
	private Instances evalset;
	// rounding
    NumberFormat nf = NumberFormat.getNumberInstance(); 
	
	
	// A.2 Constructor
	
	
	// constructor
	public MyEvalB(String train_file, String eval_file, String typ, Experiment resul) throws Exception{
		
		
		// classifiers
		Classifier[] cla = {new Logistic(),new SMO(),new NaiveBayes(),new MultilayerPerceptron(),new J48()};
		
		
		// set datasets
		setTrain(train_file); // set trainset
		setEvaluation(eval_file); // set eval set

						
		// experiments
		
		// first 
		if (typ == "uni"){
			
			// classifiers x metrics x features
			resul.classif = new String[cla.length][4][this.trainset.numAttributes()-1];//cross-validation
			resul.classifB = new String[cla.length][4][this.trainset.numAttributes()-1];//custom split
			// run all
			runEval(this.trainset,this.evalset,resul,cla,0); // custom
			runCrossVal(this.evalset,resul,cla,resul.fold,0); // 10-fold cross-validation	
			// project away attributes
			selectAttributes(this.trainset.numAttributes(),resul,cla);
			
			}
		
		// second
		if (typ == "bi"){
			
			// classifiers x metrics x features
			resul.classif = new String[cla.length][4][(this.trainset.numAttributes()/2)-1];//cross-validation
			resul.classifB = new String[cla.length][4][(this.trainset.numAttributes()/2)-1];//custom split
			// run all
			runEval(this.trainset,this.evalset,resul,cla,0); // custom
			runCrossVal(this.evalset,resul,cla,resul.fold,0); // 10-fold cross-validation
			// select
			selectAttributesB(this.trainset.numAttributes(),resul,cla);
			
		}
		
		// third
		if (typ == "sel"){
			
			// classifiers x metrics x features
			resul.classif = new String[cla.length][4][(this.trainset.numAttributes()-1)/2];//cross-validation
			resul.classifB = new String[cla.length][4][(this.trainset.numAttributes()-1)/2];//custom split
			// run all
			runEval(this.trainset,this.evalset,resul,cla,0); // custom
			runCrossVal(this.evalset,resul,cla,resul.fold,0); // 10-fold cross-validation
			// select
			selectAttributesB(this.trainset.numAttributes(),resul,cla);
			
		}
		
		
		// print and save
		System.out.println("\n################ RESULTS #################\n");
		
		
		// print by feature
		System.out.println("\n(A1. Cross Validation)\n");
		resul.printbyFeat(resul.classif,"cross");
		System.out.println("\n(A2. Custom split)\n");
		resul.printbyFeat(resul.classifB,"custom");
		
		
		// print averages
		System.out.println("\n(B1. Cross validation (avg))\n");
		resul.printAVG(resul.classif,"cross");
		System.out.println("\n(B2. Custom split (avg))\n");
		resul.printAVG(resul.classifB,"custom");

		
		// print F1-measure
		System.out.println("\n(B1. Cross validation (F1))\n");
		resul.printbyF(resul.classif,"cross");
		System.out.println("\n(B2. Custom split (F1))\n");
		resul.printbyF(resul.classifB,"custom");		
		
		
	}
	
	
	// B.1 Methods
	
	
	// set training set
	public void setTrain(String train_file) throws Exception{
		BufferedReader trainfile = null;
		trainfile = new BufferedReader(new FileReader(train_file));
		this.trainset = new Instances(trainfile);
		trainfile.close();
	}
    
    
	// set evaluation set
	public void setEvaluation(String eval_file) throws Exception{
		BufferedReader evalfile = null;
		evalfile = new BufferedReader(new FileReader(eval_file));	        
		this.evalset = new Instances(evalfile);
    	evalfile.close();
	}
	
	
	// labeling method
	public static void myLabel(Classifier myclass, String unlabelled, 
			String predictions, int number) throws Exception{		
    	// unlabeled sample
    	BufferedReader runfile = null;
    	runfile = new BufferedReader(new FileReader(unlabelled));
    	Instances testset = new Instances(runfile);
    	// filter out attribute X_i
    	// testset = myFilter(testset,3);
        testset.setClassIndex(number);
        runfile.close();
        // label instances       
        for (int i = 0; i < testset.numInstances(); i++) {
          double clsLabel = myclass.classifyInstance(testset.instance(i));
          testset.instance(i).setClassValue(clsLabel);
        }
        // save labeled data
        BufferedWriter writer = new BufferedWriter(
                                  new FileWriter(predictions));
        writer.write(testset.toString());
        writer.newLine();
        writer.flush();
        writer.close();		
	}
	
	
	// method to save results
	public static void saveConfusion(String results, String file_name) throws Exception{
        // save labeling data
        BufferedWriter writer = new BufferedWriter(
                                  new FileWriter(file_name));
        writer.write(results);
        writer.newLine();
        writer.flush();
        writer.close();			
	}
	
	
	// attribute (simple) filter
	// removes attribute X_i from the dataset 
	public static Instances myFilter(Instances data, int index) throws Exception{
		 String[] options = new String[2];
		 options[0] = "-R";                                    // "range"
		 options[1] = ""+index+"";                             // first attribute
		 Remove remove = new Remove();                         // new instance of filter
		 remove.setOptions(options);                           // set options
		 remove.setInputFormat(data);                          // inform filter about dataset **AFTER** setting options
		 Instances newData = Filter.useFilter(data, remove);   // apply filter
		 return newData;
	}
	
	
	// attribute selection
	// removes attribute X_i, for 1<=i<=k, where k is the number
	// of **INDEPENDENT** features/attributes
	// and evaluates the classifiers
	public void selectAttributes(int n, Experiment res, Classifier[] cla) throws Exception{
		for (int i=1;i<(n-1);i++){
	        // filter out attribute X_i from trainset and evalset
	        Instances mytrainset = myFilter(this.trainset,i);
	        Instances myevalset  = myFilter(this.evalset,i);
	        // run evaluation
	    	System.out.println("\n##########################################");
			System.out.println("Removing attribute "+ i);
	    	System.out.println("##########################################");
	        // run custom 2/5-3/5
	    	runEval(mytrainset,myevalset,res,cla,i);
	        // run5-fold cross-validation
	        runCrossVal(myevalset,res,cla,5,i);
		}
	}
	
	
	// attribute selection
	// removes attribute X_i, for 1<=i<=k, where k is the number
	// of **INDEPENDENT** features/attributes
	// with *REPEATED* vectors
	public void selectAttributesB(int n, Experiment res, Classifier[] cla) throws Exception{
		for (int i=1;i<((n-1)/2);i++){
	        // filter out attribute X_i from trainset and evalset
	        Instances mytrainsetA = myFilter(this.trainset,i);
	        Instances myevalsetA  = myFilter(this.evalset,i);
	        Instances mytrainset  = myFilter(mytrainsetA,i+6);
	        Instances myevalset	  = myFilter(myevalsetA,i+6);
	        // run evaluation
	    	System.out.println("\n##########################################");
			System.out.println("Removing attribute "+ i);
	    	System.out.println("##########################################");
	        // run custom 2/5-3/5
	        runEval(mytrainset,myevalset,res,cla,i);
	        // run5-fold cross-validation
	        runCrossVal(myevalset,res,cla,5,i);
		}
	}

		
	// run n-fold cross-validation
	public void runCrossVal(Instances eval_set, Experiment res, Classifier[] cla, int n, int feat) throws Exception {
		
    	System.out.println("\n******************************************");
    	System.out.println(" Running cross-validation...  ");       
    	System.out.println("******************************************");
    	
        // selecting dependent variable
        int num = eval_set.numAttributes()-1;
        // last of evaluation set
        eval_set.setClassIndex(num);
        
        // classifiers
        Classifier myclassA 			= cla[0]; // Logit 
        Classifier myclassB 			= cla[1]; // SVM
        Classifier myclassC 			= cla[2]; // Bayes
        Classifier myclassD			 	= cla[3]; // neural
        Classifier myclassE 			= cla[4]; // tree
        
        // evaluation
        Evaluation evalA = new Evaluation(eval_set);
        Evaluation evalB = new Evaluation(eval_set);
        Evaluation evalC = new Evaluation(eval_set);
        Evaluation evalD = new Evaluation(eval_set);
        Evaluation evalE = new Evaluation(eval_set);
        
        
        // rounding to 2 decimal places
        nf.setMaximumFractionDigits(2);  
        nf.setMinimumFractionDigits(2); 
        
        
        // n-fold cross-validation
        
        // Logit
        evalA.crossValidateModel(myclassA, eval_set, n, new Random(1));
        //System.out.println(evalA.toSummaryString("\n=================\nResults-1 Logit\n=================\n",false));
        //System.out.println(evalA.toClassDetailsString("=================\nResults-2 Logit\n=================\n"));
        res.classif[0][0][feat] = String.valueOf(nf.format(evalA.precision(0)));
        res.classif[0][1][feat] = String.valueOf(nf.format(evalA.recall(0)));
        res.classif[0][2][feat] = String.valueOf(nf.format(evalA.fMeasure(0)));
        res.classif[0][3][feat] = String.valueOf(nf.format(evalA.pctCorrect()/100));
        		
        // SVM
        evalB.crossValidateModel(myclassB, eval_set, n, new Random(1));
        //System.out.println(evalB.toSummaryString("\n=================\nResults-1 SVM\n=================\n",false));
        //System.out.println(evalB.toClassDetailsString("=================\nResults-2 SVN\n=================\n"));
        res.classif[1][0][feat] = String.valueOf(nf.format(evalB.precision(0)));
        res.classif[1][1][feat] = String.valueOf(nf.format(evalB.recall(0)));
        res.classif[1][2][feat] = String.valueOf(nf.format(evalB.fMeasure(0)));
        res.classif[1][3][feat] = String.valueOf(nf.format(evalB.pctCorrect()/100));
        
        // Bayes
        evalC.crossValidateModel(myclassC, eval_set, n, new Random(1));
        //System.out.println(evalC.toSummaryString("\n=================\nResults-1 Bayes\n=================\n",false));
        //System.out.println(evalC.toClassDetailsString("=================\nResults-2 Bayes\n=================\n"));
        res.classif[2][0][feat] = String.valueOf(nf.format(evalC.precision(0)));
        res.classif[2][1][feat] = String.valueOf(nf.format(evalC.recall(0)));
        res.classif[2][2][feat] = String.valueOf(nf.format(evalC.fMeasure(0)));
        res.classif[2][3][feat] = String.valueOf(nf.format(evalC.pctCorrect()/100));
        
        // Neural
        evalD.crossValidateModel(myclassD, eval_set, n, new Random(1));
        //System.out.println(evalD.toSummaryString("\n=================\nResults-1 Neural\n=================\n",false));
        //System.out.println(evalD.toClassDetailsString("=================\nResults-2 Neural\n=================\n"));
        res.classif[3][0][feat] = String.valueOf(nf.format(evalD.precision(0)));
        res.classif[3][1][feat] = String.valueOf(nf.format(evalD.recall(0)));
        res.classif[3][2][feat] = String.valueOf(nf.format(evalD.fMeasure(0)));
        res.classif[3][3][feat] = String.valueOf(nf.format(evalD.pctCorrect()/100));
        
        // Tree
        evalE.crossValidateModel(myclassE, eval_set, n, new Random(1));
        //System.out.println(evalE.toSummaryString("\n=================\nResults-1 Tree\n=================\n",false));
        //System.out.println(evalE.toClassDetailsString("=================\nResults-2 Tree\n=================\n"));
        res.classif[4][0][feat] = String.valueOf(nf.format(evalE.precision(0)));
        res.classif[4][1][feat] = String.valueOf(nf.format(evalE.recall(0)));
        res.classif[4][2][feat] = String.valueOf(nf.format(evalE.fMeasure(0)));
        res.classif[4][3][feat] = String.valueOf(nf.format(evalE.pctCorrect()/100));
        
	}
	
	
	// run evaluation method
    public void runEval(Instances train_set, Instances eval_set, Experiment res, Classifier[] cla, int feat) throws Exception {
            	    	
    	System.out.println("\n******************************************");
    	System.out.println(" Running evaluation...  ");       
    	System.out.println("******************************************");
    	
        // selecting dependent variable
        int num = train_set.numAttributes()-1;
        // last of train set
        train_set.setClassIndex(num);
        // last of evaluation set
        eval_set.setClassIndex(num);
        
        Classifier myclassA 			= cla[0]; // Logit 
        Classifier myclassB 			= cla[1]; // SVM
        Classifier myclassC 			= cla[2]; // Bayes
        Classifier myclassD			 	= cla[3]; // neural
        Classifier myclassE 			= cla[4]; // tree
        
        // training the classifiers
        myclassA.buildClassifier(train_set);
        myclassB.buildClassifier(train_set);
        myclassC.buildClassifier(train_set);
        myclassD.buildClassifier(train_set);
        myclassE.buildClassifier(train_set);
        
        // rounding to 2 decimal places
        nf.setMaximumFractionDigits(2);  
        nf.setMinimumFractionDigits(2); 
                
        // evaluation statistics 
        
        // Logit
        Evaluation myevalA = new Evaluation(train_set);      
        myevalA.evaluateModel(myclassA, eval_set);
        //System.out.println(myevalA.toSummaryString("\n=================\nResults-1 Logit\n=================\n",false));
        //System.out.println(myevalA.toClassDetailsString("=================\nResults-2 Logit\n=================\n"));
        res.classifB[0][0][feat] = String.valueOf(nf.format(myevalA.precision(0)));
        res.classifB[0][1][feat] = String.valueOf(nf.format(myevalA.recall(0)));
        res.classifB[0][2][feat] = String.valueOf(nf.format(myevalA.fMeasure(0)));
        res.classifB[0][3][feat] = String.valueOf(nf.format(myevalA.pctCorrect()/100));
                
        // SVM
        Evaluation myevalB = new Evaluation(train_set);
  		myevalB.evaluateModel(myclassB, eval_set);
        //System.out.println(myevalB.toSummaryString("\n=================\nResults-1 SVM\n=================\n",false));
        //System.out.println(myevalB.toClassDetailsString("=================\nResults-2 SVM\n=================\n"));
        res.classifB[1][0][feat] = String.valueOf(nf.format(myevalB.precision(0)));
        res.classifB[1][1][feat] = String.valueOf(nf.format(myevalB.recall(0)));
        res.classifB[1][2][feat] = String.valueOf(nf.format(myevalB.fMeasure(0)));
        res.classifB[1][3][feat] = String.valueOf(nf.format(myevalB.pctCorrect()/100));    
        
        // Bayes
        Evaluation myevalC = new Evaluation(train_set);
        myevalC.evaluateModel(myclassC, eval_set);
        //System.out.println(myevalC.toSummaryString("\n=================\nResults-1 Bayes\n=================\n",false));
        //System.out.println(myevalC.toClassDetailsString("=================\nResults-2 Bayes\n=================\n"));
        res.classifB[2][0][feat] = String.valueOf(nf.format(myevalC.precision(0)));
        res.classifB[2][1][feat] = String.valueOf(nf.format(myevalC.recall(0)));
        res.classifB[2][2][feat] = String.valueOf(nf.format(myevalC.fMeasure(0)));
        res.classifB[2][3][feat] = String.valueOf(nf.format(myevalC.pctCorrect()/100));
        
        // Neural
        Evaluation myevalD = new Evaluation(train_set);
        myevalD.evaluateModel(myclassD, eval_set);
        //System.out.println(myevalD.toSummaryString("\n=================\nResults-1 Neural\n=================\n",false));
        //System.out.println(myevalD.toClassDetailsString("=================\nResults-2 Neural\n=================\n"));
        res.classifB[3][0][feat] = String.valueOf(nf.format(myevalD.precision(0)));
        res.classifB[3][1][feat] = String.valueOf(nf.format(myevalD.recall(0)));
        res.classifB[3][2][feat] = String.valueOf(nf.format(myevalD.fMeasure(0)));
        res.classifB[3][3][feat] = String.valueOf(nf.format(myevalD.pctCorrect()/100)); 
        
        // Tree
        Evaluation myevalE = new Evaluation(train_set);
        myevalE.evaluateModel(myclassE, eval_set);
        //System.out.println(myevalE.toSummaryString("\n=================\nResults-1 Tree\n=================\n",false));
        //System.out.println(myevalE.toClassDetailsString("=================\nResults-2 Tree\n=================\n"));
        res.classifB[4][0][feat] = String.valueOf(nf.format(myevalE.precision(0)));
        res.classifB[4][1][feat] = String.valueOf(nf.format(myevalE.recall(0)));
        res.classifB[4][2][feat] = String.valueOf(nf.format(myevalE.fMeasure(0)));
        res.classifB[4][3][feat] = String.valueOf(nf.format(myevalE.pctCorrect()/100));             
       
    }
    
    
	// save classification model
	public static void saveModel(Classifier myclass, String name) throws Exception{
        ObjectOutputStream oos = new ObjectOutputStream(
        		new FileOutputStream("/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/experiments/"+name+".model"));
        oos.writeObject(myclass);
        oos.flush();
        oos.close();		
	}
	
	
	// load classification model
	public static Classifier openModel(String name) throws Exception{
        ObjectInputStream ois = new ObjectInputStream(
        		new FileInputStream("/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/experiments/"+name+".model"));
        Classifier cls = (Classifier) ois.readObject();
        ois.close();
        return cls;
	}
    
	
	// B.2 main method
	
    
    // main method
    public static void main (String[] args){
    	
    	String train1 = "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/semrepB-small.arff";
    	String eval1  = "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/semrepB.arff";
    	
    	String train2 = "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/semrepB2-small.arff";
    	String eval2  = "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/semrepB2.arff";
    	
    	String train3 = "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/semrep-relation-small.arff";
    	String eval3  = "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/semrep-relation.arff";
    	
    	try{
    		
    		System.out.println("###====================================###");
    		System.out.println("### A. activity (no context)");
    		System.out.println("###====================================###");
    		Experiment expA = new Experiment("activity",10);
    		new MyEvalB(train1,eval1,"uni",expA);
    		new SaveExperiment(expA);
    		
    		System.out.println("\n###====================================###");
    		System.out.println("### B. activity (context)");
    		System.out.println("###====================================###");
    		Experiment expB = new Experiment("activity-con",10);
    		new MyEvalB(train2,eval2,"bi",expB);
    		new SaveExperiment(expB);
    		
    		System.out.println("\n###====================================###");
    		System.out.println("### C. relation");
    		System.out.println("###====================================###");
    		Experiment expC = new Experiment("relation",10);
    		new MyEvalB(train3,eval3,"sel",expC);
    		new SaveExperiment(expC);
    		
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }

    
} // end of the Class //


//===============================================================================


// Class for displaying/saving data
//
// N.B. This class wraps into .tex and .csv files
// the results (P, R, F, Acc w.r.t. classifier, feature and
// evaluation method) of the experiments
//
//
class Experiment{

	
	// A. Fields
	
	
	// data
	String[][][] classif; // cross-validation
	String[][][] classifB;// custom split
	
	// identifiers
	String name; // name
	int fold;// n-fold (cross-validation)
	
	
	// tags
	String[] ftr 	= 	{"none  ", "lf    ", "sub   ", "nest  ", "pos   ", "ls    ", "freq  ", "hd    ", "class "};
	String[] metric = 	{"Pr  ", "Re  ", "F1  ", "Ac  "};
	String[] tagger = 	{"Logit ", "SVM   ", "Bayes ", "Neural", "Tree  "};
	
	
	// csv tags
	String[] ftrB 	= 	{"'none'", "'lf'", "'sub'", "'nest'", "'pos'", "'ls'", "'freq'", "'hd'", "'class'"};
	String[] metricB = 	{"'Pr'", "'Re'", "'F1'", "'Ac'"};
	String[] taggerB = 	{"'Logit'", "'SVM'", "'Bayes'", "'Neural'", "'Tree'"};
	
	
	// saving the stuff (cross-validation)
	String myfeatureA;
	String myaverageA;
	String mycsvA;
	String mycsvAF;
	// saving the stuff (custom)
	String myfeatureB;
	String myaverageB;
	String mycsvB;	
	String mycsvBF;	
	
	
    // rounding to 2 decimal places
    NumberFormat nf = NumberFormat.getNumberInstance(); 

    
    // B. Methods
    
	
	// constructor
	public Experiment(String name,int fold){		
		this.name = name;
		this.fold = fold;
	}
	
	
	// print averages
	public void printAVG(String[][][] clas, String typ){
		String res = "";
		String csv = "";
	    nf.setMaximumFractionDigits(2);  
	    nf.setMinimumFractionDigits(2);
		res = res + "\\begin{tabular}{r";
		for (int s=0;s<metric.length;s++){
			res = res + "|r";
		}
		res = res + "}" + "\n";
		res = res + "\\hline" + "\n";
		res = res + "avg   ";
		csv = csv + "'avg'";
		for (int t=0;t<metric.length;t++){
			res = res + " & " + metric[t];
			csv = csv + "," + metricB[t];
		}
		res = res + "\\" + "\\" + "\n";
		res = res + "\\hline" + "\n";
		csv = csv + "\n";
		for (int k=0;k<clas[0][0].length;k++){
				//System.out.print(ftr[k]);
				res = res + ftr[k];
				csv = csv + ftrB[k];
				for (int j=0;j<clas[0].length;j++){
					Float fres = new Float(0.00);
					for (int i=0;i<clas.length;i++){
						fres = fres + Float.valueOf(clas[i][j][k]);
					}
					fres = (fres/clas.length);
					//System.out.print(" "+metric[j]+"(avg)="+nf.format(fres));
					res = res + " & "+nf.format(fres);
					csv = csv + ","+nf.format(fres);
				}
				//System.out.println();
				res = res + "\\" + "\\" + "\n";
				csv = csv + "\n";
			}
			//System.out.println();
			res = res + "\\end{tabular}" + "\n\n";
			//csv = csv + "\n";
			if (typ=="cross"){
				this.myaverageA = res;
				this.mycsvA = csv;
			}
			if(typ=="custom"){
				this.myaverageB = res;
				this.mycsvB = csv;			
			}
			//System.out.print(res);
			//System.out.print(csv);
	}
	
	
	// print all by feature
	public void printbyFeat(String[][][] clas, String typ){
		String res = "";
	    nf.setMaximumFractionDigits(2);  
	    nf.setMinimumFractionDigits(2);
	    res = res + "\\begin{tabular}{ccc}\n";
		for (int k=0;k<clas[0][0].length;k++){
				//System.out.println("------------------------------------------");
				//System.out.println(ftr[k]);
				res = res + "\\begin{tabular}{r";
				for (int s=0;s<metric.length;s++){
					res = res + "|r";
				}
				res = res + "}" + "\n";
				res = res + "\\hline" + "\n";
				res = res + ftr[k];
				//System.out.println("------------------------------------------");
				for (int t=0;t<metric.length;t++){
					res = res + " & " + metric[t];
				}
				res = res + "\\" + "\\" + "\n";
				res = res + "\\hline" + "\n";
				for (int j=0;j<clas.length;j++){				
					//System.out.print(tagger[j]+":");
					res = res + tagger[j];			
					for (int i=0;i<clas[0].length;i++){
						//System.out.print(" "+metric[i]+"="+clas[j][i][k]);
						res = res+" & "+clas[j][i][k];
					}
					//System.out.println();
					res = res + "\\" + "\\" + "\n";
				}
				//System.out.println();
				res = res + "\\end{tabular}" + "\n";
				if ((k+1)%3==0){
					res = res + "\\"+"\\"+'\n';
				}
				else{
					res = res + "&\n";
				}
			}
			res = res + "\\end{tabular}\n";
			if (typ=="cross"){
				this.myfeatureA = res;
			}
			if (typ=="custom"){
				this.myfeatureB = res;
			}
			//System.out.print(res);
	}
	

	// print F measure
	public void printbyF(String[][][] clas, String typ){
		String csv = "";
	    nf.setMaximumFractionDigits(2);  
	    nf.setMinimumFractionDigits(2);
		csv = csv + "'feat'";
		for (int t=0;t<tagger.length;t++){
			csv = csv + "," + taggerB[t];
		}
		csv = csv + "\n";
		for (int k=0;k<clas[0][0].length;k++){
				//System.out.print(ftr[k]);
				csv = csv + ftrB[k];
				for (int j=0;j<clas.length;j++){
					Float fres = Float.valueOf(clas[j][2][k]);
					//System.out.print(" "+nf.format(fres));
					csv = csv + ","+nf.format(fres);
				}
				//System.out.println();
				csv = csv + "\n";
			}
			System.out.println();
			if (typ=="cross"){
				this.mycsvAF = csv;
			}
			if(typ=="custom"){
				this.mycsvBF = csv;			
			}
			//System.out.print(csv);
	}
	
	
} // End of Class


//===============================================================================


// Class for saving data
//
// N.B. This class saves the results of the experiments in (1) a .tex table
// and (2) .csv files/tables. The generation of the .pdf (via Latex) and of
// the plots is done by means of a (couple of) Python script(s)
//
//
class SaveExperiment{
	
	
	// fields
	public Experiment exp;	
	// cross-validation
	public String latexcross;
	public String latexcustom;
	// custom
	public String csvcross;
	public String csvcustom;
	public String csvFcross;
	public String csvFcustom;
	
	
	// constructor
	public SaveExperiment(Experiment exp){
	
		this.exp = exp;		
		System.out.println("\n----------(Files to save)----------\n");
		
		// cross-validation
		String path1 	= "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/experiments/"+exp.name+"-"+exp.fold+"cross.tex"; 	// .tex to save
		String path2 	= "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/experiments/"+exp.name+"-"+exp.fold+"cross.csv"; 	// .csv to save
		String path2B 	= "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/experiments/"+exp.name+"-"+exp.fold+"crossF.csv"; 	// .csv to save			
		this.latexcross = setLatexA();
		new Write(path1,this.latexcross);
		this.csvcross  = exp.mycsvA;
		this.csvFcross = exp.mycsvAF;
		//System.out.println(this.csvcross); // check if it is ok
		new Write(path2,this.csvcross);
		new Write(path2B,this.csvFcross);
		
		// custom
		String path3 	= "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/experiments/"+exp.name+"-custom.tex"; 	// .tex to save
		String path4 	= "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/experiments/"+exp.name+"-custom.csv"; 	// .csv to save
		String path4B	= "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/experiments/"+exp.name+"-customF.csv"; // .csv to save
		this.latexcustom = setLatexB();
		new Write(path3,this.latexcustom);
		this.csvcustom 	= exp.mycsvB;
		this.csvFcustom = exp.mycsvBF;
		//System.out.println(this.csvcustom); // check if it is ok
		new Write(path4B,this.csvFcustom);
	
	}
	
	
	// bundle together Latex tables (cross)
	public String setLatexA(){
		
		String res = "";
		res = res + "\\begin{center}\n";
		res = res + this.exp.myfeatureA;
		res = res + "\n";
		res = res + "\\vspace{1cm}\n";
		res = res + this.exp.myaverageA;
		res = res + "\n";
		res = res + "\\end{center}\n";
		//System.out.println(res); // check if all is ok
		return res;
		
	}
	
	
	// bundle together Latex tables (custom)
	public String setLatexB(){
		
		String res = "";
		res = res + "\\begin{center}\n";
		res = res + this.exp.myfeatureB;
		res = res + "\n";
		res = res + "\\vspace{1cm}\n";
		res = res + this.exp.myaverageB;
		res = res + "\n";
		res = res + "\\end{center}\n";
		//System.out.println(res); // check if all is ok
		return res;
		
	}
	

} // End of Class


