package wordnet;

//import java.util.ArrayList;
import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;

import edu.smu.tspell.wordnet.Synset;

//Auxiliary class to mine NP features
public class MyNounPhrase{

	
	private String 				phrase;			// phrase
	private List<String> 		context; 		// sentence context (tokenized)
	private List<MySense> 		synsets; 		// synsets
	private MySense 			synset;			// WSD sense
	
	
	public MySense getSynset(){
		return synset;
	}

	public void setSynset(MySense synset){
		this.synset = synset;
	}	
	
	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}

	public List<String> getContext() {
		return context;
	}

	public void setContext(List<String> context) {
		this.context = context;
	}

	public List<MySense> getSynsets() {
		return synsets;
	}

	public void setSynsets(List<MySense> synsets) {
		this.synsets = synsets;
	}
		
}
