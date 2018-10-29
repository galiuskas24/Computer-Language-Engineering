package compiler.lexical.analysis.analyzer;

import compiler.lexical.analysis.Automaton;
import compiler.lexical.analysis.LexManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Analyzer {
    private static String inputPath = "src/compiler/lexical/analysis/analyzer/Automatons.obj";
    private static ArrayList<Automaton> automatonsList;
    private static LexManager manager;

    public static void main(String[] args) throws Exception {

        LoadDataForAnalyzer();
        automatonsList = manager.getAutomatonList();

        //generate epsEnvironment
        for (Automaton aut: automatonsList) aut.generateEpsEnv();

        //read code
        Scanner sc = new Scanner(new File(args[0]));
        sc.useDelimiter("\\Z");
        String inputCode = sc.next();

        int startIndex, lastIndex, finishIndex;
        startIndex = lastIndex = finishIndex = 0;
        boolean EOF = false;
        String lexUnit = "";
        boolean errorOccurred = false;
        ArrayList<Automaton> automatons = cloneList(automatonsList);

        for (int i = 0; i <= inputCode.length(); i++){
            finishIndex = i;
            lastIndex = i-1;

            char tempChar;
            if (i == inputCode.length()){
                tempChar = '$';
                EOF = true;

            }else{
                tempChar = inputCode.charAt(finishIndex);
                if (tempChar == '\r')continue;
                lexUnit += tempChar;
            }



            ArrayList<Automaton> oldListOfAutomatons = cloneList(automatons);
            automatons = (ArrayList<Automaton>) automatons.stream()
                                .filter(x -> x.getMainState().equals(manager.getTempState()))
                                .filter(o -> o.doTransitionForChar(tempChar))
                                .collect(Collectors.toList());

            if (automatons.size() == 0 || EOF) {
                if(lexUnit.length() > 1){
                    lexUnit = lexUnit.substring(0, lexUnit.length() - 1);
                }
                if (manager.getRow() == 27){
                    System.out.print("");
                }
                if(!findSuccessfulAutomaton(oldListOfAutomatons, lexUnit)){
                    //handling error
                    System.err.println("Error: " + lexUnit.substring(0,1));
                    errorOccurred = true;
                }
                lexUnit = "";
                automatons = cloneList(automatonsList);//restart every automaton

                if (errorOccurred){
                    errorOccurred = false;
                    i = startIndex = startIndex + 1;
                    continue;
                }

                int head = manager.getHead();
                if (head != -1){
                    manager.setHead(-1);

                    startIndex += head;
                    lastIndex = startIndex;
                }

                if(!EOF){
                    i = startIndex = lastIndex;
                }


            }

        }

    }

    private static ArrayList<Automaton> cloneList(ArrayList<Automaton> list){
        ArrayList<Automaton> clone = new ArrayList<>();
        for (Automaton at : list) clone.add(new Automaton(at));
        return clone;
    }

    private static boolean findSuccessfulAutomaton(ArrayList<Automaton> listOfAutomatons, String lexUnit) {

        for (Automaton aut: listOfAutomatons) {
            if (aut.isSatisfied()){
                manager.executeActions(aut.getActions(), lexUnit);
                return true;
            }
        }
        return false;
    }

    private static void LoadDataForAnalyzer() throws Exception{
        ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(inputPath).getAbsolutePath()));
        manager = (LexManager) input.readObject();
        input.close();
    }


}
