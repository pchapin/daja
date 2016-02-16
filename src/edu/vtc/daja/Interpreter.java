package edu.vtc.daja;


import edu.vtc.daja.Reporter;

import java.util.Set;
import java.util.TreeMap;

public class Interpreter extends DajaBaseListener {

    private TreeMap<String, Integer> symbolValues;
    private Reporter reporter;

    public Interpreter(Set<String> symbolTable, Reporter reporter)
    {
        this.reporter = reporter;
        // Fill the symbolValues map with initialized values for everything in the symbol table.
    }

    public void displayResults()
    {
        // Output the current values of all variables in some suitable format.
    }
}
