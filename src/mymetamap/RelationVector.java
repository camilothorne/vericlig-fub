package mymetamap;


import java.util.ArrayList;
import java.util.Set;


import rw.Write;


//----------------------
// Observation(s)
//----------------------


// This class extracts the clinical entities/terms
// with their labels from the MetaMap corpus
class RelObservation{

	
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
	
	// relation
	public 	String 				relation; // relation
	
	
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
	public RelObservation(myNounPhrase np, 
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
		this.relation		= myab.simplifyRel(np.relation_label);
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


public class RelationVector{
	
	
	// extra fields 
	private static Abbrev abbv;
		
	
	// NPs
	public ArrayList<RelObservation> myNPs;
	
	
	// constructor
	public RelationVector(String name, ArrayList<mySentence> sents, String filepath, String type) throws Exception{
		abbv = new Abbrev();
		// init samples
		this.myNPs = new ArrayList<RelObservation>();
		// assign samples
		for (mySentence sen: sents){
			for (myNounPhrase np: sen.noun_phrases){
				RelObservation obs = new RelObservation(np,sen.senlabels,abbv);
				this.myNPs.add(obs);
			}
		}
		returnArff2(this.myNPs, name, filepath, type);
	}	

		
	// return arff file
	public void returnArff2(ArrayList<RelObservation> mySens, String name, String filepath, String type){
		
		String path = filepath + name + type;
		String data = ""; // data
		
		// File header
		String header 	= "@relation metamap-weka.filters.unsupervised.instance.ClassRemover-Clast-N2-H";
		
		// NP1
		String mynest 	= "@attribute nestl1 numeric";
		String mysubor 	= "@attribute subor1 {yes, no}";
		String depen	= "@attribute gov1 	 {yes,no}";
		String myarg 	= "@attribute arg1	 {subj, obj}";
		String myfreq 	= "@attribute freq1  numeric";
		String mysimA 	= "@attribute simA1  numeric";
		String mysimB 	= "@attribute simB1  numeric";
		String mysimC 	= "@attribute simC1  numeric";
		
		// NP2
		String mynest2 	= "@attribute nestl2  numeric";
		String mysubor2 = "@attribute subor2 {yes, no}";
		String depen2	= "@attribute gov2 	{yes,no}";
		String myarg2 	= "@attribute arg2	{subj, obj}";
		String myfreq2 	= "@attribute freq2  numeric";
		String mysimA2 	= "@attribute simA2  numeric";
		String mysimB2 	= "@attribute simB2  numeric";
		String mysimC2 	= "@attribute simC2  numeric";		
		
		// NP1 type
		String mylabel 	= "@attribute class1 {event, actor, resource, other}"; // simple labels
		
		// NP2 type
		String mylabel2 = "@attribute class2 {event, actor, resource, other}"; // simple labels
		
		// control flow
		String relation = "@attribute flow	{temporal, causal, none}";
		
		// initialize header
		data = data + 	header  	+ 
						"\n" 		+ mynest  + "\n" 	+ mysubor 	+ "\n" + depen  + "\n" +
						myarg 		+ "\n" + myfreq  + "\n" 	+ mysimA 	+ "\n" + mysimB + "\n" + 
						mysimC 		+ "\n" + mylabel + 
						"\n" 		+ mynest2  + "\n" 	+ mysubor2 	+ "\n" + depen2  + "\n" +
						myarg2 		+ "\n" + myfreq2  + "\n" 	+ mysimA2 	+ "\n" + mysimB2 + "\n" + 
						mysimC2 	+ "\n" + mylabel2 + "\n"	+ relation 	+
						"\n\n" 		+ "@data" 	+ "\n";
		
		// initialize data
		int bound = 0;// looping bound
		if (mySens.size()%2 == 0){
			bound = mySens.size(); // =, if even
		}
		else{
			bound = mySens.size()-1; // -1, if odd
		}
		for (int i=0;i<bound;i=i+2){ // increment iterator by 2
			RelObservation o1 = mySens.get(i);
			RelObservation o2 = mySens.get(i+1);
			if (mySens.get(i).sentence == mySens.get(i+1).sentence){ // should belong to the same sentence
				// non-inverted order
				String vector = 
					o1.nesting 	+ " , " + o1.subor 	+ " , " + o1.depen   + " , " + o1.argum 	+ " , " +
					o1.freq 	+ " , " + o1.similA + " , " + o1.similB  + " , " + o1.similC 	+ " , " + o1.simplabel + " , " +
					o2.nesting 	+ " , " + o2.subor 	+ " , " + o2.depen   + " , " + o2.argum 	+ " , " +
					o2.freq 	+ " , " + o2.similA + " , " + o2.similB  + " , " + o2.similC 	+ " , " + o2.simplabel + " , " + 
					o1.relation;
				data = data + vector + "\n";
				
				//------------------------------------------
				//   testing  observations:
				//
				System.out.println("freq1: "+o1.freq);
				System.out.println("nest1: "+o1.nesting);
				System.out.println("sub1?: "+o1.subor);
				System.out.println("gov1?: "+o1.depen);
				System.out.println("arg1?: "+o1.argum);
				System.out.println("simA1: "+o1.similA);
				System.out.println("simB1: "+o1.similB);
				System.out.println("simC1: "+o1.similC);
				System.out.println("con1:  "+o1.label);
				System.out.println("-----------------------------------------------");
				System.out.println("conS1: "+o1.simplabel);
				System.out.println("-----------------------------------------------");
				System.out.println("freq2: "+o2.freq);
				System.out.println("nest2: "+o2.nesting);
				System.out.println("sub2:  "+o2.subor);
				System.out.println("gov2?: "+o2.depen);
				System.out.println("arg2?: "+o2.argum);
				System.out.println("simA2: "+o2.similA);
				System.out.println("simB2: "+o2.similB);
				System.out.println("simC2: "+o2.similC);
				System.out.println("con2:  "+o2.label);
				System.out.println("-----------------------------------------------");
				System.out.println("conS2: "+o2.simplabel);
				System.out.println("-----------------------------------------------");
				System.out.println("rela:  "+o1.relation);				
				System.out.println("===============================================");
				//
				//------------------------------------------
				//   (.arff to be used by Weka)				
				
			}	
		}
		
		new Write(path,data); // write .arff file
	}
	
	
}
