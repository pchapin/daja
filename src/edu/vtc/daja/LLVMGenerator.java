package edu.vtc.daja;

import edu.vtc.daja.Reporter;

import java.util.Set;

public class LLVMGenerator extends DajaBaseVisitor<Void> {

    private BasicSymbolTable symbolTable;
    private Reporter reporter;


    public LLVMGenerator(BasicSymbolTable symbolTable, Reporter reporter)
    {
        this.symbolTable = symbolTable;
        this.reporter = reporter;
    }
}
