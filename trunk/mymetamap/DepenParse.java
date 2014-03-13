package mymetamap;

//java libs
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Date;
import java.util.Set;
import java.io.StringReader;

import tests.MMTagger;


//stanford dep parser + pos/sem tags
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.DocumentPreprocessor;
import edu.stanford.nlp.ling.CoreLabel;  
import edu.stanford.nlp.ling.HasWord;  
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.process.Morphology;


//dep tree viewer
import com.chaoticity.dependensee.*;


public class DepenParse {
	
	
	// fields
	static MMTagger tagg;
	static WNTagger mywn;
	static Morphology mor;
	static TreebankLanguagePack tlp = new PennTreebankLanguagePack();
	static GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
	static Tree parse;
	static LexicalizedParser lp;
	
	
	//constructor
	public DepenParse(){
		tagg = new MMTagger();	// call MetaMap annotator
		mywn = new WNTagger();	//call WordNet
		mor = new Morphology(); //call stemmer/lemmatizer
		tlp = new PennTreebankLanguagePack();    // initialize treebank
		gsf = tlp.grammaticalStructureFactory(); // initialize dependency parser
		// loading language model + parsing options
		lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
		lp.setOptionFlags("-maxLength", "80", "-outputFormat", "typedDependenciesCollapsed");
	}

	
	// parsing string(s)
	public void demoAPI(String sent, String type) 
		  throws Exception{
 
		// parsing		
		TokenizerFactory<CoreLabel> tokenizerFactory = 
				PTBTokenizer.factory(new CoreLabelTokenFactory(), "");// initialize tokenizer
		List<CoreLabel> rawWords = 
				tokenizerFactory.getTokenizer(new StringReader(sent)).tokenize(); // call tokenizer
		
		parse = lp.apply(rawWords); // call parser
		
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse); // call dependency parser   
		
		/*
		// printing results to stout			
 		System.out.println("\n################################################################");
		System.out.println("################################################################");
		System.out.println("#");
		System.out.println("# NEW SENTENCE ");
		System.out.println("#");
		System.out.println("################################################################");
		System.out.println("################################################################\n");
		System.out.println(sent);// print sentence
		*/
				
		System.out.println("\n################################################################\n");
		if (type == "Depen"){
			List<TypedDependency> tdl = gs.typedDependenciesCCprocessed(); // transform trees into dependency lists
			TreePrint tp = new TreePrint("typedDependencies",tlp);
			tp.printTree(parse.flatten()); //dependencies
			System.out.println("################################################################\n");
			//System.out.println(tdl.toString()); //dependencies (list)
			//System.out.println("################################################################");
		}
		if (type == "Penn"){
			parse.pennPrint(); // parse (constituency) tree (unflattened)
			System.out.println("\n################################################################\n");
		}
		if (type == "flatten"){
			System.out.println(parse); // parse tree (flattened) 
			System.out.println("################################################################");
		}
		if (type == "yield"){
			System.out.println(parse.taggedYield()); // tree yield  
			System.out.println("\n################################################################\n");
		}
		
		/*		
 		// annotate nouns and verbs with WordNet
	    ArrayList<TaggedWord> yield = parse.taggedYield();
	    for (TaggedWord t: yield){
	    	if (t.tag().matches("NN")){
				System.out.println("\n----------------------------------------------------------------");
	    		System.out.println("( " + mor.stem(t.word()) + " , " + t.tag() + " ) ");
				System.out.println("----------------------------------------------------------------\n");
	    		mywn.exploreNoun(mor.stem(t.word()));
	    	}
	    	if (t.tag().matches("VB|VBN")){
				System.out.println("\n----------------------------------------------------------------");
		    	System.out.println("( " + mor.stem(t.word()) + " , " + t.tag() + " ) ");
				System.out.println("----------------------------------------------------------------\n");
	    		mywn.exploreVerb(mor.stem(t.word()));
	    	}
	    }
	 
		
		// semantic tags
		for (CoreLabel lab: rawWords){
			tagg.processString(lab.originalText());
		}	
 
		// writing the dependency tree to a graphic file
		String prefix = getMyDate();
		Main.writeImage(parse,tdl, "/home/camilo/Desktop/depen" + prefix + ".jpg",3);
		*/
	
	}
	
	
	// annotate NP, NN and Verb constituents
	public void annotateTree(Tree myparse){ 
		Set<Tree> sub = myparse.subTrees();
		for (Tree tree: sub){
			
			// NPs
			if (tree.label().value().matches("NP")){ // all NPs
				String np = "";
				ArrayList<TaggedWord> words = tree.taggedYield();
				System.out.println("----------------------------------------------------------------");
				System.out.println("( complex phrase, NP )");
				for (Word wor: words){
					np = np + " " + wor.toString().replaceAll("/.*", "");
				}
				System.out.println(np);	
				System.out.println("----------------------------------------------------------------");	
				tagg.processString(np,0);// metamap annotation
			}
			
			//Nouns
			if (tree.label().value().matches("NN")){ // only NNs
				ArrayList<TaggedWord> words = tree.taggedYield();
				for (TaggedWord wor: words){
					System.out.println("----------------------------------------------------------------");
					System.out.println("( " + mor.stem(wor.word()) + " , " + wor.tag() + " ) ");
					System.out.println("----------------------------------------------------------------");						
					//mywn.exploreNoun(mor.stem(wor.word())); // wn annotation
					tagg.processString(mor.stem(wor.word()),0); //metamap annotation
				}
			}
			
			// Verbs
			if (tree.label().value().matches("(VB|VBN)")){// base forms (act) + participles (pass)
				ArrayList<TaggedWord> words = tree.taggedYield();
				for (TaggedWord wor: words){
					System.out.println("----------------------------------------------------------------");
					System.out.println("( " + mor.stem(wor.word()) + " , " + wor.tag() + " ) ");
					System.out.println("----------------------------------------------------------------");						
					//mywn.exploreVerb(mor.stem(wor.word())); // wn annotation
					tagg.processString(mor.stem(wor.word()),0); //metamap annotation
				}
			}
			
		}
	}
	
	
	// getting the date
	public static String getMyDate(){
		String mydate = "";
		Date tm = new Date();
		mydate = tm.toString();
		return mydate;
	}
	
	
/*	
 	// parsing file(s)
	public static void demoDP(LexicalizedParser lp, String filename) 
		  throws Exception{
 
		// this option shows loading and sentence-segment and tokenizing
		// a file using DocumentPreprocessor
		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
 
		// you could also create a tokenizer here (as below) and pass it
		// to DocumentPreprocessor
		for (List<HasWord> sentence : new DocumentPreprocessor(filename)) {
			Tree parse = lp.apply(sentence);
			System.out.println(); 
			parse.pennPrint();
			System.out.println();     
			GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
			Collection tdl = gs.typedDependenciesCCprocessed(true);
			System.out.println(tdl);
			System.out.println();
		}
	}
*/
	
	
/*	// main method
	public static void main(String[] args) throws Exception, 
		ClassNotFoundException {
		
		// we start the parser + annotators
		DepenParse myp = new DepenParse();
 	  
		// some sample sentences   
		String mysent3 = "If adjuvant chemotherapy is indicated following breast-conserving "+
						 "surgery, RT should typically be given after chemotherapy is completed.";
		String mysent1 = "Emphasise advice on healthy balanced eating that is applicable to "+
						 "the general population when providing advice to people with type 2 diabetes.";
		String mysent2 = "Encourage high-fibre, low-glycaemic-index sources of "+
						 "carbohydrate in the diet, such as fruit, vegetables, wholegrains and "+
						 "pulses; include low-fat dairy products and oily fish; and control the "+
						 "intake of foods containing saturated and trans fatty acids).";
		String mysent4 = "Emphasise advice on healthy balanced eating that is applicable to the "+
						 "general population when providing advice to people with type 2 diabetes.";
		String mysent0 = "Continue with metformin if blood glucose control remains inadequate "+
						 "and another oral glucose-lowering medication is added.";
		// we put them in an array
		//String[] samples = new String[] {mysent1,mysent2,mysent3};
		String[] samples = new String[] {mysent0,mysent4};
   	 
		// parse string(s)
		for (String sam: samples){
			
			// parse
			myp.demoAPI(sam, "Depen");
			// annotate
			myp.annotateTree(parse);
			
		}
 
	}*/

}

