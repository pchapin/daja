package edu.vtc.daja;

import java.util.Set;
import edu.vtc.daja.Reporter;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Class to do semantic analysis of Daja programs.
 */
public class SemanticAnalyzer extends DajaBaseListener {

    private Set<String> symbolTable;
    private Reporter reporter;
    private int expressionLevel = 0;

    public SemanticAnalyzer(Set<String> symbolTable, Reporter reporter)
    {
        this.symbolTable = symbolTable;
        this.reporter = reporter;
    }

    @Override
    public void enterModule(DajaParser.ModuleContext ctx)
    {
        TerminalNode name = ctx.IDENTIFIER();
        if (!name.getText().equals("main")) {
            reporter.reportError(
                    name.getSymbol().getLine(),
                    name.getSymbol().getCharPositionInLine() + 1,
                    "Main function must be named 'main'");
        }
    }

    @Override
    public void enterInit_declarator(DajaParser.Init_declaratorContext ctx)
    {
        // Note that by added declared symbols when the init_declarator is entered allows the
        // symbol to be used in its own declaration. Do we want that? Probably. If not, we could
        // add the symbol in exitInit_declarator instead.
        //
        symbolTable.add(ctx.IDENTIFIER().getText());
    }


    @Override
    public void enterExpression(DajaParser.ExpressionContext ctx)
    {
        // Keep track of the number of nested open expressions.
        expressionLevel++;
    }


    @Override
    public void exitExpression(DajaParser.ExpressionContext ctx)
    {
        // Keep track of the number of nested open expressions.
        expressionLevel--;
    }


    @Override
    public void visitTerminal(TerminalNode node)
    {
        // We are only concerned about identifiers in expressions.
        if (expressionLevel > 0) {
            switch (node.getSymbol().getType()) {
                case DajaLexer.IDENTIFIER:
                    if (!symbolTable.contains(node.getText())) {
                        reporter.reportError(
                                node.getSymbol().getLine(),
                                node.getSymbol().getCharPositionInLine() + 1,
                                "Undefined identifier: " + node.getText());
                    }
                    break;
            }
        }
    }
}
