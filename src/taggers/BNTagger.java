package taggers;


import java.io.IOException;

import babeltest.BabelTagger;

/**
 * 
 * @author camilo
 *
 */
public class BNTagger {
	
	// field
	BabelTagger tagger;
	
	// constructor
	public BNTagger(){
		this.tagger = new BabelTagger();
	}

	// annotation method
	// (interface/wrapper for BabelTagger)
	public void exploreWord(String word){
		try {
			//BabelTagger tagger = new BabelTagger();
			this.tagger.exploreWord(word);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// main (for tests)
	static public void main(String[] args) throws Exception
	{
		BNTagger bn = new BNTagger();
		bn.exploreWord("bank");
	}

}
