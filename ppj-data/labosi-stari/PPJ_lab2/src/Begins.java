import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Begins {

	private HashMap<String, HashMap<String, Integer>> beginsWith = new HashMap<>();

	private ArrayList<String> all = new ArrayList<>();
	private ArrayList<String> ntcs = new ArrayList<>();
	private HashSet<String> empty = new HashSet<>();


	private HashMap<String, ArrayList<Production>> grammar = new HashMap<>();

	public Begins(ArrayList<String> all, ArrayList<String> ntcs,
			HashMap<String, ArrayList<Production>> grammar) {
		super();
		this.all = all;
		this.ntcs = ntcs;
		this.grammar = grammar;
		getEmptyNtcs();
		generateBeginsSet();
	}

	public void getEmptyNtcs() {
		
		// ntc => $
		for (String ntc : ntcs) {
			for (Production production : grammar.get(ntc)) {
				if (production.right.size() == 0) {
					empty.add(production.left);
				}
			}
		}
		// ntc *=> $
		boolean notDone = true;
		while (notDone) {
			notDone = false;
			for (String ntc : ntcs) {
				for (Production production : grammar.get(ntc)) {
					boolean emptyFlag = true;
					for (String right : production.right) {
						if (!empty.contains(right)) {
							emptyFlag = false;
							break;
						}
					}

					if (!empty.contains(production.left) && emptyFlag) {
						notDone = true;
						empty.add(production.left);
					}
				}
			}
		}
	}
	private void generateBeginsSet() {
		initFalse();
		beginsDirectlyWith();
		transitiveEnvironment();
	}
	
	private void initFalse() {
		for (String c1 : all) {
			beginsWith.put(c1, new HashMap<String, Integer>());
			for (String c2 : all) {
				beginsWith.get(c1).put(c2, 0);
			}
		}
	}
	private void beginsDirectlyWith() {
		for (String ntc : ntcs) {
			for (Production production : grammar.get(ntc)) {
				for (int i = 0; i < production.right.size(); i++) {
					beginsWith.get(production.left).put(production.right.get(i), 1);
					if (!empty.contains(production.right.get(i)))
						break;
				}
			}
		}
	}
	private void transitiveEnvironment() {
		for (String c : beginsWith.keySet()) {
			beginsWith.get(c).put(c, 1);

			Queue<String> chars = new LinkedList<String>();
			HashSet<String> visited = new HashSet<>();

			for (String c2 : beginsWith.get(c).keySet()) {
				if (beginsWith.get(c).get(c2) == 1) {
					chars.add(c2);
				}
			}
			while (chars.size() > 0) {
				String c2 = chars.peek();
				chars.remove();
				if (visited.contains(c2))
					continue;
				visited.add(c2);
				beginsWith.get(c).put(c2, 1);
				for (String c3 : beginsWith.get(c2).keySet()) {
					if (beginsWith.get(c2).get(c3) == 1)
						chars.add(c3);
				}
			}

		}
	}
	public boolean isEmpty(String ntc) {
		return empty.contains(ntc);
	}
	public HashSet<String> getBeginsWith(String ntc) {
		HashSet<String> begins = new HashSet<>();
		for (String c : beginsWith.get(ntc).keySet()) {
			if (beginsWith.get(ntc).get(c) == 1) {
				begins.add(c);
			}
		}
		return begins;
	}
}