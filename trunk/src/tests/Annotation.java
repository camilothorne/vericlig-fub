package tests;


import corpora.RelationVectorB;
import mymetamap.FeatureVector;
import mymetamap.GuidAnn;
import gov.nih.nlm.nls.metamap.MetaMapApi;


// Class for running guideline annotations
public class Annotation {
	
	
	  // Main method
	  public static void main(String[] args) throws Exception{
		  //featureExtraction();
		  featureRelExtraction();
		  //printAnnotation();
	  }
	
	
	  // to display MetaMap + Stanford parser annotations
	  public static void printAnnotation() throws Exception{
		  //parameters
		  String serverhost = MetaMapApi.DEFAULT_SERVER_HOST; // default host
		  int serverport = MetaMapApi.DEFAULT_SERVER_PORT; 	// default port
		  int timeout = -1; // use default timeout		  
		  // we instantiate the class
		  String path = "/home/camilo/Desktop/mmap-exp-data/mmcorpus-raw.guid";
		  GuidAnn frontEnd = new GuidAnn(serverhost, serverport, path); // result object
		  // timeout
		  if (timeout > -1) {
		    frontEnd.setTimeout(timeout);
		  } 		
	  }
	 	
	
	  // to extract a set of observations from a
	  // training corpus
	  public static void featureExtraction() throws Exception{
		  // this class encapsulates the whole
		  // procedure
		  new FeatureVector("semrepB");
	  }

	  // to extract a set of observations from a
	  // training corpus with relations
	  public static void featureRelExtraction() throws Exception{
		  // this class encapsulates the whole
		  // procedure
		  new RelationVectorB("semrep-");
	  }
	  
	  
}
