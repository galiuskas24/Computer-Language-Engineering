import java.util.HashSet;

public class GroupState {

		public HashSet<State> states = new HashSet<State>();

		public GroupState() {
		}

		public GroupState(HashSet<State> states) {
			for (State s : states) {
				this.states.add(new State(s));
			}
		}
		public GroupState(State state) {
			this.states.add(State.nextState(state));
		}

		public void addStates(HashSet<State> otherStates) {
			for(State s : otherStates) {
				if(!this.states.contains(s))
					this.states.add(new State(s));
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((states == null) ? 0 : states.hashCode());
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
			GroupState other = (GroupState) obj;
			if (states == null) {
				if (other.states != null)
					return false;
			} else if (!states.equals(other.states))
				return false;
			return true;
		}
	}