package wordnet;

import java.io.BufferedReader;


/** WSD test
 * 
 * @author camilo
 *
 */
public class MyLeskTest extends MyLesk {

	/**
	 * 
	 */
	public MyLeskTest() {}

	/**
	 * @param path
	 * @param file
	 * @throws Exception
	 */
	public MyLeskTest(String path, BufferedReader file) throws Exception {
		super(path, file);
	}

	/**
	 * @param sentence
	 * @throws Exception
	 */
	public MyLeskTest(String sentence) throws Exception {
		super(sentence);
	}

	/**
	 * @param sentences
	 * @throws Exception
	 */
	public MyLeskTest(String[] sentences) throws Exception {
		super(sentences);
	}	
	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		String inputText1 = "BabelNet is both a multilingual encyclopedic dictionary and a semantic network";
		String inputText2 = "Strategies for preclinical evaluation of "
				+ "dendritic cell subsets for promotion of transplant tolerance in the nonhuman primate.";
		
		String[] sentences = {inputText1,inputText2};
		
		new MyLeskTest(sentences);
		
	}

}
