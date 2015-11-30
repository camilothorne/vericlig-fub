package tests;

import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;
import gov.nih.nlm.nls.metamap.AcronymsAbbrevs;
import gov.nih.nlm.nls.metamap.ConceptPair;
import gov.nih.nlm.nls.metamap.Ev;
import gov.nih.nlm.nls.metamap.Mapping;
import gov.nih.nlm.nls.metamap.MetaMapApi;
import gov.nih.nlm.nls.metamap.MetaMapApiImpl;
import gov.nih.nlm.nls.metamap.Negation;
import gov.nih.nlm.nls.metamap.PCM;
import gov.nih.nlm.nls.metamap.Position;
import gov.nih.nlm.nls.metamap.Result;
import gov.nih.nlm.nls.metamap.Utterance;

/**
 * ApiTestSimp: A simple annotation test.
 * 
 *
 */
public class ApiTestSimp {
	
  /** 
   * MetaMap api instance
   *  
   */
  MetaMapApi api;

  /**
   * Creates a new instance.
   *
   */
  public ApiTestSimp() {
    this.api = new MetaMapApiImpl();
  }

  /**
   * Creates a new instance using specified host and port.
   *
   * @param serverHostname hostname of MetaMap server.
   * @param serverPort     listening port used by MetaMap server.
   */
  public ApiTestSimp(String serverHostname, int serverPort) {
    this.api = new MetaMapApiImpl();
    this.api.setHost(serverHostname);
    this.api.setPort(serverPort);
  }

  /**
   * Timeout interval
   * 
   * @param interval
   */
  void setTimeout(int interval) {
    this.api.setTimeout(interval);
  }

  /**
   * Process terms using MetaMap API and display result to standard output.
   *
   * @param terms input terms
   * @param out output printer
   * @param serverOptions options to pass to metamap server before processing input text.
   */
  void process(String terms, PrintStream out, List<String> serverOptions) 
    throws Exception
  {
    if (serverOptions.size() > 0) {
      api.setOptions(serverOptions);
    }
    List<Result> resultList = api.processCitationsFromString("promotion");
    for (Result result: resultList) {
      if (result != null) {
	out.println("input text: ");
	out.println(" " + result.getInputText());
	List<AcronymsAbbrevs> aaList = result.getAcronymsAbbrevsList();
	if (aaList.size() > 0) {
	  out.println("Acronyms and Abbreviations:");
	  for (AcronymsAbbrevs e: aaList) {
	    out.println("Acronym: " + e.getAcronym());
	    out.println("Expansion: " + e.getExpansion());
	    out.println("Count list: " + e.getCountList());
	    out.println("CUI list: " + e.getCUIList());
	  }
	}
	List<Negation> negList = result.getNegationList();
	if (negList.size() > 0) {
	  out.println("Negations:");
	  for (Negation e: negList) {
	    out.println("type: " + e.getType());
	    out.print("Trigger: " + e.getTrigger() + ": [");
	    for (Position pos: e.getTriggerPositionList()) {
	      out.print(pos  + ",");
	    }
	    out.println("]");
	    out.print("ConceptPairs: [");
	    for (ConceptPair pair: e.getConceptPairList()) {
	      out.print(pair + ",");
	    }
	    out.println("]");
	    out.print("ConceptPositionList: [");
	    for (Position pos: e.getConceptPositionList()) {
	      out.print(pos + ",");
	    }
	    out.println("]");
	  }
	}
	for (Utterance utterance: result.getUtteranceList()) {
	  out.println("Utterance:");
	  out.println(" Id: " + utterance.getId());
	  out.println(" Utterance text: " + utterance.getString());
	  out.println(" Position: " + utterance.getPosition());
	  for (PCM pcm: utterance.getPCMList()) {
	    out.println("Phrase:");
	    out.println(" text: " + pcm.getPhrase().getPhraseText());
	    out.println(" Minimal Commitment Parse: " + pcm.getPhrase().getMincoManAsString());
	    out.println("Candidates:");
	    for (Ev ev: pcm.getCandidatesInstance().getEvList()) {
	      out.println(" Candidate:");
	      out.println("  Score: " + ev.getScore());
	      out.println("  Concept Id: " + ev.getConceptId());
	      out.println("  Concept Name: " + ev.getConceptName());
	      out.println("  Preferred Name: " + ev.getPreferredName());
	      out.println("  Matched Words: " + ev.getMatchedWords());
	      out.println("  Semantic Types: " + ev.getSemanticTypes());
	      out.println("  MatchMap: " + ev.getMatchMap());
	      out.println("  MatchMap alt. repr.: " + ev.getMatchMapList());
	      out.println("  is Head?: " + ev.isHead());
	      out.println("  is Overmatch?: " + ev.isOvermatch());
	      out.println("  Sources: " + ev.getSources());
	      out.println("  Positional Info: " + ev.getPositionalInfo());
	      out.println("  Pruning Status: " + ev.getPruningStatus());
	      out.println("  Negation Status: " + ev.getNegationStatus());
	    }
	    out.println("Mappings:");
	    for (Mapping map: pcm.getMappingList()) {
	      out.println(" Map Score: " + map.getScore());
	      for (Ev mapEv: map.getEvList()) {
		out.println("   Score: " + mapEv.getScore());
		out.println("   Concept Id: " + mapEv.getConceptId());
		out.println("   Concept Name: " + mapEv.getConceptName());
		out.println("   Preferred Name: " + mapEv.getPreferredName());
		out.println("   Matched Words: " + mapEv.getMatchedWords());
		out.println("   Semantic Types: " + mapEv.getSemanticTypes());
		out.println("   MatchMap: " + mapEv.getMatchMap());
		out.println("   MatchMap alt. repr.: " + mapEv.getMatchMapList());
		out.println("   is Head?: " + mapEv.isHead());
		out.println("   is Overmatch?: " + mapEv.isOvermatch());
		out.println("   Sources: " + mapEv.getSources());
		out.println("   Positional Info: " + mapEv.getPositionalInfo());
		out.println("   Pruning Status: " + mapEv.getPruningStatus());
		out.println("   Negation Status: " + mapEv.getNegationStatus());
	      }
	    }
	  }
	}
      } else {
	out.println("NULL result instance! ");
      }
    }
    this.api.resetOptions();
  }

  /**
   * Main method
   * 
   */
  public static void main(String[] args) 
    throws Exception{
    
	String serverhost = MetaMapApi.DEFAULT_SERVER_HOST; 
    int serverport = MetaMapApi.DEFAULT_SERVER_PORT; 	// default port
    int timeout = -1; // use default timeout
    
	String[]  opts = {"--ignore_word_order","--all_derivational_variants", 
		   		 "--all_acros_abbrs", "--word_sense_disambiguation",
		   		 "--ignore_stop_phrases", "--allow_overmatches", "--threshold", "10"}; 
     
    PrintStream output = System.out;
    List<String> options = new ArrayList<String>();
    
    for (int i=0;i<opts.length;i++){
    	options.add(opts[i]);	// set complex options	
    }

    System.out.println("#############################################");
    System.out.println("serverport: " + serverport);
    ApiTest frontEnd = new ApiTest(serverhost, serverport);
    System.out.println("options: " + options);
    System.out.println("#############################################\n");
    if (timeout > -1) {
      frontEnd.setTimeout(timeout);
    }
    
    // we send only one term to the mmserver
    frontEnd.process(" promotion", output, options);
    
    // disconnect
    frontEnd.api.disconnect();
    
  }
}

