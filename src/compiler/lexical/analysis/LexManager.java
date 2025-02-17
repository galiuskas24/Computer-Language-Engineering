package compiler.lexical.analysis;

import java.io.Serializable;
import java.util.TreeMap;

@SuppressWarnings("serial")
public class LexManager implements Serializable {
    private TreeMap<Integer, Automaton> automatonList;
    private int row;
    private int head;
    private String tempState;
    private String startState;


    public LexManager(String startState, TreeMap<Integer, Automaton> automatonList) {
        this.automatonList = automatonList;
        this.startState = startState;
        this.tempState = startState;
        this.row = 1;
        this.head = -1;

    }

    public void executeActions(String action, String lexUnit){
        String[] actions = action.split("\n");

        int endIndex = lexUnit.length();
        //actions
        for (int i = 1; i< actions.length; i++) {

            if (actions[i].startsWith("NOVI_REDAK")){
                nextRow();

            } else if (actions[i].startsWith("UDJI_U_STANJE")){
                String[] parts = actions[i].split(" ");
                setTempState(parts[1]);

            } else if ((actions[i].startsWith("VRATI_SE"))){
                String[] parts = actions[i].split(" ");
                returnHeadOn(Integer.parseInt(parts[1]));
                endIndex = Integer.parseInt(parts[1]);

            } else {
            System.err.println("Action not defined!");

            }

        }

        //first row - lex name
        if (!actions[0].equals("-")){
            if (lexUnit.length() > 1 ) lexUnit = lexUnit.substring(0, endIndex);
            System.out.println(actions[0] + " " + row + " " + lexUnit);
        }

    }

    public String getTempState() {
        return tempState;
    }

    public void setTempState(String tempState) {
        this.tempState = tempState;
    }

    public void restartAutomaton() {
        this.tempState = startState;
    }

    public TreeMap<Integer, Automaton> getAutomatons() {
        return automatonList;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getHead() {
        return head;
    }

    //special actions

    public void setHead(int value) {
        this.head = value;
    }

    private void nextRow(){
        row++;
    }

    private void returnHeadOn(int index){
        this.head = index;
    }

}
