package compiler.lexical.analysis;

import java.io.Serializable;

public class Automaton implements Serializable {
    public int start_state;


    public Automaton(int a){
        this.start_state = a;
    }
}
