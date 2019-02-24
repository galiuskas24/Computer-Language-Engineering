import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class DKA {

	public int noOfStates;
	private HashMap<Integer, HashSet<State>> groupState = new HashMap<Integer, HashSet<State>>();
	private HashMap<HashSet<State>, Integer> groupStateID = new HashMap<HashSet<State>, Integer>();
	private HashSet<Transition> transitions = new HashSet<Transition>();
	private HashSet<TransitionGS> GSTransitions = new HashSet<TransitionGS>();
	private HashSet<GroupState> setOfGroupStates = new HashSet<GroupState>();
	private Queue<GroupState> states = new LinkedList<GroupState>();

	public DKA(EpsNKA enka) {

		noOfStates = 0;
		constructDKA(enka);

	}
	
	private void constructDKA(EpsNKA enka) {
		defineGroupStates(enka);
		defineTransitions();
	}

	private void defineGroupStates(EpsNKA enka) {
		
		// initial state
		HashSet<State> initial = new HashSet<State>();
		initial.add(enka.getStates().get(0));
		groupState.put(0, enka.epsSurrounding(initial));
		groupStateID.put(enka.epsSurrounding(initial), 0);
		states.add(new GroupState(enka.epsSurrounding(initial)));
		++noOfStates;
		
		while (!states.isEmpty()) {

			GroupState currentGS = states.peek();
			states.remove();

			// terminal characters
			for (String c : enka.getTcs()) {
				GroupState nextState = new GroupState(enka.transition(currentGS.states, c));
				nextState.addStates(enka.epsSurrounding(nextState.states));
				if (nextState.states.isEmpty())
					continue;
				GSTransitions.add(new TransitionGS(currentGS.states, nextState.states, c));
				if (!states.contains(nextState) && !setOfGroupStates.contains(nextState)) {
					states.add(nextState);
					setOfGroupStates.add(nextState);
				}
			}

			// nonterminal characters
			for (String c : enka.getNtcs()) {
				GroupState nextState = new GroupState(enka.transition(currentGS.states, c));
				nextState.addStates(enka.epsSurrounding(nextState.states));
				if (nextState.states.isEmpty())
					continue;
				GSTransitions.add(new TransitionGS(currentGS.states, nextState.states, c));
				if (!states.contains(nextState) && !setOfGroupStates.contains(nextState)) {
					states.add(nextState);
					setOfGroupStates.add(nextState);
				}
			}
		}

		for (GroupState c : setOfGroupStates) {
			groupStateID.put(c.states, noOfStates);
			groupState.put(noOfStates++, c.states);
		}

	}

	private void defineTransitions() {

		for (TransitionGS transition : GSTransitions) {
			Integer fromID = groupStateID.get(transition.from);
			Integer toID = groupStateID.get(transition.to);
			transitions.add(new Transition(fromID, toID, transition.character));
		}
	}

	public HashMap<Integer, HashSet<State>> getGroupState() {
		return groupState;
	}

	public HashSet<Transition> getTransitions() {
		return transitions;
	}

	public class Transition {

		public Integer from;
		public Integer to;
		public String character;

		public Transition(Integer from, Integer to, String character) {
			this.from = from;
			this.to = to;
			this.character = character;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((character == null) ? 0 : character.hashCode());
			result = prime * result + ((from == null) ? 0 : from.hashCode());
			result = prime * result + ((to == null) ? 0 : to.hashCode());
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
			Transition other = (Transition) obj;
			if (character == null) {
				if (other.character != null)
					return false;
			} else if (!character.equals(other.character))
				return false;
			if (from == null) {
				if (other.from != null)
					return false;
			} else if (!from.equals(other.from))
				return false;
			if (to == null) {
				if (other.to != null)
					return false;
			} else if (!to.equals(other.to))
				return false;
			return true;
		}
	}
}