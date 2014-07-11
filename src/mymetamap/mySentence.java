package mymetamap;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TypedDependency;


//Auxiliary class to save NPs
public class mySentence{

	
	// a sentence is a collection of NPs
	public ArrayList<myNounPhrase> noun_phrases;
	public String sent;
	public int number;
	public Set<String> senlabels;
	
	// (morpho)syntatic info
	public List<CoreLabel> sentokens; // token stream
	public Tree parsing; // parse tree
	public List<TypedDependency> sendeps; // dependencies


}
