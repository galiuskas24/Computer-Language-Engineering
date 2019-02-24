import java.util.*;

public class EpsNKA {

	public EpsNKA() {
		
	}

	private String initialState;
	private ArrayList<String> ntcs;
	private ArrayList<String> tcs;
	private Begins beginsWith;
	private HashMap<String, ArrayList<Production>> grammar = new HashMap<>();

	private ArrayList<State> states;
	private ArrayList<Transition> transitions;

	public EpsNKA(
			String initialState,
			HashMap<String, ArrayList<Production>> grammar,
			Begins beginsWith,
			ArrayList<String> ntcs,
			ArrayList<String> tcs) {

		this.initialState = initialState;
		this.ntcs = ntcs;
		this.tcs = tcs;
		this.beginsWith = beginsWith;
		this.grammar = grammar;

		states = new ArrayList<State>();
		transitions = new ArrayList<Transition>();

		for (Production production : grammar.get(this.initialState)) {
			State state = new State(production, 0);

			HashSet<String> starts = new HashSet<String>();
			starts.add("$");
			state.starts = starts;
			states.add(state);
		}

	}

	public void generateEpsNKA() {

		for(int i=0; i<states.size();i++) {
			State current = states.get(i);
			if (!current.alive()) {
				continue;
			}
			State next = State.nextState(current);
			if (!states.contains(next)) {
				states.add(next);
			}
			transitions.add(new Transition(current, next, current.afterDot()));

			if (tcs.contains(current.afterDot())) {
				continue;
			}

			HashSet<String> begins = new HashSet<String>();
			boolean flag = true;

			for (int j = current.dotPosition + 1; j < current.production.right.size(); ++j) {
				String currentChar = current.production.right.get(j);
				for (String c : beginsWith.getBeginsWith(currentChar)) {
					if (ntcs.contains(c)) continue;
					begins.add(new String(c));
				}
				if (!beginsWith.isEmpty(currentChar)) break;
			}

			for (int j = current.dotPosition + 1; j < current.production.right.size(); ++j) {
				String currentChar = current.production.right.get(j);
				if (!beginsWith.isEmpty(currentChar)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				for (String character : current.starts)
					begins.add(new String(character));
			}

			for (Production production : grammar.get(current.afterDot())) {
				next = new State(production, 0);
				next.starts = begins;
				transitions.add(new Transition(current, next, "$"));
				if (!states.contains(next)) {
					states.add(next);
				}
			}
		}
	}

	public HashSet<State> transition(HashSet<State> from, String e) {
		HashSet<State> states = new HashSet<State>();
		for (Transition transition : transitions) {
			if (from.contains(transition.from) && transition.character.equals(e))
				states.add(transition.to);
		}
		return states;
	}

	public HashSet<State> epsSurrounding(HashSet<State> start) {
		HashSet<State> epsSurr = new HashSet<State>();
		for (State s : start) {
			epsSurr.add(new State(s));
		}
		boolean newEps;
		do {
			newEps = false;
			for (Transition transition : transitions) {
				if (!transition.character.equals("$"))
					continue;
				if (epsSurr.contains(transition.from) && !epsSurr.contains(transition.to)) {
					newEps = true;
					epsSurr.add(new State(transition.to));
				}
			}
		} while (newEps);
		return epsSurr;
	}

	public ArrayList<State> getStates() {
		return states;
	}

	public ArrayList<Transition> getTransitions() {
		return transitions;
	}

	public ArrayList<String> getTcs() {
		return tcs;
	}

	public ArrayList<String> getNtcs() {
		return ntcs;
	}
	
	private class Transition {

		public State from;
		public State to;
		public String character;

		public Transition(State from, State to, String character) {
			this.from = from;
			this.to = to;
			this.character = character;
		}

		@Override
		public String toString() {
			return character + "|" + from.toString() + " -> " + to.toString();
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
