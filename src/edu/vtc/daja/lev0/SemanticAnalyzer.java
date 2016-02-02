package edu.vtc.daja.lev0;

import java.util.Set;
import edu.vtc.daja.Reporter;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Class to do semantic analysis of Daja level 0 programs.
 */
public class SemanticAnalyzer extends DajaBaseListener {

    private Set<String> symbolTable;
    private Reporter reporter;

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
}
