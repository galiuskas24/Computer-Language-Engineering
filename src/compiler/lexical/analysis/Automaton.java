package compiler.lexical.analysis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;
import java.util.stream.Collectors;

public class Automaton implements Serializable {
    private int startState;
    private int finishState;
    private int numOfStates;
    private String mainState;
    private String actions;
    private ArrayList<Transition> transitions;
    private ArrayList<Integer> tempStates;

    public Automaton(){
        this.numOfStates = 0;
        transitions = new ArrayList<>();
        tempStates = new ArrayList<>();
    }

    public Automaton(Automaton automaton){
        this.startState = automaton.getStartState();
        this.finishState = automaton.getFinishState();
        this.numOfStates = automaton.getNumOfStates();
        this.mainState = automaton.getMainState();
        this.actions = automaton.getActions();
        this.transitions = automaton.getTransitions();
        this.tempStates = automaton.getTempStates();
    }

    public int createNewState(){
        return numOfStates++;
    }

    public void addTransition(int from, int to, Character forChar){
        transitions.add(new Transition(from, to, forChar));
    }

    public void restartAutomaton(){
        tempStates = new ArrayList<>();
        tempStates.add(startState);
    }

    public void generateEpsEnv(){
        Stack<Integer> stack = new Stack<>();
        ArrayList<Integer> newStates = new ArrayList<>(tempStates);
        stack.addAll(tempStates);

        while (!stack.isEmpty()){
            Integer fromStack = stack.pop();

            //generate epsEnvironment of state
            ArrayList<Integer> tempEpsEnv = (ArrayList<Integer>) transitions.stream()
                            .filter(o -> o.from == fromStack && o.forChar == (char)0)
                            .map(Transition::getTo)
                            .collect(Collectors.toList());

            for (Integer newState: tempEpsEnv) {
                if (!newStates.contains(newState)){
                    newStates.add(newState);
                    stack.push(newState);
                }
            }

        }

        tempStates = newStates;
    }

    public boolean doTransitionForChar(char charachter){
        ArrayList<Integer> newStates = new ArrayList<>();

        for (Integer state: tempStates) {

            ArrayList<Integer> tempEpsEnv = (ArrayList<Integer>) transitions.stream()
                    .filter(o -> o.from == state && o.forChar == charachter)
                    .map(Transition::getTo)
                    .collect(Collectors.toList());

            newStates.addAll(tempEpsEnv);
        }

        tempStates = newStates;
        generateEpsEnv();

        return tempStates.size() > 0;
    }

    public String getMainState() {
        return mainState;
    }

    public void setMainState(String mainState) {
        this.mainState = mainState;
    }

    public ArrayList<Integer> getTempStates() {
        return tempStates;
    }

    public void addTempState(int tempState) {
        tempStates.add(tempState);
    }

    public boolean isSatisfied(){
        return tempStates.contains(finishState);
    }

    public int getStartState() {
        return startState;
    }

    public int getFinishState() {
        return finishState;
    }

    public void setFinishState(int finishState) {
        this.finishState = finishState;
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
        private Character forChar;

        public Transition(int from, int to, Character forChar) {
            this.from = from;
            this.to = to;
            this.forChar = forChar;
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

        public Character getForChar() {
            return forChar;
        }

        public void setForChar(Character forChar) {
            this.forChar = forChar;
        }
    }
}
