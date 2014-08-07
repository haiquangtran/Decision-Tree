
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//Read in files
		if (args.length == 2){
			String trainingSet = args[0];
			String testSet = args[1];
			
			//Start program
			DecisionTree data = new DecisionTree(trainingSet, testSet);

		} else {
			System.out.println("You must pass in two files into the arguments. ");
		}

	}

}
