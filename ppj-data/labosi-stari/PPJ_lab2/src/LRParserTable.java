import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class LRParserTable implements Serializable {

	private static final long serialVersionUID = -3044873486336643411L;
	private DKA dka;
	private ArrayList<String> tcs = new ArrayList<>();
	private ArrayList<String> ntcs = new ArrayList<>();
	public HashMap<Integer, HashMap<String, Action>> actions = new HashMap<>();
	public HashMap<Integer, HashMap<String, Integer>> newState = new HashMap<>();

	public LRParserTable(DKA dka, ArrayList<String> tcs, ArrayList<String> ntcs) {
		this.dka = dka;
		this.tcs = tcs;
		this.ntcs = ntcs;
		actions();
		newState();
	}

	private void actions() {

		for (int i = 0; i < dka.noOfStates; i++) {
			actions.put(i, new HashMap<String, Action>());
			for (String tc : tcs) {
				actions.get(i).put(tc, new Action());
			}
			actions.get(i).put("$", new Action());
		}
		calculateShift();
		calculateOtherActions();
	}

	private void calculateShift() {
		for (DKA.Transition transition : dka.getTransitions()) {
			if (!tcs.contains(transition.character))
				continue;

			for (State LRState : dka.getGroupState().get(transition.from)) {
				if (LRState.dotPosition >= LRState.production.right.size())
					continue;
				if (LRState.production.right.get(LRState.dotPosition).equals(transition.character)) {
					actions.get(transition.from).get(transition.character).shift = transition.to;
				}
			}
		}
	}
	private void calculateOtherActions() {
		for (Integer gState : dka.getGroupState().keySet()) {
			for (State LRState : dka.getGroupState().get(gState)) {
				if (LRState.dotPosition != LRState.production.right.size())
					continue;

				if (LRState.production.left.equals(ntcs.get(0))) {
					actions.get(gState).get("$").accept = true;
					continue;
				}
				calculateReduce(gState,LRState);

			}
		}
	}
	private void calculateReduce(Integer gState, State LRState) {
		for (String transChar : LRState.starts) {
			Action action = actions.get(gState).get(transChar);
			
			// shift/reduce
			if (action.shift != -1) {
				continue;
			}
			// reduce/reduce
			if (action.reduce != null) {
				if (action.reduce.id > LRState.production.id) {
					action.reduce = LRState.production;
				}
				continue;
			}
			action.reduce = LRState.production;
		}
	}
	private void newState() {
		for (int i = 0; i < dka.noOfStates; i++) {
			newState.put(i, new HashMap<String, Integer>());
			for (String ntc : ntcs) {
				newState.get(i).put(ntc, -1);
			}
		}
		for (DKA.Transition transition : dka.getTransitions()) {
			if (!ntcs.contains(transition.character))
				continue;
			newState.get(transition.from).put(transition.character, transition.to);
		}
	}

	public HashMap<Integer, HashMap<String, Action>> getActions() {
		return actions;
	}

	public HashMap<Integer, HashMap<String, Integer>> getNewState() {
		return newState;
	}

}