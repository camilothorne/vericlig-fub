package mymetamap;


// java APIs
import java.net.URL;
import java.util.Set;


// VerbNet1 API
import vn.Inspector;


// VerbNet2 API
import edu.mit.jverbnet.data.FrameType;
import edu.mit.jverbnet.data.IFrame;
import edu.mit.jverbnet.data.IMember;
import edu.mit.jverbnet.data.IVerbClass;
import edu.mit.jverbnet.index.IVerbIndex;
import edu.mit.jverbnet.index.VerbIndex;


public class VerbNet {

	
	// fields
	String path = "/home/camilo/Desktop/Com-Sem-Frams/VerbNet/verbnet-3.2";// path
	String opt0; // pre arg options
	String opt1; // post arg options
	
	
	// constructor
	public VerbNet(String opt0, String opt1){
		this.opt0=opt0;
		this.opt1=opt1;
	}
	
	// verb data retrieval method 1
	public void getVerbA(String ver){
		Inspector.run(this.path + opt0 + ver + opt1); // calling the inspector class
	}
	
	// verb data retrieval method 2
	public void getVerbB(String ver) throws Exception {
	
		// make a url pointing to the Verbnet data
		String pathToVerbnet = this.path;
		URL url = new URL("file", null, pathToVerbnet);
		
		// construct the index and open it
		IVerbIndex index = new VerbIndex(url);
		index.open();
		
		// look up a verb class and print out some info
		IVerbClass verb = index.getRootVerb(ver+"-13.1");//?
		IMember member = verb.getMembers().get(0);
		Set keys = member.getWordnetTypes().keySet();
		IFrame frame = verb.getFrames().get(0);
		FrameType type = frame.getPrimaryType();
		String example = frame.getExamples().get(0);
		System.out.println("id: " + verb.getID());
		System.out.println("first wordnet keys: " + keys);
		System.out.println("first frame type: " + type.getID());
		System.out.println("first example: " + example);
		      
	}
	
	// main method
	public static void main(String[] args){
		String pre = " -i -Vcq -O";
		String post = "";
		VerbNet mvn = new VerbNet(pre, post);
		try{
			mvn.getVerbA("give");
			mvn.getVerbB("give");
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
}
