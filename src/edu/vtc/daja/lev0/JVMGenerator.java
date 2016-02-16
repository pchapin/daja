package edu.vtc.daja.lev0;

import edu.vtc.daja.Literals;
import edu.vtc.daja.Reporter;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class JVMGenerator extends DajaBaseVisitor<Void> {

    private Set<String> symbolTable;
    private Map<String, Integer> localVariables = new TreeMap<String, Integer>();
    private int localVariableCounter = 0;
    private Reporter reporter;
    private PrintWriter output;
    private int expressionLevel = 0;  // Number of open 'expression' rules that are active.


    public JVMGenerator(Set<String> symbolTable, Reporter reporter)
    {
        this.symbolTable = symbolTable;
        this.reporter = reporter;

        for (String symbol : symbolTable) {
            localVariableCounter++;
            localVariables.put(symbol, localVariableCounter);
        }
    }


    /**
     * Computes a load/store instruction given the local variable name. This method tries to use
     * the abbreviated instruction forms if possible, falling back to the instructions that take
     * an operand only when necessary.
     *
     * @param baseInstruction The base name of instruction. Should be either "iload" or "istore."
     * @param localVariableName The name of the local variable.
     * @return The appropriate instruction, possibly with an operand.
     */
    private String getLoadStoreInstruction(String baseInstruction, String localVariableName)
    {
        int localVariableNumber = localVariables.get(localVariableName);
        switch (localVariableNumber)
        {
            case 0:
            case 1:
            case 2:
            case 3:
                return baseInstruction + "_" + localVariableNumber;

            default:
                return baseInstruction + " " + localVariableNumber;
        }
    }


    @Override
    public Void visitModule(DajaParser.ModuleContext ctx)
    {
        try {
            String className = ctx.IDENTIFIER().getText();
            output =
                    new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(
                                            new FileOutputStream(className + ".j"), "US-ASCII")));

            output.println(".class public " + className);
            output.println(".super java/lang/Object");
            output.println();
            output.println(".method public <init>()V");
            output.println("    aload_0");
            output.println("    invokenonvirtual java/lang/Object/<init>()V");
            output.println("    return");
            output.println(".end method");
            output.println();
            output.println(".method public static main([Ljava/lang/String;)V");
            output.println("    .limit locals " + (localVariableCounter + 1)); // +1 for the argument?
            // TODO: Compute an appropriate size for the operand stack.
            output.println("    .limit stack 4");
            output.println("    ;");
            output.println("    ; Local Variable Table");
            output.println("    ; ====================");

            visitChildren(ctx);

            output.println("    return");
            output.println(".end method");
            output.close();
        }
        catch (FileNotFoundException ex) {
            // TODO: Do something sensible here!
        }
        catch (UnsupportedEncodingException ex) {
            // TODO: Do something sensible here (good luck with that)!
        }
        return null;
    }


    @Override
    public Void visitTerminal(TerminalNode node)
    {
        try {
            if (expressionLevel > 0) {
                switch (node.getSymbol().getType()) {
                    case DajaLexer.IDENTIFIER:
                        output.println("    " + getLoadStoreInstruction("iload", node.getText()));

                    case DajaLexer.NUMERIC_LITERAL:
                        int literalValue = Literals.convertIntegerLiteral(node.getText());
                        String instruction;
                        if (literalValue >= -128 && literalValue <= 127)
                            instruction = "bipush";
                        else if (literalValue >= -32768 && literalValue <= 32767)
                            instruction = "sipush";
                        else
                            // TODO: The operand of ldc is really an index into the constant pool!
                            instruction = "ldc";
                        output.println(
                                "    " + instruction + " " + Literals.convertIntegerLiteral(node.getText()));
                }
            }
        }
        // This exception should normally never arise if illegal literals are ruled out during
        // semantic analysis. However, literal analysis is currently not being done there.
        //
        // TODO: Check literal format during semantic analysis.
        catch (Literals.InvalidLiteralException ex) {
            reporter.reportError(
                    node.getSymbol().getLine(),
                    node.getSymbol().getCharPositionInLine() + 1,
                    ex.getMessage());
        }
        return null;
    }


    @Override
    public Void visitMultiply_expression(DajaParser.Multiply_expressionContext ctx)
    {
        if (ctx.MULTIPLY() != null) {
            visit(ctx.multiply_expression());
            visit(ctx.primary_expression());
            output.println("    imul");
        }
        else if (ctx.DIVIDE() != null) {
            visit(ctx.multiply_expression());
            visit(ctx.primary_expression());
            output.println("    idiv");
        }
        else {
            visit(ctx.primary_expression());
        }
        return null;
    }


    @Override
    public Void visitAdd_expression(DajaParser.Add_expressionContext ctx)
    {
        if (ctx.PLUS() != null) {
            visit(ctx.add_expression());
            visit(ctx.multiply_expression());
            output.println("    iadd");
        }
        else if (ctx.MINUS() != null) {
            visit(ctx.add_expression());
            visit(ctx.multiply_expression());
            output.println("    isub");
        }
        else {
            visit(ctx.multiply_expression());
        }
        return null;
    }


    @Override
    public Void visitExpression(DajaParser.ExpressionContext ctx)
    {
        expressionLevel += 1;
        visitChildren(ctx);
        expressionLevel -= 1;
        return null;
    }
}
