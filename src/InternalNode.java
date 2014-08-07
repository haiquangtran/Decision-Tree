import java.util.List;
import java.util.Set;



public class InternalNode implements Node{
	private Node left;
	private Node right;
	private String name;

	public InternalNode(String bestAttribute, Node left, Node right){
		this.name = bestAttribute;
		this.left = left;
		this.right = right;
	}

	public Node getLeft(){
		return left;
	}

	public Node getRight(){
		return right;
	}

	public String getName(){
		return name;
	}

	public void report(String indent){
		System.out.format("%s%s = True:\n",
				indent, name);
		left.report(indent+" ");
		System.out.format("%s%s = False:\n",
				indent, name);
		right.report(indent+" ");
	}

}
