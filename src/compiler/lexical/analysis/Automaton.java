package compiler.lexical.analysis;

import java.io.Serializable;
import java.util.ArrayList;

public class Automaton implements Serializable {
    private int startState;
    private int numOfStates;
    private String actions;
    private ArrayList<Transition> transitions;

    public Automaton(){
        this.numOfStates = 0;
        transitions = new ArrayList<>();
    }

    public int createNewState(){
        return numOfStates++;
    }

    public void addTransition(int from, int to, String forChar){
        transitions.add(new Transition(from, to, forChar));
    }

    public int getStartState() {
        return startState;
    }

    public void setStartState(int startState) {
        this.startState = startState;
    }

    public int getNumOfStates() {
        return numOfStates;
    }

    public void setNumOfStates(int numOfStates) {
        this.numOfStates = numOfStates;
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }

    public void setTransitions(ArrayList<Transition> transitions) {
        this.transitions = transitions;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    class Transition implements Serializable{
        private int from;
        private int to;
        private String forChar;

        public Transition(int from, int to, String forChar) {
            this.from = from;
            this.to = to;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
            this.from = from;
        }

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        public String getForChar() {
            return forChar;
        }

        public void setForChar(String forChar) {
            this.forChar = forChar;
        }
    }
}
