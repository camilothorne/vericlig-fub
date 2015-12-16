package wordnet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;

//Auxiliary class to save NPs
public class MySentence{
	
	
	// a sentence is a collection of NPs
	public ArrayList<MyNounPhrase> 		noun_phrases;
	// (morpho)syntatic info
	private List<CoreLabel> 			sentokens; 	// token stream
	private List<String> 				tokens; 	// lemmatized tokens
	private Tree 						parsing; 	// parse tree
	private int 						num;		// number
	
	
	// constructor
	public MySentence(List<CoreLabel> sentokens){
		this.tokens = new LinkedList<String>();
		this.sentokens = sentokens;
		for (CoreLabel label: sentokens){
			if(label.tag().equals("NN")){
				this.tokens.add(label.lemma());
			}
		}
	}
	
	
	public ArrayList<MyNounPhrase> getNoun_phrases() {
		return noun_phrases;
	}

	public void setNoun_phrases(ArrayList<MyNounPhrase> noun_phrases) {
		this.noun_phrases = noun_phrases;
	}

	public List<CoreLabel> getSentokens() {
		return sentokens;
	}

	public void setSentokens(List<CoreLabel> sentokens) {
		this.sentokens = sentokens;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}	
	
	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}

	public Tree getParsing() {
		return parsing;
	}

	public void setParsing(Tree parsing) {
		this.parsing = parsing;
	}

}
