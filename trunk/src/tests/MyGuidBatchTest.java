package tests;


import mymetamap.MyGuidBatch;


public class MyGuidBatchTest extends MyGuidBatch{
	
	// constructor
	public MyGuidBatchTest(String path){
		super(path);
	}
	
	// annotate test guideline
	public static void main(String[] args) {
		
		new MyGuidBatchTest("/home/camilothorne/meta_map_bionlp/meta-map-gold/mmap-exp-data/test.guid");

	}

}
