import java.util.*;
import java.io.*;

public class DecisionTree {
	// some bits of java code that you may use if you wish.
	// assumes that the enclosing class has fields:
	private int numCategories;
	private int numAtts;
	private List<String> categoryNames;
	private List<String> attNames;
	private List<Instance> allInstances;

	public DecisionTree(String trainingSet, String testSet){
		readDataFile(trainingSet);
		decisionTreeTest(testSet);
	}

	public void decisionTreeTest(String testSet){
		//Make decision tree
		Node decisionTree = buildTree(new HashSet<Instance>(allInstances), attNames);
		//Print statistics
		int correctOne = 0;
		int incorrectOne = 0;
		int correctTwo = 0;
		int incorrectTwo = 0;
		//Find baseline predictor
		LeafNode baseLine = majorityClass(new HashSet<Instance>(allInstances));

		//Override training data with test data to do the checking
		readDataFile(testSet);

		//Go through instances of test set
		for (Instance test: allInstances){
			Node branch = decisionTree;
			//Recurse through decision tree until reaching leaf node
			while (!(branch instanceof LeafNode)){
				InternalNode internalBranch = (InternalNode) branch;
				//Find boolean values in test set instances
				for (int i=0; i< test.vals.size(); i++){
					//If True go left
					if (test.vals.get(i)){
						branch = internalBranch.getLeft();
					} else {
						//Else go right
						branch = internalBranch.getRight();
					}
				}
			}
			String classifiedName = ((LeafNode)branch).getName();
			//Check if the same
			if (categoryNames.get(test.getCategory()).equals(classifiedName) && classifiedName.equals(categoryNames.get(0))){
				correctOne++;
			} else if (categoryNames.get(test.getCategory()).equals(categoryNames.get(0))){
				incorrectOne++;
			}
			if (categoryNames.get(test.getCategory()).equals(classifiedName) && classifiedName.equals(categoryNames.get(1))){
				correctTwo++;
			} else if (categoryNames.get(test.getCategory()).equals(categoryNames.get(1))){
				incorrectTwo++;
			}
		}

		//Print statements
		int correct = correctOne + correctTwo;
		int incorrect = incorrectOne + incorrectTwo;
		System.out.printf("%s: %d out of %d correct\n", categoryNames.get(0), correctOne, incorrectOne+correctOne);
		System.out.printf("%s: %d out of %d correct", categoryNames.get(1), correctTwo, incorrectTwo+correctTwo);
		System.out.printf("\n\n\nAccuracy:\nDecision Tree Accuracy: %.2f%% (2 dp)\n" +
				"Baseline Accuracy (%s): %.2f%% (2 dp)",((double)correct/(double)(correct+incorrect))*100, 
				baseLine.getName(), baseLine.getProbability() *100);

		//Print tree out
		System.out.println("\n\nDecision Tree constructed:");
		printTree(decisionTree);
	}


	public void printTree(Node root){
		if (root instanceof LeafNode){
			((LeafNode)root).report("\t");
			return;
		}
		InternalNode branch = ((InternalNode)root);
		branch.report("\t");
		if (branch.getLeft() != null){
			printTree(branch.getLeft());
		}
		if (branch.getRight() != null){
			printTree(branch.getRight());
		}
	}

	/**
	 * Finds the most probable class given a set of instances
	 * - This is used to find the baseline Predictor
	 * 
	 * 
	 * @return most probable class
	 */
	public LeafNode majorityClass(Set<Instance> instances){
		int classOne = 0;
		int classTwo = 0;
		//Find out most probable class in the set 
		for (Instance instance: instances){
			if (instance.getCategory() == 0){
				classOne++;
			} else if (instance.getCategory() == 1) {
				classTwo++;
			}
		}
		//Calculate most probable class in set
		int baseLineIndex;
		double baseLineProbability;
		if (classOne > classTwo){
			//set most probable as class One
			baseLineIndex = 0;
			baseLineProbability =((double)classOne/(double)(classOne+classTwo));
		} else if (classOne == classTwo){
			//set most probable as random class
			baseLineIndex = ((Math.random()< 0.5)?0:1);
			baseLineProbability = ((double)classTwo/(double)(classOne+classTwo));
		} else {
			//set most probable as class Two
			baseLineIndex = 1;
			baseLineProbability = ((double)classTwo/(double)(classOne+classTwo));
		}
		return new LeafNode(categoryNames.get(baseLineIndex), baseLineProbability);
	}

	/**
	 * Checks whether or not all the instances in the set 
	 * is pure.
	 * 
	 * @param instances
	 * @return true if pure, false if not
	 */
	public boolean pureInstances(Set<Instance> instances){
		boolean pure = true;
		int pureClass = -1;
		//Find out if set is pure 
		for (Instance instance: instances){
			if (instance.category != pureClass && pureClass != -1){
				pure=false;
			}
			pureClass = instance.category;
		}
		return pure;
	}


	public Node buildTree(Set<Instance> instances, List<String> attributes){
		//Instances Empty
		if (instances.isEmpty()){
			//Baseline predictor - most probable class
			return majorityClass(new HashSet<Instance>(allInstances));
		}
		//Instances Pure
		if (pureInstances(instances)){
			return new LeafNode(majorityClass(instances).getName(), 1.0);
		}
		//Attributes Empty
		if (attributes.isEmpty()){
			return majorityClass(instances);
		} 
		//Find best attribute
		else {
			double weightedImpurity;
			double minWeightedImpurity = Double.MAX_VALUE;;
			String bestAttribute = null;
			Set<Instance> bestInstancesTrue = null;
			Set<Instance> bestInstancesFalse = null;
			//For each attribute
			for (String attribute: attributes){
				int index = -1;
				Set<Instance> trueSet = new HashSet<Instance>();
				Set<Instance> falseSet = new HashSet<Instance>();
				//Find index of the attribute in the attributes list field
				for (String atts: attNames){
					index++;
					if (attribute.equals(atts)){
						break;
					}
				}
				//separate instances into two sets
				for (Instance instance: instances){
					if (instance.getAtt(index) == true){
						//Instances that are true
						trueSet.add(instance);
					} else {
						//Instances that are false
						falseSet.add(instance);
					}
				}
				//Used to find impurity
				int setOneCount1 = 0;
				int setOneCount2 = 0;
				int setTwoCount1 = 0;
				int setTwoCount2 = 0;
				//Compute impurity of each of the sets
				for (Instance trues:trueSet){
					if (trues.getCategory() == 0){
						setOneCount1++;
					} else {
						setOneCount2++;
					}
				}
				for (Instance falses:falseSet){
					if (falses.getCategory() == 0){
						setTwoCount1++;
					} else {
						setTwoCount2++;
					}
				}
				//impurity
				double trueSetImpurity = 0;
				double falseSetImpurity = 0;
				if (!trueSet.isEmpty()){
					trueSetImpurity = ((double)setOneCount1/(double)trueSet.size()) * ((double)setOneCount2/(double)trueSet.size());
				}
				if (!falseSet.isEmpty()){
					falseSetImpurity = ((double)setTwoCount1/(double)falseSet.size()) * ((double)setTwoCount2/(double)falseSet.size());
				}
				//Find probability of each set
				double total = trueSet.size() + falseSet.size();
				double probTrueSet = (double)trueSet.size()/(double)total;
				double probFalseSet = (double)falseSet.size()/(double)total;
				//Find the WEIGHTED AVERAGE PURITY
				weightedImpurity = (trueSetImpurity * probTrueSet) + (falseSetImpurity * probFalseSet);

				//If wegihted average purity of these sets is best so far
				if (weightedImpurity < minWeightedImpurity){
					bestAttribute = attribute;
					bestInstancesTrue = trueSet;
					bestInstancesFalse = falseSet;
					minWeightedImpurity = weightedImpurity;
				}
			}
			//Build subtrees using remaining attributes
			ArrayList<String> remainingAttributes = new ArrayList<String>();
			for (String names: attributes){
				if (!names.equals(bestAttribute)){
					remainingAttributes.add(names);
				}
			}
			Node left = buildTree(bestInstancesTrue, remainingAttributes);	
			Node right = buildTree(bestInstancesFalse, remainingAttributes);

			return new InternalNode(bestAttribute,left,right);
		}
	}


	private void readDataFile(String fname){
		/* format of names file:
		 * names of categories, separated by spaces
		 * names of attributes
		 * category followed by true's and false's for each instance
		 */
		System.out.println("Reading data from file "+fname);
		try {
			Scanner din = new Scanner(new File(fname));

			categoryNames = new ArrayList<String>();
			for (Scanner s = new Scanner(din.nextLine()); s.hasNext();) categoryNames.add(s.next());
			numCategories=categoryNames.size();
			System.out.println(numCategories +" categories");

			attNames = new ArrayList<String>();
			for (Scanner s = new Scanner(din.nextLine()); s.hasNext();) attNames.add(s.next());
			numAtts = attNames.size();
			System.out.println(numAtts +" attributes");

			allInstances = readInstances(din);
			din.close();
		}
		catch (IOException e) {
			throw new RuntimeException("Data File caused IO exception");
		}
	}

	private List<Instance> readInstances(Scanner din){
		/* instance = classname and space separated attribute values */
		List<Instance> instances = new ArrayList<Instance>();
		String ln;
		while (din.hasNext()){ 
			Scanner line = new Scanner(din.nextLine());
			instances.add(new Instance(categoryNames.indexOf(line.next()), line));
		}
		System.out.println("Read " + instances.size()+" instances");
		return instances;
	}

	public class Instance {
		private int category;
		private List<Boolean> vals;

		public Instance(int cat, Scanner s){
			category = cat;
			vals = new ArrayList<Boolean>();
			while (s.hasNextBoolean()) vals.add(s.nextBoolean());
		}

		public boolean getAtt(int index){
			return vals.get(index);
		}

		public int getCategory(){
			return category;
		}

		public String toString(){
			StringBuilder ans = new StringBuilder(categoryNames.get(category));
			ans.append(" ");
			for (Boolean val : vals)
				ans.append(val?"true  ":"false ");
			return ans.toString();
		}
	}
}
