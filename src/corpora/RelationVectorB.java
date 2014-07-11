package corpora;


import java.util.ArrayList;


import mymetamap.RelationVector;
import mymetamap.mySentence;


// Instantiates RelationVector to an specific
// annotated corpus
// 
public class RelationVectorB extends RelationVector{
	
	
	// Fields
	private final ReadXMLFile file = new ReadXMLFile("/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/adjudicated.xml"); // corpus
	private static ArrayList<mySentence> mysents; // extracted sentences
	private static String mypath = "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/"; // file to save	
    private static String mytype = "relation.arff"; // file to save
    
    
    // Constructor
    public RelationVectorB(String name) throws Exception{    	
    	
    	// We call the superclass constructor
    	super(name, mysents, mypath, mytype);
    	
    }

}
