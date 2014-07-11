package mymetamap;


import java.util.ArrayList;
import java.util.Set;


//Auxiliary class to mine NP features
public class myNounPhrase{

	
	// main fields
	public String phrase;
	public int sentence;
	public int position;
	public ArrayList<String> labels; // MetaMap labels
	public ArrayList<String> simp_labels; // MetaMap simple labels
	public int nesting_level;
	public String role;
	
	// dependencies
	public Set<String> depens;
	
	// its head is an NP
	public myNounPhrase nphead;
		
	// to be extracted from ann. corpus
	public String class_label;
	
	// to be extracted from ann. corpus
	public String relation_label;
	
	
}
