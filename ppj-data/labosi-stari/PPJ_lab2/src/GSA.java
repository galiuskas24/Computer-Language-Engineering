import java.util.*;
import java.io.*;

public class GSA {

	private static ArrayList<String> allChars = new ArrayList<>();
	private static ArrayList<String> nonterminalChars = new ArrayList<>();
	private static ArrayList<String> terminalChars = new ArrayList<>();
	private static HashSet<String> syncChars = new HashSet<>();
	private static LRParserTable LRParserTable;
	private static LineSpec lineSpec = LineSpec.NONTERMINALLINE;
	private static HashMap<String, ArrayList<Production>> grammar = new HashMap<>();

	public static void main(String[] args) throws IOException {
		parseInput("test.san");

		init();
		Begins beginsWith = new Begins(allChars, nonterminalChars, grammar);
		EpsNKA enka = new EpsNKA(nonterminalChars.get(0), grammar, beginsWith, nonterminalChars, terminalChars);
		enka.generateEpsNKA();

		DKA dka = new DKA(enka);

		LRParserTable = new LRParserTable(dka, terminalChars, nonterminalChars);

		generateFilesForAnalyser();
	}

	private static void parseInput(String input) throws IOException {
		// BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(input)));

		String line;

		// process ntc
		line = br.readLine();
		process(line);
		for (String c : nonterminalChars) {
			grammar.put(c, new ArrayList<Production>());
		}

		// process tc
		line = br.readLine();
		process(line);

		// process syncc
		line = br.readLine();
		process(line);

		String left = "";
		int productionID = 1;
		// process productions
		while ((line = br.readLine()) != null) {
			if (line.equals("")) {
				continue;
			}
			if (!line.startsWith(" ")) {
				left = line;
			} else {
				Production production = new Production(left, line.substring(1), productionID++);
				grammar.get(left).add(production);
			}
		}

		br.close();
	}

	private static void init() {
		String first = nonterminalChars.get(0);
		String init = first.concat("'");
		nonterminalChars.add(0, init);

		grammar.put(init, new ArrayList<Production>());
		Production production = new Production(init, first, 0);
		grammar.get(init).add(production);

		allChars.add(0, init);
	}

	private static void generateFilesForAnalyser() throws IOException {

		ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream("analyzer/syncChars.ser"));
		stream.writeObject(syncChars);
		stream.close();
		stream = new ObjectOutputStream(new FileOutputStream("analyzer/actions.ser"));
		stream.writeObject(LRParserTable.getActions());
		stream.close();
		stream = new ObjectOutputStream(new FileOutputStream("analyzer/newState.ser"));
		stream.writeObject(LRParserTable.getNewState());
		stream.close();

	}

	public static void process(String line) {

		switch (lineSpec) {
		case NONTERMINALLINE:
			if (line.startsWith("%V")) {
				processNonProduction(line, nonterminalChars, allChars);
			}
			lineSpec = LineSpec.TERMINALLINE;
			break;
		case TERMINALLINE:
			if (line.startsWith("%T")) {
				processNonProduction(line, terminalChars, allChars);
			}
			lineSpec = LineSpec.SYNCLINE;
			break;
		case SYNCLINE:
			if (line.startsWith("%Syn")) {
				processNonProduction(line, syncChars);
			}
			break;
		default:
			System.err.println("Should never come here.");
			System.exit(-1);
		}
	}

	public static void processNonProduction(String line, HashSet<String> collection) {
		collection.addAll(Arrays.asList(line.trim().split(" ")));
		collection.remove("%Syn");
	}

	public static void processNonProduction(String line, ArrayList<String> collection1, ArrayList<String> collection2) {
		collection1.addAll(Arrays.asList(line.trim().split(" ")));
		collection1.remove(0);
		collection2.addAll(collection1);
	}

	private enum LineSpec {
		TERMINALLINE, NONTERMINALLINE, SYNCLINE
	}

}
