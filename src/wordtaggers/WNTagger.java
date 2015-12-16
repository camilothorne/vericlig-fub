package wordtaggers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import wordnet.MyLemmatizer;
import wordnet.MySense;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;

/**
 * 
 * @author camilo
 *
 */
public class WNTagger {
	
	
	// global fields
	private static WordNetDatabase database;
	private static final String Word_Net3 = "/home/camilo/nlp-semantics/WordNet/WordNet-3.0/dict";
	
	
	// constructor
	public WNTagger(){
		
		// setting the system path to the wn distrib 
		System.setProperty("wordnet.database.dir", Word_Net3);
		this.database = WordNetDatabase.getFileInstance();
		
	}
	
	
	// noun method
	public Set<MySense> annotateNoun(String s, MyLemmatizer lemmatizer){
		
		//exploreNoun(s.trim());

		// computing
		Synset[] synsets = database.getSynsets(s.trim(), SynsetType.NOUN);
		
		// init senses
		Set<MySense> senses = new HashSet<MySense>();
		
		NounSynset nounSynset; // synset
		String gloss; //glosses
		String[] examples; // examples	

		for (int i = 0; i < synsets.length; i++) {
			
			nounSynset = (NounSynset)(synsets[i]); // noun synset
			gloss = nounSynset.getDefinition();// definition
			examples = nounSynset.getUsageExamples(); // examples
			
			// collect senses
			MySense sense = new MySense(s,nounSynset,gloss, examples, lemmatizer);
			
			//System.out.println("\t" + s);
			//System.out.println("\t" + gloss);
			//System.out.println("\t" + nounSynset.getWordForms()[0]);
			
			senses.add(sense);
			
		}
		
		// return senses
		return senses;
        
	}	
	
	
	// print nouns method
	public void exploreNoun(String s){
		
		NounSynset nounSynset; // synset
		NounSynset[] hyponyms; // hyponyms
		NounSynset[] hypernyms;	// hypernyms
		String gloss; //glosses
		
		// computing + printing synsets, hyponyms, hypernyms
		Synset[] synsets = database.getSynsets(s, SynsetType.NOUN);
        System.out.print("\n----------------------------------------------------------------\n");
        System.out.println("Noun '" + s + "' has " + synsets.length + " senses:"); 
        System.out.print("----------------------------------------------------------------\n");
		for (int i = 0; i < synsets.length; i++) {
			
			nounSynset = (NounSynset)(synsets[i]); // noun synset
			hyponyms = nounSynset.getHyponyms();// noun hyponyms
			hypernyms = nounSynset.getHypernyms();// noun hypernyms
			gloss = nounSynset.getDefinition();// definition
			
			// print synset + hypernyms + hyponyms
			System.out.println(
				nounSynset.getWordForms()[0]
	            + " : " + gloss + " ==> has " 
	    		+ hyponyms.length + " hyponyms and "
	    		+ hypernyms.length + " hyperyms"
	    		);
			
		}
        System.out.print("----------------------------------------------------------------\n");
        
	}
	
		
	// print verbs method
	public void exploreVerb(String s){
		
		VerbSynset verbSynset; // synset
		NounSynset[] topics; // topics
		VerbSynset[] hypernyms;	// hypernyms
		Synset[] synsets; //synsets		
		String gloss; //glosses		
		
		// computing + printing synsets, hyponyms, hypernyms
		synsets = database.getSynsets(s, SynsetType.VERB);
        System.out.print("\n----------------------------------------------------------------\n");
        System.out.println("Verb '" + s + "' has " + synsets.length + " senses:"); 
        System.out.print("----------------------------------------------------------------\n");
		for (int i = 0; i < synsets.length; i++) {
			
			verbSynset = (VerbSynset)(synsets[i]); // verb synset
			topics = verbSynset.getTopics();// # of verb topics
			hypernyms = verbSynset.getHypernyms();// # of verb hypernyms
			gloss = verbSynset.getDefinition();//
			
			// print synset + hypernyms
			System.out.println(
				verbSynset.getWordForms()[0] 
				+ " : " + gloss + " ==> " 
	    		+ hypernyms.length + " hypernyms"
	    		);
			
			// print topics
			for (int j = 0; j < topics.length; j++){
				String[] wfs = topics[j].getWordForms();
				for (int k = 0; k < wfs.length; k++){
					System.out.print(wfs[k]+", ");
				}
				System.out.println();
			}
		}
		
        System.out.print("----------------------------------------------------------------\n");
	}
	
	
	// main method (test)
	public static void main(String[] args){
		String test1 = "therapy";
		String test3 = "give";
		WNTagger wn = new WNTagger();
		wn.exploreNoun(test1);
		wn.exploreVerb(test3);
	}

	
}
