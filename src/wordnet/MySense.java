package wordnet;

import java.util.List;

import edu.smu.tspell.wordnet.NounSynset;

/**
 * 
 * @author camilo
 *
 */
public class MySense implements Comparable<MySense>{
	
	
	public NounSynset nounSynset;
	public String word;
	public String gloss;
	public String[] example;
	private MyLemmatizer lemmatizer;
	public float rank;
	
	
	/** constructor
	 * 
	 * @param word
	 * @param nounSynset
	 * @param gloss
	 * @param examples
	 */
	public MySense(String word, NounSynset nounSynset, String gloss, String[] examples, 
			MyLemmatizer lemmatizer){
		
		this.lemmatizer = lemmatizer;
		this.rank = 0;
		this.word = word;
		this.gloss = gloss;
		this.example = examples;
		this.nounSynset = nounSynset;
		
	}
	
	
	/** overlap frequency
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public float computeSimGloss(String[] context){
		List<String> lemmas = this.lemmatizer.lemmatize(gloss);
		String[] mygloss = lemmas.toArray(new String[lemmas.size()]);
		int matches = 0;
		for (String s1 : mygloss){
			for (String s2 : context){
				if (s1.equals(s2)){
					matches++;
				}
			}
		}
		// co-occurence counts with Laplace smoothing (alpha = 1)
		return ((float)(matches + 1)/(float)(mygloss.length + context.length + 1));
	}
	
	
	/** count overlap
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public int countOverlap(String[] context){
		List<String> lemmas = this.lemmatizer.lemmatize(gloss);
		String[] mygloss = lemmas.toArray(new String[lemmas.size()]);
		int matches = 0;
		for (String s1 : mygloss){
			for (String s2 : context){
				if (s1.equals(s2)){
					matches++;
				}
			}
		}
		return matches;
	}
	
	
	/** comparator
	 * 
	 * @param sense
	 */
	public int compareTo(MySense s) {
		return Float.compare(this.rank, s.rank);
	}
	
}
