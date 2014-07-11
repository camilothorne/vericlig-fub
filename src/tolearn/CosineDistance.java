package tolearn;


import java.util.Enumeration;


import weka.classifiers.bayes.BayesNet;
import weka.classifiers.bayes.net.estimate.BayesNetEstimator;
import weka.core.Attribute;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.neighboursearch.PerformanceStats;
import weka.estimators.Estimator;
 

//public class CosineDistance implements DistanceFunction, OptionHandler, Serializable, RevisionHandler{
public class CosineDistance extends EuclideanDistance{
 
	
	// fields
	private static final long serialVersionUID = 1L;
	public Instances 	m_Data 	= 	null;
	public String 		version =	"1.0";
  
 
	@Override
	public double distance(Instance arg0, Instance arg1) {
		// TODO Auto-generated method stub
		return distance(arg0, arg1, Double.POSITIVE_INFINITY, null);
	}
 
 
	@Override
	public double distance(Instance arg0, Instance arg1, PerformanceStats arg2) {
		 // TODO Auto-generated method stub
		 return distance(arg0, arg1, Double.POSITIVE_INFINITY, arg2);
	}
 
 
	@Override
	public double distance(Instance arg0, Instance arg1, double arg2) {
		 // TODO Auto-generated method stub
		 return distance(arg0, arg1, arg2, null);
	}
	 
	 
	@Override
	public double distance(Instance first, Instance second, double cutOffValue,PerformanceStats arg3) {
	
		 double distanceU 		= 0;
		 
		 int firstI, secondI;
		 
		 int firstNumValues 	= first.numValues();		// length of input vector
		 int secondNumValues 	= second.numValues();		// length of input vector
		 int numAttributes 		= m_Data.numAttributes();	// num of independent features
		 int classIndex 		= m_Data.classIndex();		// dependent feature
		 
		 double normA, normB;
		 normA = 0;
		 normB = 0;
	 
		 for (int p1 = 0, p2 = 0; p1 < firstNumValues || p2 < secondNumValues;) {
			 
			 if (p1 >= firstNumValues)
				 firstI = numAttributes;
			 else
				 firstI = first.index(p1);
	 
			 if (p2 >= secondNumValues)
				 secondI = numAttributes;
			 else
				 secondI = second.index(p2);
	 
			 if (firstI == classIndex) {
				 p1++;
				 continue;
			 }
	 
			 if (secondI == classIndex) {
				 p2++;
				 continue;
			 }
	 
			 double diff;
	 
			 if (firstI == secondI) { // input vectors normalized
				 
				 diff = difference(firstI, first.valueSparse(p1), second.valueSparse(p2));
				 normA += Math.pow(first.valueSparse(p1), 2);
				 normB += Math.pow(second.valueSparse(p2), 2);
				 p1++;
				 p2++;
				 
			 } else if (firstI > secondI) { // input vectors not normalized
				 
				 diff = difference(secondI, 0, second.valueSparse(p2));
				 normB += Math.pow(second.valueSparse(p2), 2);
				 p2++;
				 
			 } else { // input vectors not normalized
				 
				 diff = difference(firstI, first.valueSparse(p1), 0);
				 normA += Math.pow(first.valueSparse(p1), 2);
				 p1++;
				 
			 }
			 
			 if (arg3 != null)
				arg3.incrCoordCount();
			 
			 distanceU = updateDistance(distanceU, diff);
			 			 
			 if (distanceU > cutOffValue)
				return Double.POSITIVE_INFINITY;
			 
		 }
	   
		 //do the post here, don't depends on other functions
		 //System.out.println(distance + " " + normA + " "+ normB);
		 
		 double distance = distanceU/(Math.sqrt(normA)*Math.sqrt(normB));
		 
		 distance = 1 - distance;
		 
		 if(distance < 0 || distance > 1)
			 System.err.println("unknown: " + distance);
		 
		 return distance;
		 
	}
  
 
	public double updateDistance(double currDist, double diff){
		
		 double result;
		 result 	= 	currDist;
		 result 	+= 	diff;
		 return result;
	}
  
 
	public double difference(int index, double val1, double val2){
		 switch(m_Data.attribute(index).type()){
		 case Attribute.NOMINAL:
			 //return Double.NaN;
			 return 1;
	   //break;
		 case Attribute.NUMERIC:
			 return val1 * val2;
	   //break;
		 }
		 return Double.NaN;
	}
  
 
	@Override
	public String getAttributeIndices() {
		 // TODO Auto-generated method stub
		 return null;
	}
	 
	 
	@Override
	public Instances getInstances() {
		 // TODO Auto-generated method stub
		 return m_Data;
	}
	 
	 
	@Override
	public boolean getInvertSelection() {
		 // TODO Auto-generated method stub
		 return false;
	}
	 
	 
	@Override
	public void postProcessDistances(double[] arg0) {
		 // TODO Auto-generated method stub
	}
	 
	 
	@Override
	public void setAttributeIndices(String arg0) {
		 // TODO Auto-generated method stub
	}
	 
	 
	@Override
	public void setInstances(Instances arg0) {
		 // TODO Auto-generated method stub
		m_Data = arg0;
	}
	 
	 
	@Override
	public void setInvertSelection(boolean arg0) {
		 // TODO Auto-generated method stub
		 //do nothing
	}
	 
	 
	@Override
	public void update(Instance arg0) {
		 // TODO Auto-generated method stub  
		 //do nothing
	}
	 
	 
	@Override
	public String[] getOptions() {
		 // TODO Auto-generated method stub
		 return null;
	}
	 
	 
	@Override
	public Enumeration listOptions() {
		 // TODO Auto-generated method stub
		 return null;
	}
	 
	 
	@Override
	public void setOptions(String[] arg0) throws Exception {
		 // TODO Auto-generated method stub
	}
	 
	 
	@Override
	public String getRevision() {
		 // TODO Auto-generated method stub
		 return "Cosine Distance function writtern by Camilo, version " + version;
	}
	 
	
	// main method
	public static void main(String[] args) throws Exception{
		
		 String src = "/home/camilo/Desktop/Com-Sem-Frams/meta-map/meta-map-gold/semrepB-small.arff";
		 DataSource source = new DataSource(src);
		 Instances data = source.getDataSet();
		 
	     // selecting dependent variable
	     int num = data.numAttributes()-1;
	     // last of train set
	     data.setClassIndex(num);
		 
		 // conditional probability
		 BayesNet net = new BayesNet();
		 net.buildClassifier(data);
		 net.initStructure();
		 net.buildStructure();
		 
		 BayesNetEstimator est = net.getEstimator();
		 est.estimateCPTs(net);
		 est.setAlpha(0);
		 
		 int nodes = net.getNrOfNodes();
		 
		 //System.out.println(nodes);
		 
		 for (int i=0;i<nodes-1;i++){
			 System.out.println(net.getNodeName(i));
			 System.out.println(net.getNrOfParents(i));
			 System.out.println(net.getCardinality(i));
			 int parents = net.getNrOfParents(i);
			 int values  = net.getCardinality(i);
			 for (int j=0;j<parents;j++){
				 for (int k=0;k<values;k++){
					 System.out.println(net.getProbability(i, j, k));
				 } 
			 }
			 //System.out.println(net.getProbability(i, net.getNrOfParents(i), net.getCardinality(i)));
		 }
		 
		 
		 // custom cosine distance
		 CosineDistance cd = new CosineDistance();
		 cd.setInstances(data);
		 System.out.println("*****************************************");
		 System.out.println(cd.distance(data.instance(0), data.instance(1)));
		 System.out.println("*****************************************");
		 System.out.println(cd.distance(data.instance(1), data.instance(2)));
		 
		 
	}


}

