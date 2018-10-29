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
        int lastIndex, head, startIndex = 0, maxLength = inputCode.length();
        char tempChar;
        boolean errorOccurred = false;
        boolean EOF = false;
        String lexUnit = "";
        ArrayList<Integer> tempList;

        //algorithm
        for (int finishIndex = 0; finishIndex <= inputCode.length(); finishIndex++){
            lastIndex = finishIndex - 1;

            if (finishIndex == maxLength){
                EOF = true;
                tempChar = (char)0; //epsilon transition
                lexUnit += '$'; // char to eat later
            }else{
                //read next character
                tempChar = inputCode.charAt(finishIndex);
                if (tempChar == '\r') continue;
                lexUnit += tempChar;
            }

            //do transition for tempChar
            tempList = new ArrayList<>(liveAutomatons);
            liveAutomatons = new ArrayList<>();

            for (Integer index: tempList) {
                Automaton temp = automatons.get(index);

                //save successful automatons and their length
                if (temp.isSatisfied()) history.put(index, lexUnit.length()-1);

                //do transition
                if (temp.getMainState().equals(manager.getTempState()))
                    if (temp.doTransitionForChar(tempChar)) liveAutomatons.add(index);
            }

            // no more available automatons or EOF
            if (liveAutomatons.size() == 0 || EOF) {
                //find lexUnit
                if(lexUnit.length() > 1) lexUnit = lexUnit.substring(0, lexUnit.length() - 1);

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
                    finishIndex = ++startIndex;
                    continue;
                }

                //implementation of "BACK TO" function
                head = manager.getHead();
                if (head != -1){
                    manager.setHead(-1);
                    startIndex += head;
                    lastIndex = startIndex;
                }

                //don't update if EOF
                if(!EOF) finishIndex = startIndex = lastIndex;

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
        //sort lengths descending
        Iterator<Integer> lengths = new TreeSet<>(history.values()).descendingIterator();

        while (lengths.hasNext()){
            //get max length
            int maxValue = (int) lengths.next();

            //iterate by priority
            for (Map.Entry<Integer, Integer> entry : history.entrySet()){

                if (entry.getValue() == maxValue){

                    Automaton aut = automatons.get(entry.getKey());
                    if (aut.isSatisfied()){
                        lexUnit = lexUnit.length() > maxValue? lexUnit.substring(0, maxValue) : lexUnit;
                        manager.executeActions(aut.getActions(),lexUnit);
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
