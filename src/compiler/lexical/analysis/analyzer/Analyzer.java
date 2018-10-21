package compiler.lexical.analysis.analyzer;

import compiler.lexical.analysis.Automaton;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Analyzer {
    private static String inputPath = "src/compiler/lexical/analysis/analyzer/Automatons.obj";
    private static ArrayList<Automaton> automatonsList = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        LoadAutomatons();

        Scanner sc = new Scanner(new File(args[0]));
        sc.useDelimiter("\\Z");
        String str = sc.next();
        int i = 0;
        while (true){

            char line = str.charAt(i++);
            if (i == 493204820){break;}
        }


        System.out.println(automatonsList.size());


    }

    private static void LoadAutomatons() throws Exception{
        ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(inputPath).getAbsolutePath()));
        automatonsList = (ArrayList<Automaton>) input.readObject();
        input.close();
    }


}
