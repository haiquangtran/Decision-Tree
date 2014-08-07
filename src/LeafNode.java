
public class LeafNode implements Node{
	private String name;
	private double probability;

	public LeafNode(String name, Double probability){
		this.name = name;
		this.probability = probability;
	}

	public String getName(){
		return name;
	}

	public double getProbability(){
		return probability;
	}

	public void report(String indent){
		System.out.format("%sClass %s, prob=%4.2f%%\n",
				indent, name, probability * 100);
	}

}
