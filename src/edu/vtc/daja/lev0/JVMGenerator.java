package edu.vtc.daja.lev0;

import edu.vtc.daja.Reporter;

import java.util.Set;

public class JVMGenerator extends DajaBaseVisitor<Void> {

    private Set<String> symbolTable;
    private Reporter reporter;


    public JVMGenerator(Set<String> symbolTable, Reporter reporter)
    {
        this.symbolTable = symbolTable;
        this.reporter = reporter;
    }
}
