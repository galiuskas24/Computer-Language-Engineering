import java.io.Serializable;

public class Action implements Serializable {

	private static final long serialVersionUID = -5260939887137365801L;
	public boolean accept = false;
	public int shift = -1;
	public Production reduce = null;

	public Action() {
	}

	public boolean accept() {
		return accept;
	}

	public boolean shift() {
		return shift != -1;
	}

	public boolean reduce() {
		return reduce != null;
	}

	public boolean noAction() {
		return shift == -1 && reduce == null && accept == false;
	}
}