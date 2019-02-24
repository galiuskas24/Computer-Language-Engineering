import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Production implements Serializable {

	private static final long serialVersionUID = -7332780991256093809L;
	public String left;
	public ArrayList<String> right;
	public int id;

	public Production() {
		this.right = new ArrayList<String>();
	}

	public Production(String left, String right, int number) {
		this.right = new ArrayList<String>();
		this.left = left;
		this.id = number;
		if (!right.equals("$")) {
			this.right.addAll(Arrays.asList(right.trim().split(" ")));
		}
	}

	public Production(Production other) {
		this.right = new ArrayList<String>();
		this.left = new String(other.left);
		this.id = other.id;
		for(String r : other.right) {
			this.right.add(new String(r));
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((left == null) ? 0 : left.hashCode());
		result = prime * result + id;
		result = prime * result + ((right == null) ? 0 : right.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Production other = (Production) obj;
		if (left == null) {
			if (other.left != null)
				return false;
		} else if (!left.equals(other.left))
			return false;
		if (id != other.id)
			return false;
		if (right == null) {
			if (other.right != null)
				return false;
		} else if (!right.equals(other.right))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return left + "->" + right.toString();
	}

}
