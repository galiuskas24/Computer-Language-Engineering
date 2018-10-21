package compiler.lexical.analysis;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class Generator {

    private static String outputPath = "src/compiler/lexical/analysis/analyzer/Automatons.obj";
    private static ArrayList<Automaton> automatonsList = new ArrayList<>();
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
        SaveDataForAnalyzer(new LexManager(startState, automatonsList));
    }

    private static void parseInput(ArrayList<String> inputData) {
        HashMap<String, String> regexMap = new HashMap<>();
        boolean readAutomatons = false;

        for (int i = 0; i < inputData.size(); i++) {
            String line = inputData.get(i);

            if (readAutomatons){
                if (line.startsWith("<")){

                    //create automaton
                    String startState = line.substring(1, line.indexOf(">"));
                    String regex = line.substring(line.indexOf(">") + 1);
                    regex = completeRegex(regex, regexMap);

                    Automaton aut = implementAutomatonFromRegex(regex);

                    //find and set actions
                    aut.setActions(parseActions(inputData, i + 1));
                    automatonsList.add(aut);
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

    private static Automaton implementAutomatonFromRegex(String regex) {
        ArrayList<String> options = new ArrayList<>();
        int parentheses = 0;
        int beginIndex = 0, length = 0;

        //find every option
        for (int i = 0; i < regex.length(); i++){

            if (regex.charAt(i) == '('){
                parentheses++;

            }else if (regex.charAt(i) == ')'){
                parentheses--;

            }else if (parentheses == 0 && regex.charAt(i) == '|' && isOperator(regex, i)){
                options.add(regex.substring(beginIndex, length));
                beginIndex = i + 1;
                length = -1;
            }

            length++;
        }


        return new Automaton();
    }

    private static boolean isOperator(String regex, int i) {
        return false;
    }

    private static String parseActions(ArrayList<String> inputData, int index) {
        String actions = "";

        while(true){
            String newLine = inputData.get(++index);
            if (newLine.startsWith("}")){
                break;
            }else{
                actions += inputData.get(index);
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
}
