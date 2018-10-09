package compiler.lexical.analysis;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;


public class Generator {

    private static String outputPath = "src/compiler/lexical/analysis/analyzer/Automatons.obj";
    private static ArrayList<Automaton> automatonsList = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        //read input data
        ArrayList<String> inputData = new ArrayList<>();
        Scanner sc = new Scanner(new File(args[0]));

        while (sc.hasNextLine()){
            String line = sc.nextLine();
            inputData.add(line);
        }

        //parse input

        System.out.println("Vladoooo");

        //END
        SaveAutomatons();
    }

    public static void SaveAutomatons() throws Exception {
        ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File(outputPath).getAbsolutePath()));
        output.writeObject(automatonsList);
        output.close();

    }
}
