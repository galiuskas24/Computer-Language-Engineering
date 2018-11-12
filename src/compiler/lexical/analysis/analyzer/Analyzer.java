package compiler.lexical.analysis.analyzer;

import compiler.lexical.analysis.Automaton;
import compiler.lexical.analysis.LexManager;
import java.io.*;
import java.util.*;

public class Analyzer {
    private static String inputPath = "src/compiler/lexical/analysis/analyzer/Automatons.obj";
    private static TreeMap<Integer, Automaton> automatons;
    private static LexManager manager;

    public static void main(String[] args) throws Exception {

        LoadDataForAnalyzer();

        ArrayList<Integer> liveAutomatons = new ArrayList<>(automatons.keySet());
        TreeMap<Integer, Integer> history = new TreeMap<>();

        //generate epsEnvironment first time
        automatons.forEach((index, automaton)-> automaton.generateEpsEnv());

        //read code
        Scanner sc = new Scanner(new File(args[0]));
        sc.useDelimiter("\\Z");
        String inputCode = sc.next();

        //start parameters
        int head, startIndex = 0, inputCodeLength = inputCode.length();
        char tempChar;
        boolean errorOccurred = false;
        boolean EOF = false;
        int EofOffset = 0;
        String lexUnit = "";
        ArrayList<Integer> reserveAutomatonList;


        //algorithm
        for (int finishIndex = 0; finishIndex <= inputCodeLength; finishIndex++){

            if (finishIndex == inputCodeLength){
                EOF = true;
                if (lexUnit.length() == 0) return;

                EofOffset = 1;
                tempChar = (char)0; //epsilon transition

            }else{
                //read next character
                tempChar = inputCode.charAt(finishIndex);
                if (tempChar == '\r') continue;
                lexUnit += tempChar;
            }


            reserveAutomatonList = new ArrayList<>(liveAutomatons);
            liveAutomatons = new ArrayList<>();

            //do transition for tempChar
            for (Integer index: reserveAutomatonList) {
                Automaton tempAutomaton = automatons.get(index);

                //save successful automatons and their temp length
                if (tempAutomaton.isSatisfied()) history.put(index, lexUnit.length() - 1 + EofOffset);

                //do transition
                if (tempAutomaton.getMainState().equals(manager.getTempState()))
                    if (tempAutomaton.doTransitionForChar(tempChar)) liveAutomatons.add(index);
            }

            // no more available automatons for transition or EOF
            if (liveAutomatons.size() == 0 || EOF) {
                //find lexUnit
                if(lexUnit.length() > 1) lexUnit = lexUnit.substring(0, lexUnit.length() - 1 + EofOffset);

                if(!findSuccessfulAutomaton(history, lexUnit)){
                    //handling error
                    System.err.println("Error: " + lexUnit.substring(0,1));
                    errorOccurred = true;
                }

                //restart automatons
                automatons.forEach((index, automaton) -> {
                    automaton.restartAutomaton();
                    automaton.generateEpsEnv();
                });
                liveAutomatons = new ArrayList<>(automatons.keySet());
                history = new TreeMap<>();
                lexUnit = "";

                if (errorOccurred){
                    errorOccurred = false;
                    finishIndex = startIndex++;
                    EOF = false;
                    EofOffset = 0;
                    continue;
                }

                //implementation of "BACK TO" function
                head = manager.getHead();
                if (head != -1){
                    manager.setHead(-1);
                    startIndex += head;
                    finishIndex = startIndex - 1;
                    EOF = false;
                    EofOffset = 0;
                    continue;
                }

                //don't update if EOF
                if(!EOF) startIndex = finishIndex--;
            }

        }

    }

    /***
     *  This method find automaton by longest length and highest priority.
     * @param history of automatons
     * @param lexUnit temporary lexical unit
     * @return false if error and true otherwise
     */
    private static boolean findSuccessfulAutomaton(TreeMap<Integer, Integer> history, String lexUnit) {
        //create automaton lengths descending
        Iterator<Integer> lengths = new TreeSet<>(history.values()).descendingIterator();

        //iterate form higher to lower length
        while (lengths.hasNext()){
            int tempLength = lengths.next();

            //for same length -> iterate by priority
            for (Map.Entry<Integer, Integer> entry : history.entrySet()){

                if (entry.getValue() == tempLength){
                    Automaton aut = automatons.get(entry.getKey());

                    if (aut.isSatisfied()){

                        //substring lexUnit and return head on last satisfied automaton
                        if (lexUnit.length() > tempLength){
                            lexUnit = lexUnit.substring(0, tempLength);
                            manager.setHead(tempLength);
                        }
                        manager.executeActions(aut.getActions(), lexUnit);
                        return true;
                    }
                }
            }

        }

        return false;
    }

    private static void LoadDataForAnalyzer() throws Exception{
        ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(inputPath).getAbsolutePath()));
        manager = (LexManager) input.readObject();
        automatons =  manager.getAutomatons();
        input.close();
    }
}
