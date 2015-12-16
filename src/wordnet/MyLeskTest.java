/**
 * 
 */
package wordnet;

import java.io.BufferedReader;

/**
 * @author camilo
 *
 */
public class MyLeskTest extends MyLesk {

	/**
	 * 
	 */
	public MyLeskTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param path
	 * @param file
	 * @throws Exception
	 */
	public MyLeskTest(String path, BufferedReader file) throws Exception {
		super(path, file);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sentence
	 * @throws Exception
	 */
	public MyLeskTest(String sentence) throws Exception {
		super(sentence);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param sentences
	 * @throws Exception
	 */
	public MyLeskTest(String[] sentences) throws Exception {
		super(sentences);
		// TODO Auto-generated constructor stub
	}	
	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String inputText1 = "BabelNet is both a multilingual encyclopedic dictionary and a semantic network";
		String inputText2 = "Strategies for preclinical evaluation of "
				+ "dendritic cell subsets for promotion of transplant tolerance in the nonhuman primate.";
		
		String[] sentences = {inputText1,inputText2};
		
		new MyLeskTest(sentences);
		
	}

}
