package mymetamap;


//Java API
import java.io.IOException;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;


//Stanford NLP API
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.CollinsHeadFinder;
import edu.stanford.nlp.ling.TaggedWord;


//NLM java APIs
import gov.nih.nlm.nls.metamap.*;


//======================
// Main annotation class
//======================


// Collect/display MMTx, syntactic and POS annotations
public class GuidAnn implements MyFeature{

	
		  // Public static fields
		  public static TreebankLanguagePack tlp; // treebank functions
		  public static GrammaticalStructureFactory gsf; // dependency parsing functions
		  public static LexicalizedParser lp; // stanford parser
		  public static TreePrint tp; // tree print
		  public static CollinsHeadFinder head; // head finder 
		  public static MetaMapApi api; // MetaMap query	
		  public static String optssim; // simple MetaMap server options
		  public static String opts; // complex MetaMap server options
		  
		  
		  // Dynamic field(s)
		  public BufferedReader file; // file to read
		  public String path; // path to file
		  public Tree parse; // parse tree
		  public List<TypedDependency> tdl; // dependencies
		  public GrammaticalStructure gf; // grammatical relations
		  public ArrayList<String> sents; // sentences to parse
		  public ArrayList<mySentence> mycorpus; // storing NPs

		  
		  // Generic MetaMap query constructor
		  public GuidAnn() {
			  api = new MetaMapApiImpl();
		  }
		  
		  
		  // Constructs a new MetaMap query using specified host and port
		  // It also constructs a parse
		  public GuidAnn(String serverHostname, int serverPort, String path) throws Exception{
			  api = new MetaMapApiImpl();
			  api.setHost(serverHostname);
			  api.setPort(serverPort);
			  this.path = path;
			  // new buffer
			  this.file = null;
			  // Stanford tagger
			  tlp = new PennTreebankLanguagePack();
			  gsf = tlp.grammaticalStructureFactory();
			  // loading language model + parsing options
			  lp = LexicalizedParser.loadModel("edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz");
			  lp.setOptionFlags("-maxLength", "80", "-outputFormat", "typedDependenciesCollapsed");
			  //head finder
			  head = new CollinsHeadFinder(tlp);
			  // open raw guideline
			  if (path != ""){
				  this.sents = this.readFile(path,file);
				  this.processLines(this.sents);
			  }
			  // set MetaMap options
			  opts = "--ignore_word_order --all_derivational_variants --threshold 10" + 
		  		   		 " --all_acros_abbrs --word_sense_disambiguation --WSD localhost --silent" +
		  		   		 " --ignore_stop_phrases --composite_phrases 3 --tagger localhost";
			  optssim = "-a -r10 -y -o";
			  this.mycorpus = new ArrayList<mySentence>();
		  }
		  
		  
		  //--------------------------
		  // A. Common, shared methods
		  //--------------------------
		  
		  
		  // Timout method
		  public void setTimeout(int interval) {
			  api.setTimeout(interval);
		  }
		  
		 
		  // Converting file to list of sentences
		  // (one per line, otherwise, use sentence segmentator)
		  public ArrayList<String> readFile(String filepath, BufferedReader file){
			   ArrayList<String> result = new ArrayList<String>();
			   try {
					String sCurrentLine;
					file = new BufferedReader(new FileReader(filepath));
					while ((sCurrentLine = file.readLine()) != null) {
						result.add(sCurrentLine);
					}	 
				} 
			   catch (IOException ex) {
					ex.printStackTrace();
				} 
			   finally {
					try {
						if (file != null)
							file.close();
					} 
					catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			   return result;
		  }
		   
		   
		  // Sentence/line tokenizer
		  public List<CoreLabel> myTokenize(String sent){
				TokenizerFactory<CoreLabel> tokenizerFactory = 
						PTBTokenizer.factory(new CoreLabelTokenFactory(), "");// initialize tokenizer
				List<CoreLabel> rawWords = 
						tokenizerFactory.getTokenizer(new StringReader(sent)).tokenize(); // run tokenizer
				return rawWords;
		  }
		   
		  
		  //--------------------
		  // B. Display on stout
		  //--------------------
		  
		  
		  // Process terms using MetaMap API and display result to standard output.
		  public void process(String terms, int num, PrintStream out, List<String> serverOptions) 
			throws Exception{
	  		//Set server options
			if (serverOptions.size() > 0) {
				 api.setOptions(serverOptions);
		    }		    		    
		    //Retrieve the results
		    List<Result> resultList = api.processCitationsFromString(terms);
		    for (Result result: resultList) {
		      if (result != null) {
		    	out.println("============================================================");
		    	out.println("PHRASE : " + result.getInputText());
				out.println("============================================================");
		    	//Retrieve list of known acronyms
		    	List<AcronymsAbbrevs> aaList = result.getAcronymsAbbrevsList();
				if (aaList.size() > 0) {
				  out.println("Acronyms and Abbreviations : ");
				  for (AcronymsAbbrevs e: aaList) {
				    out.println("\nAcronym: " + e.getAcronym());
				    out.println("Expansion: " + e.getExpansion());
				    out.println("Count list: " + e.getCountList());
				    out.println("CUI list: " + e.getCUIList());
				  }
				}			
				//Retrieve list of negated concepts
				List<Negation> negList = result.getNegationList();			
				if (negList.size() > 0) {
				  out.println("Negations:");
				  for (Negation e: negList) {
				    out.println("type: " + e.getType());
				    out.print("Trigger: " + e.getTrigger() + ": [");
				    for (Position pos: e.getTriggerPositionList()) {
				      out.print(pos  + ",");
				    }
				    out.println("]");
				    out.print("ConceptPairs: [");
				    for (ConceptPair pair: e.getConceptPairList()) {
				      out.print(pair + ",");
				    }
				    out.println("]");
				    out.print("ConceptPositionList: [");
				    for (Position pos: e.getConceptPositionList()) {
				      out.print(pos + ",");
				    }
				    out.println("]");
				  }
				}				
				//Retrieve complex utterances
				for (Utterance utterance: result.getUtteranceList()) {
				  int count = 0;
				  int parse = 0;
				  int mapp = 0;
				  for (PCM pcm: utterance.getPCMList()) {
					parse = parse + 1;
				    for (Ev ev: pcm.getCandidateList()) {
				      count = count + 1;
				      out.println("------------------------------------------------------------");
				      out.println("Candidate " + count + " : ");
				      out.println("------------------------------------------------------------");
				      out.println("  Concept Name: " + ev.getConceptName());
				      out.println("  Preferred Name: " + ev.getPreferredName());
				      out.println("  Semantic Types: " + recoverSemType(ev.getSemanticTypes().toString(),0));
				    }
				    out.println("============================================================");
				    for (Mapping map: pcm.getMappingList()) {
				    	mapp = mapp + 1;
				    	for (Ev mapEv: map.getEvList()) {
					      out.println("------------------------------------------------------------");
					      out.println("Mapping " + mapp + " : ");
					      out.println("------------------------------------------------------------");
					      out.println("   Concept Name: " + mapEv.getConceptName());
					      out.println("   Preferred Name: " + mapEv.getPreferredName());
					      out.println("   Semantic Types: " + recoverSemType(mapEv.getSemanticTypes().toString(),0));
					   }
					}
				  }
				}
		      } 
		      else{
			   out.println("NULL result instance! ");
		      }
		    }
		    api.resetOptions();
		  }		  
		   
		   
		   // Tag sets of sentences
		   public void processLines(ArrayList<String> samples) throws Exception{			   
			   int num = 0; // counter
			   PrintStream myoutput = System.out; // print to stout			   		   
			   List<String> myoptions = new ArrayList<String>(); // initialize options
			   myoptions.add(optssim);	// set simple options	  
			   myoptions.add(opts);	// set complex options		   			   
			   for (String samp: samples){
				   List<CoreLabel> tokens = this.myTokenize(samp);// call tokenizer
				   if ((tokens.size()>0)&&(num<3)){
					   System.out.println("============================================================");
					   System.out.println("SENT " + num + " : " + samp);
					   System.out.println("============================================================");
					   num = num + 1;
					   this.parse = lp.apply(tokens); // call/set parser
					   System.out.println();
					   this.parse.pennPrint(); // print parse tree
					   this.gf = gsf.newGrammaticalStructure(this.parse); // call dependency parser
					   this.tdl = this.gf.typedDependenciesCCprocessed(); // transform trees into dependency lists
					   TreePrint tp = new TreePrint("typedDependencies",tlp);
					   System.out.println();
					   tp.printTree(this.parse.flatten()); // print dependencies 
					   // annotate NPs, NNs and verbs with MetaMap
					   annotateTree(this.parse,num,myoutput,myoptions);
				   }				   	
			   }			   
		  }
		   

		  // Tag one sentence only
		  public void processLine(String sample) throws Exception{
			   PrintStream myoutput = System.out; // print to stout			   		   
			   List<String> myoptions = new ArrayList<String>(); // initialize options
			   myoptions.add(optssim);	// set simple options	  
			   myoptions.add(opts);	// set complex options		   			   
			   List<CoreLabel> tokens = this.myTokenize(sample);// call tokenizer
			   System.out.println("============================================================");
			   System.out.println("SAMP : " + sample);
			   System.out.println("============================================================");
			   this.parse = lp.apply(tokens); // set/call parser
			   System.out.println();
			   this.parse.pennPrint(); // print parse tree
			   this.gf = gsf.newGrammaticalStructure(this.parse); // call dependency parser
			   this.tdl = gf.typedDependenciesCCprocessed(); // transform trees into dependency lists
			   TreePrint tp = new TreePrint("typedDependencies",tlp);
			   System.out.println();
			   tp.printTree(this.parse.flatten()); // print dependencies
			   // annotate NPs and verbs with MetaMap
			   annotateTree(this.parse,0,myoutput,myoptions);
		  }
		   
		  
		  // Display NP and Verb annotations
		  public void annotateTree(Tree myparse, int num, PrintStream myoutput, List<String> myoptions) throws Exception
		  { 
				Set<Tree> sub = myparse.subTrees();
				filterNPs(sub);
				for (Tree tree: sub){					
					// NPs
					if (tree.label().value().matches("NP")){ // all NPs
						String np = "";
						ArrayList<TaggedWord> words = tree.taggedYield();
						for (Word wor: words){
							np = np + " " + wor.toString().replaceAll("/.*", "");
						}
						process(np, num, myoutput, myoptions); // metamap annotation
						System.out.println("............................................................");
						System.out.println("Head noun : " + head.determineHead(tree));
						System.out.println("............................................................");
					}					
					// Verbs
					if (tree.label().value().matches("(VB|VBN|VBD|VBG|VBP|VBZ)")){ // base(=act)+part.(=pass)
						ArrayList<TaggedWord> words = tree.taggedYield();
						for (TaggedWord wor: words){					
							process(wor.word(),num, myoutput, myoptions); //metamap annotation
						}
						System.out.println("............................................................");
						System.out.println("Head verb : " + head.determineHead(tree));
						System.out.println("............................................................");
					}
					
				}
		  }
		  

		  //------------------------
		  // C. Tree post-processing
		  //------------------------		  
		  
		  
		  // Filter out complex NPs
		  // (we want only non-recursive NPs)
		  public void filterNPs(Set<Tree> mytree){
			  Set<Tree> NPs = new HashSet<Tree>();
			  for (Tree tree: mytree){					
				if (tree.label().value().matches("NP")){
					NPs.add(tree);
				}
			  }
			  for (Tree tree1: NPs){
				  for (Tree tree2: NPs){
					  if((tree2.subTrees().contains(tree1))&&!(tree1.equals(tree2))){ // strict containment
						  mytree.remove(tree2);
					  }
				  }
			  }
		  }

		  
		  // Expand abbreviations
		  public String recoverSemType(String type, int typ){
			  Abbrev abb = new Abbrev();
			  if (typ == 1){
				  return abb.mySimpLabel(type);
				  }
			  if (typ == 2){
				  return abb.myLabel(type);
			  }else{
				  return abb.myFilter(type);
			  }
		  }
		  

		  // complex condition for NP filtering
		  public boolean filterCond(Tree tree, String text2){
			String np = "";
			ArrayList<TaggedWord> words = tree.taggedYield();
			for (Word wor: words){
				np = np + " " + wor.toString().replaceAll("/.*", "");
			}
			if (np.matches(".*"+text2+".*")){
				return true;
			}
			else{
				return false;
			}
		  }
		  
		  
		  //-----------------------
		  // D. Annotation recovery
		  //-----------------------
		  

		   // Mine sets of sentences
		   public void mineLines(ArrayList<String> samples) throws Exception{			   			   
			   // init counter
			   int num = 0;		   		   
			   // set options
			   List<String> myoptions = new ArrayList<String>(); // initialize options
			   myoptions.add(optssim);	// set simple options	  
			   myoptions.add(opts);	// set complex options		   			   			   
			   // loop on sentences
			   for (String samp: samples){				   
				   // call tokenizer
				   List<CoreLabel> tokens = this.myTokenize(samp);				   				   
				   // call Penn parser
				   this.parse = lp.apply(tokens);				
				   // call dependency parser
				   this.gf = gsf.newGrammaticalStructure(this.parse);			   
				   // transform current tree to dependency lists
				   this.tdl = this.gf.typedDependenciesCCprocessed();
				   // mine NPs with MetaMap + parser
				   mySentence sen = mineTree(this.parse,samp,num,myoptions);
				   // save parse(s)
				   sen.sentokens = tokens;
				   sen.parsing 	 = this.parse;
				   sen.sendeps	= this.tdl;
				   this.mycorpus.add(sen);			   
				   // increase counter
				   num = num + 1;
			   }
		  }
		   

		  // Mine one sentence only
		  public void mineLine(String sample) throws Exception{		  
			   // set options
			   List<String> myoptions = new ArrayList<String>(); // initialize
			   myoptions.add(optssim);	// set simple options	  
			   myoptions.add(opts);	// set complex options		   
			   // call tokenizer
			   List<CoreLabel> tokens = this.myTokenize(sample);// call tokenizer
			   // set/call parser
			   this.parse = lp.apply(tokens); // set/call parser	
			   // call dependency parser
			   this.gf = gsf.newGrammaticalStructure(this.parse); // call dependency parser			   
			   // transform trees into dependency lists
			   this.tdl = this.gf.typedDependenciesCCprocessed();
			   // mine NPs with MetaMap + parser
			   mySentence sen = mineTree(this.parse,sample,0,myoptions);
			   // save parse(s)
			   sen.sentokens = tokens;
			   sen.parsing 	 = this.parse;
			   sen.sendeps	 = this.tdl;
			   this.mycorpus.add(sen);
		  }		  
		  
		  
		  //-----------------------
		  // D.1 Annotation methods
		  //-----------------------
		  
		  
		  // mine NP annotations (main annotation method)
		  //
		  // N.B. this method extracts/sets all the NP
		  // features with the exception of the concept
		  // label (to which it assigns a ?), within a
		  // sentence, and, if applicable, the relation(s)
		  // in which it participates
		  //
		  public mySentence mineTree(Tree myparse, String sent, int num, List<String> myoptions) throws Exception
		  { 
			    // intialize sentence
			  	mySentence sen = new mySentence();
			  	// set sentence text
			  	sen.sent = sent;
			  	// set sentence number
			  	sen.number = num;
			  	// init sentence NPs
			  	sen.noun_phrases = new ArrayList<myNounPhrase>();
			  	// return all subtrees
				Set<Tree> sub = myparse.subTrees();
				// filter non basic NPs 
				filterNPs(sub);		
				// init counter for NPs
				int counter = 0;			
				// loop over sentence constituents
				for (Tree tree: sub){
					// NPs
					if (tree.label().value().matches("NP")){ // all NPs
						// mine NP
						myNounPhrase mynp = mineNP(sen,tree,myparse,counter,num,sub,myoptions);
						// save NP
						sen.noun_phrases.add(mynp);
						// increase counter and continue
						counter = counter + 1;	
					}					
				}
				// set sentence labels
				sen.senlabels = mySenLabels(sen);
				// return sentence
				return sen;
		  }
		  

		  // mine selected NP
		  //
		  // N.B. this method modifies only NP objects
		  // and not S objects, and assigns to the NP
		  // a *class* label
		  //
		  public void mineSelTree(Tree myparse, mySentence sen, 
				  List<String> myoptions, String text_cond, 
				  String clabel, String relation, int counter) throws Exception
		  { 
				Set<Tree> sub = myparse.subTrees();
				// filter non basic NPs 
				filterNPs(sub);		
				// loop over sentence constituents
				for (Tree tree: sub){
					// NPs
					// filtered by complex conditions
					if (tree.label().value().matches("NP") & filterCond(tree,text_cond)){
						// mine NP
						myNounPhrase mynp = mineNP(sen,tree,myparse,counter,sen.number,sub,myoptions);
						// add class label
						mynp.class_label = clabel;
						mynp.relation_label = relation;
						
						//-------------------------------------------------------------------
						// testing NPs
						//
						System.out.println("depens: "		+mynp.depens);
						System.out.println("nest: "			+mynp.nesting_level);
						System.out.println("labs: "			+mynp.labels);
						System.out.println("labs2: "		+mynp.simp_labels);
						System.out.println("head labs: "	+mynp.nphead.labels);
						System.out.println("head: "			+mynp.nphead.phrase);
						System.out.println("role: "			+mynp.role);
						System.out.println("sen: "			+mynp.sentence);
						System.out.println("num: "			+mynp.position);
						System.out.println("label: "		+mynp.class_label);
						System.out.println("phrase:"		+mynp.phrase);
						System.out.println("relation: "		+mynp.relation_label);
		    			System.out.println("-----------------------------------------------");
		    			//
		    			//-------------------------------------------------------------------
		    			
						// save NP
						sen.noun_phrases.add(mynp);	
					}					
				}
		  }
		  
		  
		  // annotate single NPs
		  public myNounPhrase mineNP(mySentence sen, Tree tree, Tree myparse, int counter, int num,
				  Set<Tree> sub, List<String> myoptions
				  ) throws Exception{
				// set current (sub)tree
				Tree currtree = tree;			
				// init NP
				myNounPhrase my_np = new myNounPhrase();
				// harvest text
				String np = ""; // np phrase 
				ArrayList<TaggedWord> words = tree.taggedYield();
				for (Word wor: words){
					np = np + " " + wor.toString().replaceAll("/.*", "");	
				}
				// NP yield (words/phrase)
				my_np.phrase = np;
				// sentence number of NP
				my_np.sentence = num;
				// NP number within sentence
				my_np.position = counter;			
				// init MMTx annotations for NP
				harvestAnn(my_np, myoptions); // metamap annotations	
				// compute nesting of NP
				returnNesting(my_np, currtree, myparse, sub);			
				// determine role of NP
				returnPosition(my_np, currtree, myparse, sub);	
				// init head data of NP
				processHead(my_np, currtree, myoptions);
				// init class label
				setNPClass(my_np,"?");
				// init relation label
				setNPRel(my_np,"none");
				// init dependencies
				collectDepen(my_np);
				// return NP
				return my_np;
		  }
		  

		  // processes terms using MetaMap API and save annotations
		  public void harvestAnn(myNounPhrase np, List<String> serverOptions) 
			throws Exception{ 
	  		// set server options
			if (serverOptions.size() > 0) {
				 api.setOptions(serverOptions);
		    }
			// list to save annotations
			ArrayList<String> anns = new ArrayList<String>();
			ArrayList<String> simpanns = new ArrayList<String>();
			// retrieve the results
		    List<Result> resultList = api.processCitationsFromString(np.phrase);	    
		    // loop over results
		    for (Result result: resultList) {		      
		    	// check for emptyness	
		    	if (result != null) {	    	  
		    		// loop over complex utterances
		    		for (Utterance utterance: result.getUtteranceList()) {
		    			for (PCM pcm: utterance.getPCMList()) {
		    				for (Ev ev: pcm.getCandidateList()) {	      
		    					// collect semantic types 
		    					anns.add(recoverSemType(ev.getSemanticTypes().toString(),0));
		    					simpanns.add(recoverSemType(ev.getSemanticTypes().toString(),1));
		    				}
		    			}
		    		}
		    	}
		    	// if empty return warning
		    	else{
		    		System.out.println("NULL result instance! ");
		    	}
		    }		    
		    //reset server
		    api.resetOptions();		    
		    // set annotations
		    np.labels = anns;
		    np.simp_labels = simpanns;
		  }
		  
		  
		  // sets the label of the NP
		  public void setNPClass(myNounPhrase np, String label){
			  np.class_label = label;
		  }
		  
		  
		  // sets the relation of the NP
		  public void setNPRel(myNounPhrase np, String label){
			  np.relation_label = label;
		  }
		  
		  // computes the level of nesting/subordination
		  public void returnNesting(myNounPhrase np, Tree currtree, Tree globaltree, Set<Tree> subtrees){
			  int nest = 0;
			  int depth = globaltree.depth();
			  while (depth > 0){  
				  Tree explore = currtree.ancestor(depth, globaltree);
				  if ((explore!=null) && (explore.label().value().matches("SBAR"))){
					  nest = nest + 1;
					  //System.out.println(explore.toString());
				  }
				  depth = depth - 1;
			  }			  
			  np.nesting_level = nest;
		  }
		  

		  // checks for the position of the NP in the tree (subj or obj)
		  public void returnPosition(myNounPhrase np, Tree currtree, Tree globaltree, Set<Tree> subtrees){
			  int depth = globaltree.depth();
			  np.role = "subj";
			  while (depth > 0){
				  Tree explore = currtree.ancestor(depth, globaltree);
				  if ((explore!=null) && (explore.label().value().matches("VP"))){
					  np.role = "obj";
					  //System.out.println(explore.toString());
				  }
				  depth = depth - 1;
			  }
		  }
		  
		  
		  // process heads in NP
		  public void processHead(myNounPhrase np, Tree currtree, 
				  List<String> myoptions) throws Exception{
			  myNounPhrase myhead = new myNounPhrase();
			  myhead.nphead = null; // the had of a head is null
			  myhead.nesting_level = np.nesting_level;
			  myhead.position = np.position;
			  myhead.role = np.role;
			  myhead.relation_label = np.relation_label;
			  myhead.sentence = np.sentence;
			  myhead.phrase = head.determineHead(currtree).toString();
			  myhead.class_label = "";
			  // try to harvest annotations for the head
			  harvestAnn(myhead, myoptions);
			  np.nphead = myhead;
		  }
		  
		  
		  // compute senlabels over corpus
		  public void senLabels(ArrayList<mySentence> sents){
			  for (mySentence sen : sents){
				  if (sen.senlabels == null){
					  Set<String> sen_labels = new HashSet<String>();
					  for (myNounPhrase np: sen.noun_phrases){
						  for (String label : np.labels){
							  sen_labels.add(label);
						  }
					  }
					  sen.senlabels = sen_labels;
				  }
			  }
		  }

		  
		  // compute senlabels of single sentence
		  public Set<String> mySenLabels(mySentence sen){
				  Set<String> sen_labels = new HashSet<String>();
				  for (myNounPhrase np: sen.noun_phrases){
					  for (String label : np.labels){
						  sen_labels.add(label);
					  }
				  }
				  return sen_labels;
		  }		  
		  
		  
		  // extract NP dependencies
		  public void collectDepen(myNounPhrase np){
			  Set<String> depen = new HashSet<String>();
			  for (TypedDependency td : this.tdl){
				  
				  String worA = td.dep().label().originalText();// arg1 of relation
				  
				  String worB = td.gov().label().originalText();// arg2 of relation
				  			  
				  if (np.nphead.phrase.matches(".*"+worA+".*") | np.nphead.phrase.matches(".*"+worB+".*")){
					 
					  //System.out.println("match!");
					  //System.out.println("rel : "+td.reln().toString());
					  
					  if (!td.reln().toString().equals("root")){
						  depen.add(td.reln().toString());
					  }
				  }
			  } 
			  np.depens=depen;
		  }
		  

//====================
// Auxiliary classes
//====================


//// Auxiliary class to mine NP features
//public class myNounPhrase{
//
//	
//	// main fields
//	public String phrase;
//	public int sentence;
//	public int position;
//	public ArrayList<String> labels; // MetaMap labels
//	public ArrayList<String> simp_labels; // MetaMap simple labels
//	public int nesting_level;
//	public String role;
//	
//	// dependencies
//	public Set<String> depens;
//	
//	// its head is an NP
//	public myNounPhrase nphead;
//		
//	// to be extracted from ann. corpus
//	public String class_label;
//	
//	// to be extracted from ann. corpus
//	public String relation_label;
//	
//	
//}


//// Auxiliary class to save NPs
//public class mySentence{
//
//	
//	// a sentence is a collection of NPs
//	public ArrayList<myNounPhrase> noun_phrases;
//	public String sent;
//	public int number;
//	public Set<String> senlabels;
//	
//	// (morpho)syntatic info
//	public List<CoreLabel> sentokens; // token stream
//	public Tree parsing; // parse tree
//	public List<TypedDependency> sendeps; // dependencies
//
//
//}


}