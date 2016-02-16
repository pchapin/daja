package edu.vtc.daja;

import edu.vtc.daja.Reporter;

import java.util.Set;

public class LLVMGenerator extends DajaBaseVisitor<Void> {

    private Set<String> symbolTable;
    private Reporter reporter;


    public LLVMGenerator(Set<String> symbolTable, Reporter reporter)
    {
        this.symbolTable = symbolTable;
        this.reporter = reporter;
    }
}
