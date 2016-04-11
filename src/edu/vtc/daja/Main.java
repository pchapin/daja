package edu.vtc.daja;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.util.Set;
import java.util.TreeSet;

/**
 * The main class of the Daja compiler.
 */
public class Main {

    /**
     * Specifies the different modes of operation that are supported.
     *
     * CHECK    : Syntax and semantic check only, no code generation or execution.
     * INTERPRET: Execute the program without generating code for it.
     * C        : Generate a Standard C program as output.
     * LLVM     : Generate LLVM assembly language as output.
     * JVM      : Generate JVM assembly language as output.
     */
    private enum Mode {
        CHECK,
        INTERPRET,
        C,
        LLVM,
        JVM
    }

    private static BasicConsoleReporter reporter = new BasicConsoleReporter();

    /**
     * Process input files in the Daja language. This method runs the compiler pipeline.
     *
     * @param input The input file to compile.
     * @param mode The desired target or mode of operation.
     */
    private static void processDaja(ANTLRFileStream input, Mode mode) {
        // Parse the input file as Daja.
        DajaLexer lexer           = new DajaLexer(input);
        CommonTokenStream tokens  = new CommonTokenStream(lexer);
        DajaParser parser         = new DajaParser(tokens);
        DajaParser.ModuleContext tree = parser.module();

        // Walk the tree created during the parse and analyze it for semantic errors.
        BasicSymbolTable symbolTable   = new BasicSymbolTable();
        SemanticAnalyzer myAnalyzer    = new SemanticAnalyzer(symbolTable, reporter);
        ParseTreeWalker analyzerWalker = new ParseTreeWalker();
        analyzerWalker.walk(myAnalyzer, tree);

        if (reporter.getErrorCount() > 0) {
            System.out.printf(
                    "%d errors found; compilation aborted!", reporter.getErrorCount());
        }
        else {
            switch (mode) {
                case CHECK:
                    // Do nothing. Semantic analysis is all that is required.
                    break;

                case INTERPRET:
                    Interpreter myInterpreter = new Interpreter(symbolTable, reporter);
                    ParseTreeWalker interpreterWalker = new ParseTreeWalker();
                    interpreterWalker.walk(myInterpreter, tree);
                    myInterpreter.displayResults();
                    break;

                case C:
                    CGenerator myCGenerator = new CGenerator(symbolTable, reporter);
                    myCGenerator.visit(tree);
                    break;

                case LLVM:
                    System.out.println("LLVM code generation is not implemented!");
                    LLVMGenerator myLLVMGenerator = new LLVMGenerator(symbolTable, reporter);
                    myLLVMGenerator.visit(tree);
                    break;

                case JVM:
                    JVMGenerator myJVMGenerator = new JVMGenerator(symbolTable, reporter);
                    myJVMGenerator.visit(tree);
                    break;
            }
        }
    }


    /**
     * The main program of the Daja educational compiler. This program accepts a file to compile
     * on the command line, along with some options the desired target, and produces error
     * messages or a compiled output file. Daja programs can also be interpreted in some cases.
     *
     * @param args The command line arguments.
     * @throws java.io.IOException If an I/O error occurs during File I/O.
     */
    public static void main(String[] args)
            throws java.io.IOException
    {
        System.out.println("Daja D Compiler (C) 2016 by Vermont Technical College");

        // Analyze the command line.
        if (args.length != 2) {
            System.out.println(
                    "Usage: java -jar Daja (-k | -i | -c | -l | -j) source-file");
            System.exit(1);
        }

        Mode mode;
        switch (args[0]) {
            case "-k":
                mode = Mode.CHECK;
                break;
            case "-i":
                mode = Mode.INTERPRET;
                break;
            case "-c":
                mode = Mode.C;
                break;
            case "-l":
                mode = Mode.LLVM;
                break;
            case "-j":
                mode = Mode.JVM;
                break;
            default:
                System.out.println("Error: Unknown mode, defaulting to CHECK!\n");
                mode = Mode.CHECK;
                break;
        }

        // Create a stream that reads from the specified file.
        ANTLRFileStream input = new ANTLRFileStream(args[1]);
        processDaja(input, mode);
    }
}
