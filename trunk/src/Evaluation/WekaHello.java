package Evaluation;


// java
//import java.awt.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.List;


// weka
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.classifiers.meta.Vote;
import weka.classifiers.meta.Stacking;
import weka.classifiers.meta.Bagging;


// simple evaluation class
public class WekaHello {

	
	// A.1 Fields
	
	
	// training and eval sets
	private Instances trainset;
	private Instances evalset;

	// rounding
    NumberFormat nf = NumberFormat.getNumberInstance(); 
	
	
	// A.2 Constructor
	
	
	// constructor function
	public WekaHello(String train_file, String eval_file) throws Exception{
		
		setTrain(train_file); 		// set trainset
		setEvaluation(eval_file); 	// set eval set
		
        // classifiers
        Classifier myclassa 	= new Logistic(); 				// multinomial logistic regression (max entropy)       
        Classifier myclassb 	= new SMO(); 					// kernel (support vector machine)
        Classifier myclassc 	= new NaiveBayes(); 			// naive bayes
        Classifier myclassd 	= new MultilayerPerceptron(); 	// neural network
        Classifier myclasse 	= new J48(); 					// decision tree
		
        Classifier[] myclassif = {myclassa,myclassb,myclassc,myclassd,myclasse};
        
        // Experiments:
        
        
    	System.out.println("\n******************************************");
    	System.out.println(" Base (cross evaluation)...  ");       
    	System.out.println("******************************************");
	
		// majority vote 	(base)
		runEvalVote(this.trainset,this.evalset,myclassif,"base"); // run experiment

		// stacking 		(base)
		runEvalStack(this.trainset,this.evalset,myclassif,"base"); // run experiment
		
		
    	System.out.println("\n******************************************");
    	System.out.println(" Att selection (cross evaluation)...  ");       
    	System.out.println("******************************************");
		
		// majority vote 	(att selection)
		runEvalVote(this.trainset,this.evalset,myclassif,"sel"); // run experiment

		// stacking 		(att selection)
		runEvalStack(this.trainset,this.evalset,myclassif,"sel"); // run experiment
		
		
    	System.out.println("\n******************************************");
    	System.out.println(" Bagging (cross evaluation)...  ");       
    	System.out.println("******************************************");
		
		// majority vote 	(bagging)
		runEvalVote(this.trainset,this.evalset,myclassif,"bag"); // run experiment

		// stacking 		(bagging)
		runEvalStack(this.trainset,this.evalset,myclassif,"bag"); // run experiment
		
	

		// don't project away attributes
		// runEval(this.trainset,this.evalset,myclassif); // run experiment
    	
		
		// project away attributes
		// selectAttributes(this.trainset.numAttributes(),myclassif); // run experiment
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
	
	
	// labelling method
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
	
	
	// attribute (complex) filter
	// removes attribute X_{i_1}...X_{i_k} from the dataset 
	public static Instances myFilters(Instances data, List<Integer> index) throws Exception{
		 Instances mydata = data;
		 String[] options = new String[2];
		 String myindex = "" + index.get(0);
		 for (Integer i : index){
			 if (!i.equals(index.get(0))){
				 myindex = myindex + "," + i;
			 }
		 }
		 options[0] = "-R";                                    // "range"
		 options[1] = ""+index+"";                             // first attribute
		 Remove remove = new Remove();                         // new instance of filter
		 remove.setOptions(options);                           // set options
		 remove.setInputFormat(mydata);                        // inform filter about dataset **AFTER** setting options
		 Instances newData = Filter.useFilter(mydata, remove); // apply filter
		 return newData;
	}
	
	
	// attribute selection
	public void selectAttributes(int n, Classifier[] cls) throws Exception{
		for (int i=1;i<n;i++){
	        // filter out attribute X_i from trainset and evalset
	        Instances mytrainset = myFilter(this.trainset,i);
	        Instances myevalset  = myFilter(this.evalset,i);
	        // run evaluation
	    	System.out.println("\n##########################################");
			System.out.println("Removing attribute "+ i);
	    	System.out.println("##########################################");
	        runEval(mytrainset,myevalset,cls);
		}
	}
	
	
	// selecting all possible non-repeated 2^n - 1 combinations
	// of n attributes in the dataset
	public void selectAttribute2(int n, Classifier[] cls) throws Exception {
		Set<Integer> set = new HashSet<Integer>();
		for (int i=0;i<n;i++){
			set.add(i);
		}
		Set<Set<Integer>> power = powerSet(set);
		for (Set<Integer> se : power){
			if (!se.isEmpty()){
				List<Integer> list = new ArrayList<Integer>(se);
				// filter out attributes X_i...X_j from trainset and evalset
				Instances mytrainset = myFilters(this.trainset,list);
				Instances myevalset  = myFilters(this.evalset,list);
				// run evaluation
				runEval(mytrainset,myevalset,cls);
			}
		}
	}
	
	
	// power set
	public static Set<Set<Integer>> powerSet(Set<Integer> originalSet) {
        Set<Set<Integer>> sets = new HashSet<Set<Integer>>();
        if (originalSet.isEmpty()) {
            sets.add((Set<Integer>) new HashSet<Integer>());
            return sets;
        }
        List<Integer> list = new ArrayList<Integer>(originalSet);
        Integer head = list.get(0);
        Set<Integer> rest = new HashSet<Integer>(list.subList(1, list.size()));
        for (Set<Integer> set : powerSet(rest)) {
            Set<Integer> newSet = new HashSet<Integer>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        return sets;
    }
	
	
	// test kNN
	public void runKNN(int k, Instances train_set, Instances eval_set) throws Exception{
		IBk model = new IBk(k);
		String[] options = {"-F","-W", "20", "-E"};
		model.setOptions(options);
		model.buildClassifier(train_set);
		Evaluation myeval = new Evaluation(eval_set);
		myeval.evaluateModel(model, eval_set);
		myeval.crossValidateModel(model, eval_set,10, new Random(1));
		System.out.println(myeval.toSummaryString("\n=================\nResults-1 kNN\n=================\n", false));
		System.out.println(myeval.toClassDetailsString("=================\nResults-2 kNN\n=================\n"));
  		//----------------------------------------	
        System.out.println(myeval.fMeasure(0));
        System.out.println(myeval.precision(0));
        System.out.println(myeval.recall(0));
        System.out.println(myeval.pctCorrect());
	}
	
	
	// attribute selection x classifier
	public Classifier selectAtt(Classifier cls){
		  AttributeSelectedClassifier classifier = new AttributeSelectedClassifier();
		  CfsSubsetEval eval = new CfsSubsetEval();
		  GreedyStepwise search = new GreedyStepwise();
		  search.setSearchBackwards(true);
		  classifier.setClassifier(cls);
		  classifier.setEvaluator(eval);
		  classifier.setSearch(search);
		  return classifier;
	}
	
	
	// bagging/boosting x classifier
	public Classifier baggingBoost(Classifier cls){
		  Bagging classifier = new Bagging();
		  classifier.setClassifier(cls);
		  classifier.setBagSizePercent(20); // 20% random sample (iterated 10 times)
		  return classifier;		
	}	
	
	
	//  majority vote 
	public void runEvalVote(Instances train_set, Instances eval_set, Classifier[] classifs, String type) throws Exception {
		
		// a. voting
		
		Vote vote = new Vote();
		if (type=="bag"){
			for (int i=0;i<classifs.length;i++){
				Classifier cls_new = baggingBoost(classifs[i]);
				classifs[i] = cls_new;
			}
			vote.setClassifiers(classifs);				
		}
		if (type=="sel"){
			for (int i=0;i<classifs.length;i++){
				Classifier cls_new = selectAtt(classifs[i]);
				classifs[i] = cls_new;
			}
			vote.setClassifiers(classifs);				
		}
		else{
			vote.setClassifiers(classifs);
		}
		
        // b. evaluation
		    	
        // selecting dependent variable
        int num = train_set.numAttributes()-1;
        // last of train set
        train_set.setClassIndex(num);
        // last of evaluation set
        eval_set.setClassIndex(num);
        
		// create evaluation
        Evaluation evalA = new Evaluation(eval_set);
        
        // rounding to 2 decimal places
        nf.setMaximumFractionDigits(2);  
        nf.setMinimumFractionDigits(2); 
        
        // n-fold cross-validation
        evalA.crossValidateModel(vote, eval_set, 10, new Random(1));
 
        // print results
        System.out.println(evalA.toSummaryString("\n=================\nResults-1 Vote\n=================\n",false));
        System.out.println(evalA.toClassDetailsString("=================\nResults-2 Vote\n=================\n"));
        
	}
		
	
	//  stacking
	public void runEvalStack(Instances train_set, Instances eval_set, Classifier[] classifs, String type) throws Exception {
		
		// a. stacking
		
		Stacking stack = new Stacking();		
		if (type=="bag"){
			for (int i=0;i<classifs.length;i++){
				Classifier cls_new = baggingBoost(classifs[i]);
				classifs[i] = cls_new;
			}
			stack.setClassifiers(classifs);
			stack.setMetaClassifier(baggingBoost(new J48()));					
		}
		if (type=="sel"){
			for (int i=0;i<classifs.length;i++){
				Classifier cls_new = selectAtt(classifs[i]);
				classifs[i] = cls_new;
			}
			stack.setClassifiers(classifs);
			stack.setMetaClassifier(selectAtt(new J48()));				
		}
		else{
			stack.setClassifiers(classifs);
			stack.setMetaClassifier(new J48());
		}
		
        // b. evaluation
		
    	System.out.println("\n******************************************");
    	System.out.println(" Running...  ");       
    	System.out.println("******************************************");
    	
        // selecting dependent variable
        int num = train_set.numAttributes()-1;
        // last of train set
        train_set.setClassIndex(num);
        // last of evaluation set
        eval_set.setClassIndex(num);
        
		// create evaluation
        Evaluation evalA = new Evaluation(eval_set);
        
        // rounding to 2 decimal places
        nf.setMaximumFractionDigits(2);  
        nf.setMinimumFractionDigits(2); 
        
        // n-fold cross-validation
        evalA.crossValidateModel(stack, eval_set, 10, new Random(1));
 
        // print results
        System.out.println(evalA.toSummaryString("\n=================\nResults-1 Stack\n=================\n",false));
        System.out.println(evalA.toClassDetailsString("=================\nResults-2 Stack\n=================\n"));
        
	}
	
	
	// run evaluation method
    public void runEval(Instances train_set, Instances eval_set, Classifier[] classifs) throws Exception {
            	    	
    	System.out.println("\n******************************************");
    	System.out.println(" Running...  ");       
    	System.out.println("******************************************");
    	
        // selecting dependent variable
        int num = train_set.numAttributes()-1;
        // last of train set
        train_set.setClassIndex(num);
        // last of evaluation set
        eval_set.setClassIndex(num);
          
        for (int i=0;i<=classifs.length;i++){
        	Classifier myclassA = classifs[i];
            myclassA.buildClassifier(train_set);
            Evaluation myevalA = new Evaluation(train_set);      
            myevalA.evaluateModel(myclassA, eval_set);
            //----------------------------------------
            System.out.println(myevalA.toSummaryString("\n=================\nResults-1 "+ classifs[i] +"\n=================\n",false));
            System.out.println(myevalA.toClassDetailsString("=================\nResults-2"+ classifs[i] +"Logit\n=================\n"));
            //---------------------------------------- 
            System.out.println(myevalA.fMeasure(0));
            System.out.println(myevalA.precision(0));
            System.out.println(myevalA.recall(0));
            System.out.println(myevalA.pctCorrect());
        }
  
        // run k nearest neighbors (k = 10)
		runKNN(10,train_set,eval_set);
       
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
    

    
} // End of the class //
