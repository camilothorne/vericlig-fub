package corpora;


import java.util.ArrayList;
import corpora.ReadXMLFile;
import features.RelationVector;


import mymetamap.mySentence;


// Instantiates RelationVector to an specific
// annotated corpus
// 
public class Relation extends RelationVector{
	
	
	// Fields
	private final ReadXMLFile file = new ReadXMLFile("/home/camilo/meta-map-exp/adjudicated.xml"); // corpus
	private static ArrayList<mySentence> mysents; // extracted sentences
	private static String mypath = "/home/camilo/meta-map-exp/"; // file to save	
    private static String mytype = "relation.arff"; // file to save
    
    
    // Constructor
    public Relation(String name) throws Exception{    	
    	
    	// We call the superclass constructor
    	super(name, mysents, mypath, mytype);
    	
    }

}
