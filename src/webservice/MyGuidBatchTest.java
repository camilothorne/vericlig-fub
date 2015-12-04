package webservice;




public class MyGuidBatchTest extends MyGuidBatch{
	
	// constructor
	public MyGuidBatchTest(String path){
		super(path);
	}
	
	// annotate test guideline
	public static void main(String[] args) {
		
		new MyGuidBatchTest("/home/camilo/meta-map-exp/mmap-exp-data/test.guid");

	}

}
