import java.util.HashSet;

public class State {

	public Production production;
	public int dotPosition = 0;
	public HashSet<String> starts = new HashSet<String>();

	public State() {
		this.production = new Production();
	}

	public State(State other) {
		this.production = new Production(other.production);
		this.dotPosition = other.dotPosition;
		this.starts = new HashSet<String>();
		for (String str : other.starts)
			this.starts.add(new String(str));
	}

	public State(Production production, int dotPosition) {
		this.production = production;
		this.dotPosition = dotPosition;
	}

	public boolean alive() {
		return dotPosition < production.right.size();
	}

	public String afterDot() {
		return production.right.get(dotPosition);
	}

	public static State nextState(State state) {
		State newState = new State(new Production(state.production),state.dotPosition+1);
		newState.starts = new HashSet<String>();
		for (String begin : state.starts)
			newState.starts.add(new String(begin));
		return newState;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dotPosition;
		result = prime * result + ((production == null) ? 0 : production.hashCode());
		result = prime * result + ((starts == null) ? 0 : starts.hashCode());
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
		State other = (State) obj;
		if (dotPosition != other.dotPosition)
			return false;
		if (production == null) {
			if (other.production != null)
				return false;
		} else if (!production.equals(other.production))
			return false;
		if (starts == null) {
			if (other.starts != null)
				return false;
		} else if (!starts.equals(other.starts))
			return false;
		return true;
	}

}