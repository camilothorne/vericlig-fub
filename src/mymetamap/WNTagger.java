package mymetamap;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class WNTagger {
	
	// global field
	private static WordNetDatabase database;
	
	// constructor
	public WNTagger(){
		// setting the system path to the wn distrib 
		System.setProperty("wordnet.database.dir", "/opt/local/share/WordNet-3.0/dict");
		this.database = WordNetDatabase.getFileInstance();
	}
	
	// noun method
	public void exploreNoun(String s){
		
		NounSynset nounSynset; // synset
		NounSynset[] hyponyms; // hyponyms
		NounSynset[] hypernyms;	// hypernyms
		
		// computing + printing synsets, hyponyms, hypernyms
		Synset[] synsets = database.getSynsets(s, SynsetType.NOUN);
		for (int i = 0; i < synsets.length; i++) {
			
			nounSynset = (NounSynset)(synsets[i]); // noun synset
			hyponyms = nounSynset.getHyponyms();// noun hyponyms
			hypernyms = nounSynset.getHypernyms();// noun hypernyms
			
			// print synset + hypernyms + hyponyms
			System.out.println(nounSynset.getWordForms()[0] + 
	            ": " + nounSynset.getDefinition() + ") has " 
	    		+ hyponyms.length + " hyponyms and "
	    		+ hypernyms.length + " hyperyms");
		}
        System.out.print("................................................................\n");
	}
		
	// verb method
	public void exploreVerb(String s){
		
		VerbSynset verbSynset; // synset
		NounSynset[] topics; // hyponyms
		VerbSynset[] hypernyms;	// hypernyms
		//String[] frames; //frames
		Synset[] synsets; //synsets
		
		// computing + printing synsets, hyponyms, hypernyms
		synsets = database.getSynsets(s, SynsetType.VERB);
		for (int i = 0; i < synsets.length; i++) {
			
			verbSynset = (VerbSynset)(synsets[i]); // verb synset
			topics = verbSynset.getTopics();// # of verb topics
			hypernyms = verbSynset.getHypernyms();// # of verb hypernyms
			//frames = verbSynset.getSentenceFrames(); // verb frames
			
			// print synset + hypernyms
			System.out.println(verbSynset.getWordForms()[0] + 
	            ": (" + verbSynset.getDefinition() + ") has " 
	    		+ hypernyms.length + " hyperyms");
			
			// print frames
			//for (int j = 0; j < frames.length; j++){
			//	System.out.println(frames[j]);
			//}
			
			// print topics
			for (int j = 0; j < topics.length; j++){
				String[] wfs = topics[j].getWordForms();
				for (int k = 0; k < wfs.length; k++){
					System.out.print(wfs[k]+", ");
				}
				System.out.println();
			}
		}
        System.out.print("................................................................\n");
	}
	
}
