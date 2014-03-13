package mymetamap;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


// class for expanding MetaMap abbreviations
public class Abbrev {
	
	
	// hash table field(s)
	private static HashMap<String,String> abbv = 
			new HashMap<String,String>(); // expanded labels
	private static HashMap<String,String> maps = 
			new HashMap<String,String>(); // simplified labels
	private static HashMap<String,String> relabbv = 
			new HashMap<String,String>(); // simplified relations
	
	
	// abbrev + concept name or label
	public Abbrev(){
		
		
		// A. table of abbreviations + concepts
		
		
		abbv.put("[aapp]", "Amino Acid, Peptide, or Protein");
		abbv.put("[acab]", "Acquired Abnormality");
		abbv.put("[acty]", "Activity");		
		abbv.put("[aggp]", "Age Group");
		abbv.put("[amas]", "Amino Acid Sequence");		
		abbv.put("[amph]", "Amphibian");
		abbv.put("[anab]", "Anatomical Abnormality");
		abbv.put("[anim]", "Animal");
		abbv.put("[anst]", "Anatomical Structure");
		abbv.put("[antb]", "Antibiotic");
		abbv.put("[arch]", "Archaeon");
		abbv.put("[bacs]", "Biologically Active Substance");
		abbv.put("[bact]", "Bacterium");
		abbv.put("[bdsu]", "Body Substance");
		abbv.put("[bdsy]", "Body System");
		abbv.put("[bhvr]", "Behavior");		
		abbv.put("[biof]", "Biologic Function");
		abbv.put("[bird]", "Bird");
		abbv.put("[blor]", "Body Location or Region");
		abbv.put("[bmod]", "Biomedical Occupation or Discipline");
		abbv.put("[bodm]", "Biomedical or Dental Material");
		abbv.put("[bpoc]", "Body Part, Organ, or Organ Component");
		abbv.put("[bsoj]", "Body Space or Junction");
		abbv.put("[carb]", "Carbohydrate");
		abbv.put("[celc]", "Cell Component");
		abbv.put("[celf]", "Cell Function");
		abbv.put("[cell]", "Cell");		
		abbv.put("[cgab]", "Congenital Abnormality");
		abbv.put("[chem]", "Chemical");
		abbv.put("[chvf]", "Chemical Viewed Functionally");
		abbv.put("[chvs]", "Chemical Viewed Structurally");
		abbv.put("[clas]", "Classification");
		abbv.put("[clna]", "Clinical Attribute");
		abbv.put("[clnd]", "Clinical Drug");
		abbv.put("[cnce]", "Conceptual Entity");
		abbv.put("[comd]", "Cell or Molecular Dysfunction");
		abbv.put("[crbs]", "Carbohydrate Sequence");
		abbv.put("[diap]", "Diagnostic Procedure");		
		abbv.put("[dora]", "Daily or Recreational Activity");
		abbv.put("[drdd]", "Drug Delivery Device");
		abbv.put("[dsyn]", "Disease or Syndrome");
		abbv.put("[edac]", "Educational Activity");
		abbv.put("[eehu]", "Environmental Effect of Humans");
		abbv.put("[eico]", "Eicosanoid");
		abbv.put("[elii]", "Elelment, Ion, or Isotope");
		abbv.put("[emod]", "Experimental Model of Disease");
		abbv.put("[emst]", "Embryonic Structure");
		abbv.put("[enty]", "Entity");
		abbv.put("[enzy]", "Enzyme");		
		abbv.put("[euka]", "Eukaryote");
		abbv.put("[evnt]", "Event");
		abbv.put("[famg]", "Family Group");
		abbv.put("[ffas]", "Fully Formed Anatomical Structure");
		abbv.put("[fish]", "Fish");
		abbv.put("[fndg]", "Finding");
		abbv.put("[fngs]", "Fungus");
		abbv.put("[food]", "Food");
		abbv.put("[ftcn]", "Functional Concept");		
		abbv.put("[genf]", "Genetic Function");
		abbv.put("[geoa]", "Geographic Area");		
		abbv.put("[gngm]", "Gene or Genome");
		abbv.put("[gora]", "Governmental or Regulatory Activity");
		abbv.put("[grpa]", "Group Attribute");
		abbv.put("[grup]", "Group");	
		abbv.put("[hccp]", "Human-caused Phenomenon or Process");
		abbv.put("[hcro]", "Health Care Related Organization");
		abbv.put("[hlca]", "Health Care Activity");
		abbv.put("[hops]", "Hazardous or Poisonous Substance");
		abbv.put("[horm]", "Hormone");		
		abbv.put("[humn]", "Human");
		abbv.put("[idcn]", "Idea or Concept");		
		abbv.put("[imft]", "Immunologic Factor");
		abbv.put("[inbe]", "Idividual Behavior");
		abbv.put("[inch]", "Inorganic Chemical");
		abbv.put("[inpo]", "Injury or Poisoning");
		abbv.put("[inpr]", "Intellectual Product");
		abbv.put("[irda]", "Indicator, Reagent, or Diagnostic Aid");
		abbv.put("[lang]", "Language");
		abbv.put("[lbpr]", "Laboratory Procedure");
		abbv.put("[lbtr]", "Laboratory or Test Result");		
		abbv.put("[lipd]", "Lipid");
		abbv.put("[mamm]", "Mammal");		
		abbv.put("[mbrt]", "Molecular Biology Research Technique");
		abbv.put("[mcha]", "Machine Activity");
		abbv.put("[medd]", "Medical Device");
		abbv.put("[menp]", "Mental Process");
		abbv.put("[mnob]", "Manufactured Object");
		abbv.put("[modb]", "Mental or Behavioral Dysfunction");
		abbv.put("[moft]", "Molecular Function");
		abbv.put("[mosq]", "Molecular Sequence");
		abbv.put("[neop]", "Neoplastic Process");
		abbv.put("[nnon]", "Nucleic Acid, Nucleoside, or Nucleotide");
		abbv.put("[npop]", "Natural Phenomenon or Process");		
		abbv.put("[nsba]", "Neuroreactive Substance or Biogenic Amine");
		abbv.put("[nusq]", "Nucleotide Sequence");
		abbv.put("[ocac]", "Occupational Activity");
		abbv.put("[ocdi]", "Occupation or Discipline");
		abbv.put("[opco]", "Organophosphorus Compound");
		abbv.put("[orch]", "Organic Chemical");
		abbv.put("[orga]", "Organism Attribute");
		abbv.put("[orgf]", "Organism Function");
		abbv.put("[orgm]", "Organism");
		abbv.put("[orgt]", "Organization");
		abbv.put("[ortf]", "Organ or Tissue Function");		
		abbv.put("[patf]", "Pathologic Function");
		abbv.put("[phob]", "Physical Object");
		abbv.put("[phpr]", "Phenomenon or Process");
		abbv.put("[phsf]", "Physiologic Function");
		abbv.put("[phsu]", "Physical Object");
		abbv.put("[plnt]", "Phenomenon or Process");
		abbv.put("[podg]", "Physiologic Function");//
		abbv.put("[phsu]", "Pharmacologic Substance");//
		abbv.put("[plnt]", "Plant");
		abbv.put("[podg]", "Patient or Disabled Group");
		abbv.put("[popg]", "Population Group");
		abbv.put("[prog]", "Professional or Occupational Group");
		abbv.put("[pros]", "Professional Society");
		abbv.put("[qlco]", "Qualitative Concept");		
		abbv.put("[qnco]", "Quantitative Concept");
		abbv.put("[rcpt]", "Receptor");
		abbv.put("[rept]", "Reptile");
		abbv.put("[resa]", "Research Activity");
		abbv.put("[resd]", "Research Device");
		abbv.put("[rnlw]", "Regulation or Law");
		abbv.put("[sbst]", "Substance");
		abbv.put("[shro]", "Self-help or Relief Organization");
		abbv.put("[socb]", "Social Behavior");
		abbv.put("[sosy]", "Sign or Symptom");
		abbv.put("[spco]", "Spatial Concept");		
		abbv.put("[strd]", "Steroid");
		abbv.put("[tisu]", "Tissue");
		abbv.put("[tmco]", "Temporal Concept");		
		abbv.put("[topp]", "Therapeutic or Preventive Procedure");
		abbv.put("[virs]", "Virus");
		abbv.put("[vita]", "Vitamin");
		abbv.put("[vtbt]", "Vertebrate");
		
		
		// B. table of abbreviations + labels
		
		
		// we want to have between 8 and 12 labels! n
		
		// i. events:
		
		//---------	
		
		// (event)
		
		// activities
		maps.put("[gora]", "activity");		
		maps.put("[lbpr]", "activity");
		maps.put("[mbrt]", "activity");
		maps.put("[mcha]", "activity");
		maps.put("[hlca]", "activity");		
		maps.put("[ocac]", "activity");		
		maps.put("[resa]", "activity");	
		maps.put("[topp]", "activity");		
		maps.put("[diap]", "activity");		
		maps.put("[dora]", "activity");		
		maps.put("[edac]", "activity");
		maps.put("[acty]", "activity");	
		maps.put("[inbe]", "activity");
		maps.put("[bhvr]", "activity");
		maps.put("[socb]", "activity");
		maps.put("[lbtr]", "activity");
		maps.put("[evnt]", "activity");		
		
		// process
		maps.put("[neop]", "process");
		maps.put("[npop]", "process");
		maps.put("[plnt]", "process");
		maps.put("[podg]", "process");
		maps.put("[phpr]", "process");	
		maps.put("[phsf]", "process");		
		maps.put("[ortf]", "process");
		maps.put("[orgf]", "process");
		maps.put("[biof]", "process");
		maps.put("[menp]", "process");
		maps.put("[genf]", "process");	
		maps.put("[modb]", "process");
		maps.put("[moft]", "process");
		maps.put("[patf]", "process");
		maps.put("[comd]", "process");		
		maps.put("[dsyn]", "process");
		maps.put("[eehu]", "process");
		maps.put("[hccp]", "process");
		maps.put("[celf]", "process");
		maps.put("[emod]", "process");
		
		//---------	
		
		// ii. entities

		//---------	
		
		// (organism)
		
		// organism
		maps.put("[amph]", "organism");
		maps.put("[anab]", "organism");
		maps.put("[anim]", "organism");
		maps.put("[arch]", "organism");
		maps.put("[bact]", "organism");
		maps.put("[bird]", "organism");
		maps.put("[fngs]", "organism");	
		maps.put("[orgm]", "organism");		
		maps.put("[humn]", "organism");
		maps.put("[plnt]", "organism");
		maps.put("[mamm]", "organism");	
		maps.put("[rept]", "organism");			
		maps.put("[virs]", "organism");
		maps.put("[vtbt]", "organism");		
		maps.put("[euka]", "organism");	
		maps.put("[fish]", "organism");

		// body part
		maps.put("[anst]", "body");
		maps.put("[bpoc]", "body");	
		maps.put("[celc]", "body");
		maps.put("[cell]", "body");
		maps.put("[tisu]", "body");			
		maps.put("[ffas]", "body");
		maps.put("[gngm]", "body");		
		maps.put("[emst]", "body");

		//---------		
		
		// finding/condition
		maps.put("[acab]", "finding");			
		maps.put("[cgab]", "finding");
		maps.put("[inpo]", "finding");
		maps.put("[sosy]", "finding");
		maps.put("[fndg]", "finding");
		
		//---------

		// substance
		maps.put("[aapp]", "substance");		
		maps.put("[bacs]", "substance");		
		maps.put("[antb]", "substance");		
		maps.put("[bdsu]", "substance");		
		maps.put("[bodm]", "substance");		
		maps.put("[carb]", "substance");		
		maps.put("[elii]", "substance");		
		maps.put("[chem]", "substance");
		maps.put("[chvf]", "substance");
		maps.put("[chvs]", "substance");
		maps.put("[crbs]", "substance");		
		maps.put("[eico]", "substance");
		maps.put("[hops]", "substance");
		maps.put("[imft]", "substance");
		maps.put("[nnon]", "substance");
		maps.put("[nsba]", "substance");
		maps.put("[opco]", "substance");
		maps.put("[orch]", "substance");		
		maps.put("[inch]", "substance");		
		maps.put("[horm]", "substance");			
		maps.put("[food]", "substance");
		maps.put("[irda]", "substance");
		maps.put("[lipd]", "substance");		
		maps.put("[enzy]", "substance");
		maps.put("[vita]", "substance");
		maps.put("[phsu]", "substance");
		maps.put("[sbst]", "substance");	
		maps.put("[rcpt]", "substance");
		maps.put("[strd]", "substance");		
		maps.put("[amas]", "substance");
		
		//---------
		
		// (resource)
				
		// object
		maps.put("[enty]", "object");
		maps.put("[phob]", "object");
		maps.put("[phsu]", "object");
		maps.put("[bmod]", "object");	
		maps.put("[clas]", "object");		
		maps.put("[cnce]", "object");
		maps.put("[ocdi]", "object");		
		maps.put("[rnlw]", "object");		
		maps.put("[idcn]", "object");
		maps.put("[inpr]", "object");		
		maps.put("[lang]", "object");			
		maps.put("[ftcn]", "object");
		
		// device
		maps.put("[resd]", "device");		
		maps.put("[clnd]", "device");		
		maps.put("[drdd]", "device");		
		maps.put("[medd]", "device");
		maps.put("[mnob]", "device");
		
		//---------

		// property
		maps.put("[clna]", "property");		
		maps.put("[clna]", "property");
		maps.put("[qlco]", "property");		
		maps.put("[qnco]", "property");
		maps.put("[ftcn]", "property");
		maps.put("[bdsy]", "property");
		maps.put("[grpa]", "property");
		maps.put("[orga]", "property");
		
		//---------
		
		// location
		maps.put("[blor]", "location");		
		maps.put("[spco]", "location");	
		maps.put("[nusq]", "location");	
		maps.put("[mosq]", "location");	
		maps.put("[bsoj]", "location");
		maps.put("[tmco]", "location");
		maps.put("[geoa]", "location");
		
		//---------
				
		// (actor)
		
		// group/actor (agent or patient of process or activity)
		maps.put("[famg]", "group");		
		maps.put("[grup]", "group");			
		maps.put("[podg]", "group");
		maps.put("[popg]", "group");
		maps.put("[prog]", "group");
		maps.put("[aggp]", "group");
		
		// organization
		maps.put("[pros]", "organization");
		maps.put("[orgt]", "organization");
		maps.put("[shro]", "organization");
		maps.put("[hcro]", "organization");
		
		//---------	
		
		
		// C. relations
		
		// temporal
		relabbv.put("PRECEDES", "temporal");
		relabbv.put("COEXISTS_WITH", "temporal");
		
		// causal
		relabbv.put("CAUSES", "causal");
		relabbv.put("PROCESS_OF", "causal");
		relabbv.put("PRODUCES", "causal");
		relabbv.put("STIMULATES", "causal");
		relabbv.put("TREATS", "causal");
		relabbv.put("COMPLICATES", "causal");
		relabbv.put("DISRUPTS", "causal");
		relabbv.put("INHIBITS", "causal");
		relabbv.put("AUGMENTS", "causal");
		relabbv.put("AFFECTS", "causal");
		relabbv.put("DIAGNOSES", "causal");
		relabbv.put("DISRUPTS", "causal");
		
		// other
		relabbv.put("ADMINISTERED_TO", "none");
		relabbv.put("ASSOCIATED_WITH", "none");
		relabbv.put("CONVERTS_TO", "none");
		relabbv.put("COMPLICATES", "none");
		relabbv.put("SAME_AS", "none");
		relabbv.put("COMPARED_WITH", "none");
		relabbv.put("HIGHER_THAN", "none");
		relabbv.put("LOWER_THAN", "none");
		relabbv.put("USES", "none");
		relabbv.put("PART_OF", "none");		
		relabbv.put("MANIFESTATION_OF", "none");
		relabbv.put("METHOD_OF", "none");
		relabbv.put("OCCURS_IN", "none");
		relabbv.put("ISA", "none");
		relabbv.put("LOCATION_OF", "none");
		relabbv.put("INTERACTS_WITH", "none");
			
	}
	
	
	// mapping abbrev --> concept
	public String myFilter(String key){
		return abbv.get(key);
	}

	
	// mapping abbrev --> label (12 labels)
	public String myLabel(String key){
		return maps.get(key);
	}
	
	
	// mapping relabb --> relation (3 labels)
	public String simplifyRel(String key){
		return relabbv.get(key);
	}
	
	
	// mapping abbrev --> label (4 labels)
	public String mySimpLabel(String key){
		if (maps.get(key) == "activity" | maps.get(key) == "process" | maps.get(key) == "finding"){
			return "event";
		}
		if (maps.get(key) == "group" | maps.get(key) == "organization"){
			return "actor";
		}
		if (maps.get(key) == "object" | maps.get(key) == "device" | maps.get(key) == "substance"){
			return "resource";
		}
		else{
			return "other";
		}
	}

	
	// counting the num of UMLS concepts
	// falling a generic concept label (12)
	public int multi(String label){
		int count = 0;
		for (String key : maps.keySet()){
			if (maps.get(key).equals(label)){
				count = count + 1;
			}
		}
		return count;
	}

		
	// counting the num of UMLS concepts
	// falling a generic concept label (4)
	public int multiSimp(String label){
		if (label == "event"){
			return multi("activity") + multi("process") + multi("finding");
		}
		if (label == "resource"){
			return multi("object") + multi("device") + multi("substance");
		}
		if (label == "actor"){
			return multi("group") + multi("organization");
		}
		if (label == "other"){
			return multi("body") + multi("location") + multi("property") + multi("organism");
		}
		else{
			return multi(label);
		}
	}
	
	
	// label simplification (12)
	public String simplify(String label){
		String res = "";
		for (String key : abbv.keySet()){
			if (abbv.get(key).equals(label)){
				res = maps.get(key);
			}
		}
		return res;
	}
	
	
	// label simplification (8)
	public String simplifySimp(String label){
		String res = "";
		for (String key : abbv.keySet()){
			if (abbv.get(key).equals(label)){
				res = mySimpLabel(key);
			}
		}
		return res;
	}

	
}
