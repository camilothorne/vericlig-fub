package mymetamap;


import java.util.ArrayList;
import java.util.Set;

import corpora.ReadXMLFile;

import rw.Write;


//----------------------
// Observation(s)
//----------------------


// This class extracts the clinical entities/terms
// with their labels from the MetaMap corpus
class Observation{

	
	// key features (for identification)	
	public  String 	words; 		// the (base) NP (without determiner(s))
	public  int 	sentence; 	// the sentence to which it belongs
	public  int 	position; 	// its number within the sentence
		
	// this is the feature we want to predict
	public  String 	simplabel; 		// class : actor, patient, activity, resource, condition
	public  String 	label; 			// MetaMap concept
	
	// features for learning:	
		
	// a. difficult to compute:
	public  String			 	depen; 	 // dependency relation types (*)
	public  float				freq; 	 // frequency of class within a sentence (*)
		
	// b. easy to compute
	public  int 				nesting; // in how many NPs is this NP nested?
	public  String 				argum; 	 // position: subj, obj
	public  String 				subor; 	 // occurs in sub sentence: yes, no?
	public  float 				similA;  // similarity (value in [0,1]) between label and NP (first measure)
	public  float 				similB;  // similarity (value in [0,1]) between label and NP (second measure)
	public  float 				similC;  // similarity (value in [0,1]) between head N and NP (third measure)
	
	
	// ===========
	// Extensions:
	// ===========
	//
	//
	// 1. Consider labeling all NPs in a sentence *simultaneously*
	// 2. Consider ontology-driven selectional restrictions
	// 3. Consider relations
	// 4. Consider ontology-driven similarity (i.e., distance in the taxonomy) 
		
	
	// constructor
	public Observation(myNounPhrase np, 
			Set<String> senlabels, Abbrev myab){
		// simple
		this.words 			 = np.phrase;
		this.label 			 = np.class_label;
		// simple label
		if (myab.simplifySimp(np.class_label)==""){
			this.simplabel 	 = "other";
		}else{
			this.simplabel   = myab.simplifySimp(np.class_label); // simplified concept
		}
		this.nesting 		 = np.nesting_level;
		this.argum 			 = np.role;
		// dependencies
		this.depen 			 = "no";
		for (String typ : np.depens){
			if (typ.equals("nn")){
				this.depen 	 = "yes";
			}
		}
		this.sentence		 = np.sentence;
		this.position		 = np.position;
		// subordination
		if (np.nesting_level >= 1){
			this.subor 		 = "yes";
		}else{
			this.subor 		 = "no";
		}
		// frequency
		this.freq 			= setFreq(senlabels,np.class_label);	
		// similarity functions
		this.similA 		= computeSimA(np.labels,np.class_label);
		this.similC 		= computeSimC(np.nphead.labels,np.labels);
		this.similB 		= computeSimB(np.simp_labels,this.simplabel,myab);
		// relations
	}
		
	
	// label frequency (within a sentence)
	//
	// freq(T) = 
	//
	//           |occ(NP,T)|
	// ----------------------------------
	// (Sum |{occ(NP',T') ; NP',T'}|) + 1
	//
	public static float setFreq(Set<String> sen_tags, String label){
		float num = 0;
		for (String lab: sen_tags){
			if (((lab!= null)&&(label!=null))&&lab.equals(label)){
				num = num + 1;
			}
		}
		float den = (sen_tags.size()+1);
		float count = num/den;
		return count;
	}
		
	
	// A. Compute similarity
	//    (frequency-based)
	//
	// sim(NP,T) =
	//
	//      |occ(T,NP)| + 1
	// -----------------------------
	// (Sum |{occ(T',NP) ; T'}|) + 1
	//
	public float computeSimA(ArrayList<String> np_con, String label){
		float sim;
		float conA = 0; // numerator
		float conB = np_con.size(); // denominator
		for (String lab1: np_con){
			if (((lab1!= null)&&(label!=null))&&lab1.equals(label)){
				conA = conA + 1;
			}
		}
		sim = conA / (conB + 1);
		return sim; // similarity
	}
	
	
	// B. compute similarity
	// (ontology-based)
	//
	// sim(NP,T) =
	//
	//   |tags(NP) /\ sub(T)| + 1
	//   ------------------------
	//   |tags(NP) \/ sub(T)| + 1
	//	
	public float computeSimB(ArrayList<String> noun_con, 
			String label, Abbrev myab){
		float sim;
		float conA = 0; // numerator
		float conB = 0; // denominator
		for (String lab1: noun_con){
			conB = conB + 1;
			if (((lab1!= null)&&(label!=null))&&lab1.equals(label)){
				conA = conA + 1;
			}
		}
		conB = conB + myab.multiSimp(label);
		sim = conA / (conB + 1);
		return sim; // similarity
	}
	
	
	// C. Compute tag similarity
	// (between NP and head noun N)
	//
	// sim(NP,N) =
	//
	//   |tags(NP) /\ tags(N)| + 1
	//   -------------------------
	//   |tags(NP) \/ tags(N)| + 1
	//
	public float computeSimC(ArrayList<String> noun_con, ArrayList<String> np_con){
		float sim;
		float conA = 0; // numerator
		float conB = 0; // denominator
		for (String lab1: noun_con){
			conB = conB + 1;
			for (String lab2: np_con){
				conB = conB + 1;
				if (((lab1!= null)&&(lab2!=null))&&lab1.equals(lab2)){
					conA = conA + 1;
				}
			}
		}		
		sim = conA / (conB + 1);
		return sim; // similarity
	}
	
	
}


//----------------------
//  Feature vectors
//----------------------


public class FeatureVector{
	
	
	// extra fields 
	private static Abbrev abbv;
	private static ReadXMLFile file;
	
	
	// NPs
	public ArrayList<Observation> myNPs;
	
	
	// constructor
	public FeatureVector(String name) throws Exception{
		file = new ReadXMLFile("/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/adjudicated.xml");
		abbv = new Abbrev();
		// init samples
		this.myNPs = new ArrayList<Observation>();
		// assign samples
		ArrayList<mySentence> sents = file.sentences;
		for (mySentence sen: sents){
			for (myNounPhrase np: sen.noun_phrases){
				Observation obs = new Observation(np,sen.senlabels,abbv);
				this.myNPs.add(obs);
			}
		}
		returnArff(this.myNPs, name);
		returnArff2(this.myNPs, name);
	}	

	
	// return arff file
	public void returnArff(ArrayList<Observation> myNPs, String name){
		String path = "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/"+name+".arff"; // file to save
		String data = ""; // data
		// File header
		String header 	= "@relation metamap-weka.filters.unsupervised.instance.ClassRemover-Clast-N2-H";
		String mynest 	= "@attribute nestl  numeric";
		String mysubor 	= "@attribute subor {yes, no}";
		String depen	= "@attribute gov 	{yes,no}";
		String myarg 	= "@attribute arg	{subj, obj}";
		String myfreq 	= "@attribute freq  numeric";
		String mysimA 	= "@attribute simA  numeric";
		String mysimB 	= "@attribute simB  numeric";
		String mysimC 	= "@attribute simC  numeric";
		String mylabel 	= "@attribute class {substance, event, object, resource, " +
				"actor, organism, location, property, finding, other}"; // simple labels
		// initialize header
		data = data + 	header  + "\n" + mynest  + "\n" 	+ mysubor 	+ "\n" + depen  + "\n" +
						myarg 	+ "\n" + myfreq  + "\n" 	+ mysimA 	+ "\n" + mysimB + "\n" + 
						mysimC 	+ "\n" + mylabel + "\n\n" 	+ "@data" 	+ "\n";
		// initialize data	
		for (Observation o: myNPs){
			String vector = o.nesting 	+ " , " + o.subor 	+ " , " + o.depen   + " , " + o.argum 	+ " , " +
							o.freq 		+ " , " + o.similA 	+ " , " + o.similB 	+ " , " + o.similC 	+ " , " + o.simplabel;
			data = data + vector + "\n";
			
			//------------------------------------------
			//   testing  observations:
			//
			System.out.println("freq: "+o.freq);
			System.out.println("nest: "+o.nesting);
			System.out.println("sub?: "+o.subor);
			System.out.println("gov?: "+o.depen);
			System.out.println("arg?: "+o.argum);
			System.out.println("simA: "+o.similA);
			System.out.println("simB: "+o.similB);
			System.out.println("simC: "+o.similC);
			System.out.println("con:  "+o.label);
			System.out.println("conS: "+o.simplabel);
			System.out.println("===============================================");
			//
			//------------------------------------------
			//   (.arff to be used by Weka)
			
		}		
		new Write(path,data);
	}
	
	
	// return arff file 2 (multilabel)
	public void returnArff2(ArrayList<Observation> mySens, String name){
		String path = "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/"+name+"2.arff"; // file to save
		String data = ""; // data
		// File header
		String header 	= "@relation metamap-weka.filters.unsupervised.instance.ClassRemover-Clast-N2-H";
		
		String mynest 	= "@attribute nestl1  numeric";
		String mysubor 	= "@attribute subor1 {yes, no}";
		String depen	= "@attribute gov1 	{yes,no}";
		String myarg 	= "@attribute arg1	{subj, obj}";
		String myfreq 	= "@attribute freq1  numeric";
		String mysimA 	= "@attribute simA1  numeric";
		String mysimB 	= "@attribute simB1  numeric";
		String mysimC 	= "@attribute simC1  numeric";
		
		String mynest2 	= "@attribute nestl2  numeric";
		String mysubor2 = "@attribute subor2 {yes, no}";
		String depen2	= "@attribute gov2 	{yes,no}";
		String myarg2 	= "@attribute arg2	{subj, obj}";
		String myfreq2 	= "@attribute freq2  numeric";
		String mysimA2 	= "@attribute simA2  numeric";
		String mysimB2 	= "@attribute simB2  numeric";
		String mysimC2 	= "@attribute simC2  numeric";
		
		String mylabel = "@attribute class1 {substance, event, object, resource, " +
				"actor, organism, location, property, finding, other}"; // simple labels
		String mylabel2 = "@attribute class2 {substance, event, object, resource, " +
				"actor, organism, location, property, finding, other}"; // simple labels
		
		// initialize header
		data = data + 	header  + 
						"\n" + mynest  + "\n" 	+ mysubor 	+ "\n" + depen  + "\n" +
						myarg 	+ "\n" + myfreq  + "\n" 	+ mysimA 	+ "\n" + mysimB + "\n" + 
						mysimC 	+ "\n" + mylabel + 
						"\n" + mynest2  + "\n" 	+ mysubor2 	+ "\n" + depen2  + "\n" +
						myarg2 	+ "\n" + myfreq2  + "\n" 	+ mysimA2 	+ "\n" + mysimB2 + "\n" + 
						mysimC2 + "\n" + mylabel2 + 
						"\n\n" 	+ "@data" 	+ "\n";
		// initialize data
		int bound = 0;// looping bound
		if (mySens.size()%2 == 0){
			bound = mySens.size(); // =, if even
		}
		else{
			bound = mySens.size()-1; // -1, if odd
		}
		for (int i=0;i<bound;i=i+2){ // increment iterator by 2
			Observation o1 = mySens.get(i);
			Observation o2 = mySens.get(i+1);
			if (mySens.get(i).sentence == mySens.get(i+1).sentence){ // should belong to the same sentence
				// non-inverted order
				String vector = 
					o1.nesting 	+ " , " + o1.subor 	+ " , " + o1.depen   + " , " + o1.argum 	+ " , " +
					o1.freq 	+ " , " + o1.similA + " , " + o1.similB  + " , " + o1.similC 	+ " , " + o1.simplabel + " , " +
					o2.nesting 	+ " , " + o2.subor 	+ " , " + o2.depen   + " , " + o2.argum 	+ " , " +
					o2.freq 	+ " , " + o2.similA + " , " + o2.similB  + " , " + o2.similC 	+ " , " + o2.simplabel;
				data = data + vector + "\n";
				// inverted order
				String vector2 = 
						o2.nesting 	+ " , " + o2.subor 	+ " , " + o2.depen   + " , " + o2.argum 	+ " , " +
						o2.freq 	+ " , " + o2.similA + " , " + o2.similB  + " , " + o2.similC 	+ " , " + o2.simplabel + " , " +
						o1.nesting 	+ " , " + o1.subor 	+ " , " + o1.depen   + " , " + o1.argum 	+ " , " +
						o1.freq 	+ " , " + o1.similA + " , " + o1.similB  + " , " + o1.similC 	+ " , " + o1.simplabel;
				data = data + vector2 + "\n";
			}
		}		
		new Write(path,data);
	}
	
	
}
