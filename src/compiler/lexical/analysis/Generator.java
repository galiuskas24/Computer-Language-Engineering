package compiler.lexical.analysis;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;


public class Generator {

    private static String outputPath = "src/compiler/lexical/analysis/analyzer/Automatons.obj";
    private static TreeMap<Integer, Automaton> automatonsMap = new TreeMap<>();
    private static String startState;

    public static void main(String[] args) throws Exception {

        //read input data
        ArrayList<String> inputData = new ArrayList<>();
        Scanner sc = new Scanner(new File(args[0]));

        while (sc.hasNextLine()){
            inputData.add(sc.nextLine());
        }

        //parse input
        parseInput(inputData);

        //END
        SaveDataForAnalyzer(new LexManager(startState, automatonsMap));
    }

    private static void parseInput(ArrayList<String> inputData) {
        HashMap<String, String> regexMap = new HashMap<>();
        boolean readAutomatons = false;
        int priority = 1;
        for (int i = 0; i < inputData.size(); i++) {
            String line = inputData.get(i);

            if (readAutomatons){
                if (line.startsWith("<")){

                    //create automaton
                    String mainState = line.substring(1, line.indexOf(">"));
                    String regex = line.substring(line.indexOf(">") + 1);
                    regex = completeRegex(regex, regexMap);

                    Automaton automaton = new Automaton();
                    PairOfStates result = implementAutomatonFromRegex(regex, automaton);
                    automaton.setMainState(mainState);
                    automaton.setStartState(result.leftState);
                    automaton.addTempState(result.leftState);
                    automaton.setFinishState(result.rightState);

                    //find and set actions
                    automaton.setActions(parseActions(inputData, i + 1));
                    automatonsMap.put(priority++, automaton);
                }
                continue;
            }

            if (line.startsWith("%X")){
                startState = line.split(" ")[1];
                readAutomatons = true;

            }else {
                //load and complete every regex
                String[] parts = line.split(" ");
                regexMap.put(parts[0], "(" + completeRegex(parts[1], regexMap) + ")");
            }

        }
    }

    private static PairOfStates implementAutomatonFromRegex(String regex, Automaton automaton) {
        ArrayList<String> options = new ArrayList<>();

        int parentheses = 0;
        int beginIndex = 0, length = 0;

        //find every option
        for (int i = 0; i < regex.length(); i++){

            if (regex.charAt(i) == '(' && isOperator(regex, i)){
                parentheses++;

            }else if (regex.charAt(i) == ')' && isOperator(regex, i)){
                parentheses--;

            }else if (parentheses == 0 && regex.charAt(i) == '|' && isOperator(regex, i)){
                options.add(regex.substring(beginIndex, beginIndex + length));
                beginIndex = i + 1;
                length = 0;
                continue;
            }

            length++;
        }

        //add last option
        if (beginIndex > 0) options.add(regex.substring(beginIndex));


        //algorithm
        int leftState = automaton.createNewState();
        int rightState = automaton.createNewState();


        if (options.size() > 0){

            for (int i = 0; i < options.size(); i++) {
                PairOfStates result = implementAutomatonFromRegex(options.get(i), automaton);
                automaton.addTransition(leftState, result.leftState, (char)0);
                automaton.addTransition(result.rightState, rightState, (char)0);
            }

        }else{
            boolean prefix = false;
            int lastState = leftState;

            for (int i = 0; i < regex.length(); i++){
                int a, b;

                if (prefix){
                    //first case -> special character
                    prefix = false;
                    char character = regex.charAt(i);
                    char transitionChar;

                    if (character == 't'){
                        transitionChar = '\t';
                    }else if (character == 'n'){
                        transitionChar = '\n';
                    }else if (character == '_'){
                        transitionChar = ' ';
                    }else {
                        transitionChar = character;
                    }

                    a = automaton.createNewState();
                    b = automaton.createNewState();
                    automaton.addTransition(a, b, transitionChar);


                }else {
                    //second case ->
                    if (regex.charAt(i) == '\\'){
                        prefix = true;
                        continue;
                    }

                    if (regex.charAt(i) != '('){
                        //second case vol. 1
                        a = automaton.createNewState();
                        b = automaton.createNewState();

                        if (regex.charAt(i) == '$'){
                            automaton.addTransition(a, b, (char)0);
                        }else {
                            automaton.addTransition(a, b, regex.charAt(i));
                        }
                    }else{
                        //second case vol. 2
                        int brackets = 1;
                        int newLength = 0;

                        for (int k = i + 1; brackets != 0 ; k++, newLength++){
                            if (regex.charAt(k) == '(' && isOperator(regex, k)) brackets++;
                            else if (regex.charAt(k) == ')' && isOperator(regex, k)) brackets--;
                        }

                        String newRegex = regex.substring(i + 1, i + newLength);
                        PairOfStates newResult = implementAutomatonFromRegex(newRegex, automaton);
                        a = newResult.leftState;
                        b = newResult.rightState;
                        i += newLength;

                    }

                }


                //iteration operator?
                if (i + 1 < regex.length() && regex.charAt(i + 1) == '*'){
                    int x = a;
                    int y = b;

                    a = automaton.createNewState();
                    b = automaton.createNewState();
                    automaton.addTransition(a, x,(char)0);
                    automaton.addTransition(y, b,(char)0);
                    automaton.addTransition(a, b,(char)0);
                    automaton.addTransition(y, x,(char)0);
                    i++;
                }

                //connect with automaton
                automaton.addTransition(lastState, a, (char)0);
                lastState = b;
            }

            automaton.addTransition(lastState, rightState, (char)0);
        }

        return new PairOfStates(leftState, rightState);
    }

    private static boolean isOperator(String regex, int i) {
        int cnt = 0;
        for (int j = i - 1; j >= 0 && regex.charAt(j) == '\\'; j--) cnt++;
        return cnt % 2 == 0;
    }

    private static String parseActions(ArrayList<String> inputData, int index) {
        String actions = "";

        while(true){
            String newLine = inputData.get(++index);
            if (newLine.startsWith("}")){
                break;
            }else{
                if (actions != "") actions += "\n";
                actions +=  inputData.get(index);
            }
        }
        return actions;
    }

    private static String completeRegex(String newRegex, HashMap<String, String> regexMap) {

        for (HashMap.Entry<String, String> regex : regexMap.entrySet())
        {
            if (newRegex.contains(regex.getKey())){
                int startIndex;
                String replacement;

                while (true){
                    startIndex = newRegex.indexOf(regex.getKey());
                    if (startIndex == -1) break;
                    replacement = regex.getValue() + newRegex.substring(startIndex + regex.getKey().length());
                    newRegex = newRegex.substring(0, startIndex) + replacement;
                }
            }
        }

        return newRegex;
    }

    public static void SaveDataForAnalyzer(LexManager manager) throws Exception {
        ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File(outputPath).getAbsolutePath()));
        output.writeObject(manager);
        output.close();
    }

    static class PairOfStates{
        private int leftState;
        private int rightState;

        public PairOfStates(int leftState, int rightState) {
            this.leftState = leftState;
            this.rightState = rightState;
        }
    }
}
