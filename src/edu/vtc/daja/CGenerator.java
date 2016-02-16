package edu.vtc.daja;


import edu.vtc.daja.Literals;
import edu.vtc.daja.Reporter;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.PrintStream;
import java.util.Set;

public class CGenerator extends DajaBaseVisitor<Void> {

    private Set<String> symbolTable;
    private Reporter reporter;
    private PrintStream out = System.out;
    private int indentationLevel = 0;  // The number of indentations where output lines start.
    private int expressionLevel = 0;   // The number of open expression rules that are active.


    public CGenerator(Set<String> symbolTable, Reporter reporter)
    {
        this.symbolTable = symbolTable;
        this.reporter = reporter;
    }


    private void doIndentation()
    {
        for (int i = 0; i < indentationLevel; ++i) {
            out.print("    ");
        }
    }

    @Override
    public Void visitModule(DajaParser.ModuleContext ctx)
    {
        out.println("#include <stdio.h>");
        out.println("");
        out.println("int main( void )");
        out.println("{");
        indentationLevel++;

        visitChildren(ctx);

        out.println("");
        for (String symbolName : symbolTable) {
            doIndentation();
            out.println("printf(\"" + symbolName + " => %d\\n\", " + symbolName + ");");
        }
        out.println("}");
        indentationLevel--;
        return null;
    }


    @Override
    public Void visitStatement(DajaParser.StatementContext ctx)
    {
        doIndentation();
        visitChildren(ctx);
        out.println("");
        return null;
    }


    @Override
    public Void visitExpression_statement(DajaParser.Expression_statementContext ctx)
    {
        visitExpression(ctx.expression());
        out.print(";");
        return null;
    }


    @Override
    public Void visitAssignment_expression(DajaParser.Assignment_expressionContext ctx)
    {
        out.print("(");
        expressionLevel++;
        visitAdd_expression(ctx.add_expression());
        if (ctx.EQUALS() != null) {
            out.print(" = ");
            visitAssignment_expression(ctx.assignment_expression());
        }
        expressionLevel--;
        out.print(")");

        return null;
    }


    @Override
    public Void visitTerminal(TerminalNode node)
    {
        try {
            if (expressionLevel > 0) {
                switch (node.getSymbol().getType()) {
                    case DajaLexer.DIVIDE:
                    case DajaLexer.IDENTIFIER:
                    case DajaLexer.MINUS:
                    case DajaLexer.MULTIPLY:
                    case DajaLexer.PLUS:
                        out.print(node.getText());
                        break;

                    case DajaLexer.NUMERIC_LITERAL:
                        out.print(Literals.convertIntegerLiteral(node.getText()));
                        break;
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

}
