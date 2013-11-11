package tolearn;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.zip.*;

import cc.mallet.optimize.*;
import cc.mallet.fst.*;
import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.pipe.tsf.*;
import cc.mallet.types.*;
import cc.mallet.util.*;

public class TrainCRF {
	
	// constructor
	public TrainCRF(String trainingFilename, String testingFilename) throws IOException {
		
		ArrayList<Pipe> pipes = new ArrayList<Pipe>();

		// 3-grams
		int[][] conjunctions = new int[2][];
		conjunctions[0] = new int[] { -1 };
		conjunctions[1] = new int[] { 1 };
       
		// pipes
		
		pipes.add(new SimpleTaggerSentence2TokenSequence());
		//pipes.add(new CharSequence2TokenSequence());
        //pipes.add(new Target2Label());
		pipes.add(new OffsetConjunctions(conjunctions));
		pipes.add(new FeaturesInWindow("PREV-", -1, 1));
		//pipes.add(new TokenTextCharSuffix("C1=", 1));
		//pipes.add(new TokenTextCharSuffix("C2=", 2));
		//pipes.add(new TokenTextCharSuffix("C3=", 3));		
        pipes.add(new TokenSequenceRemoveStopwords(true, true));		
		//pipes.add(new RegexMatches("CAPITALIZED", Pattern.compile("^\\p{Lu}.*")));
		//pipes.add(new RegexMatches("STARTSNUMBER", Pattern.compile("^[0-9].*")));
		//pipes.add(new RegexMatches("HYPHENATED", Pattern.compile(".*\\-.*")));
		//pipes.add(new RegexMatches("DOLLARSIGN", Pattern.compile(".*\\$.*")));
		//pipes.add(new TokenFirstPosition("FIRSTTOKEN"));

		pipes.add(new TokenSequence2FeatureVectorSequence());
		
		Pipe pipe = new SerialPipes(pipes);
		
		InstanceList trainingInstances = new InstanceList(pipe);
		InstanceList testingInstances = new InstanceList(pipe);
		
		trainingInstances.addThruPipe(new 
				LineGroupIterator(new 
						BufferedReader(new 
								InputStreamReader(new 
												FileInputStream(trainingFilename))), 
												Pattern.compile("^\\s*$"), true));

		testingInstances.addThruPipe(new 
				LineGroupIterator(new 
						BufferedReader(new 
								InputStreamReader(new
												FileInputStream(testingFilename))), 
												Pattern.compile("^\\s*$"), true));

		//crf.addStatesForLabelsConnectedAsIn(trainingInstances);
		//crf.addStatesForThreeQuarterLabelsConnectedAsIn(trainingInstances);
		//crf.addStartState();
		
	    // model
	    CRF crf = new CRF(trainingInstances.getTargetAlphabet(),testingInstances.getTargetAlphabet());
	    
	    // construct the finite state machine
	    crf.addFullyConnectedStatesForLabels();
	    
	    // initialize model's weights
	    crf.setWeightsDimensionAsIn(trainingInstances, false);
	      
	    // CRF trainer (threaded)
	    CRFTrainerByThreadedLabelLikelihood crfTrainer =
	          new CRFTrainerByThreadedLabelLikelihood(crf, 32);

	    // *Note*: labels can also be obtained from the target alphabet
	    String[] labels = new String[]{"I-PER", "I-LOC", "I-ORG", "I-MISC"};
	    
	    // evaluators
	    TransducerEvaluator evalC = new MultiSegmentationEvaluator(
	          new InstanceList[]{trainingInstances, testingInstances},
	          new String[]{"train", "test"}, labels, labels) {
	        @Override
	        public boolean precondition(TransducerTrainer tt) {
	          // evaluate model every k == 0 training iterations
	          return tt.getIteration() % 5 == 0;
	        }
	    };		
		TransducerEvaluator evalA = new PerClassAccuracyEvaluator(testingInstances, "testing");
		//TransducerEvaluator evalB = new PerClassAccuracyEvaluator(trainingInstances, "training");

	    // 1. train 10 times
	    crfTrainer.train(trainingInstances, 100);
		// kill all threads
	    crfTrainer.shutdown();
	    
	    // 2. train until convergence (very slow)
	    //crfTrainer.setMaxResets(0);
	    //crfTrainer.train(trainingInstances, Integer.MAX_VALUE);

	    // add evaluator(s)
		//crfTrainer.addEvaluator(evalC);		
		crfTrainer.addEvaluator(evalA);
		//crfTrainer.addEvaluator(evalB);
	    
		// evaluate
		//evalC.evaluate(crfTrainer);
		evalA.evaluate(crfTrainer);
		//evalB.evaluate(crfTrainer);
		
	}

	public static void main (String[] args) throws Exception {
		
		String my_train = "/home/camilo/mallet-eval/chunking/eng-train.txt";
		String my_test = "/home/camilo/mallet-eval/chunking/eng-test.txt";
		
		new TrainCRF(my_train, my_test);

	}

}
