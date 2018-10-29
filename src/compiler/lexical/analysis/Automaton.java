package compiler.lexical.analysis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Stack;
import java.util.stream.Collectors;

@SuppressWarnings("serial")
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

    public int createNewState(){
        return numOfStates++;
    }

    public void addTransition(int from, int to, Character forChar){
        transitions.add(new Transition(from, to, forChar));
    }

    public boolean isSatisfied(){
        return tempStates.contains(finishState);
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

    public boolean doTransitionForChar(char character){
        ArrayList<Integer> newStates = new ArrayList<>();

        //find new states for transition with parameter character
        for (Integer state: tempStates) {

            ArrayList<Integer> tempEpsEnv = (ArrayList<Integer>) transitions.stream()
                    .filter(o -> o.from == state && o.forChar == character)
                    .map(Transition::getTo)
                    .collect(Collectors.toList());

            newStates.addAll(tempEpsEnv);
        }

        // If no more transitions but automaton is satisfied
        if (this.isSatisfied() && newStates.size() == 0){
            tempStates.add(finishState);
            return false; // no new states
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

    public void addTempState(int tempState) {
        tempStates.add(tempState);
    }

    public void setFinishState(int finishState) {
        this.finishState = finishState;
    }

    public void setStartState(int startState) {
        this.startState = startState;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    @SuppressWarnings("serial")
    class Transition implements Serializable{
        private int from;
        private int to;
        private Character forChar;

        public Transition(int from, int to, Character forChar) {
            this.from = from;
            this.to = to;
            this.forChar = forChar;
        }

        public int getTo() {
            return to;
        }

    }
}
