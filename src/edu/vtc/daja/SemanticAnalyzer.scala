package edu.vtc.daja

import org.antlr.v4.runtime.tree.TerminalNode

/**
 * Class to do semantic analysis of Daja programs.
 */
class SemanticAnalyzer(
  private val symbolTable: BasicSymbolTable,
  private val reporter   : Reporter) extends DajaBaseListener {

  private var expressionLevel = 0

  override def enterModule(ctx: DajaParser.ModuleContext): Unit = {
    val name: TerminalNode = ctx.IDENTIFIER()
    if (!name.getText.equals("main")) {
      reporter.reportError(
        name.getSymbol.getLine,
        name.getSymbol.getCharPositionInLine + 1,
        "Main function must be named 'main'")
    }
  }


  override def enterInit_declarator(ctx: DajaParser.Init_declaratorContext): Unit = {
    // Note that by added declared symbols when the init_declarator is entered allows the
    // symbol to be used in its own declaration. Do we want that? Probably. If not, we could
    // add the symbol in exitInit_declarator instead.
    //
    // symbolTable.add(ctx.IDENTIFIER().getText());
  }


  override def enterExpression(ctx: DajaParser.ExpressionContext): Unit = {
    // Keep track of the number of nested open expressions.
    expressionLevel += 1
  }


  override def exitExpression(ctx: DajaParser.ExpressionContext): Unit = {
    // Keep track of the number of nested open expressions.
    expressionLevel -= 1
  }


  override def visitTerminal(node: TerminalNode): Unit = {
    // We are only concerned about identifiers in expressions.
    if (expressionLevel > 0) {
      node.getSymbol.getType match {
        case DajaLexer.IDENTIFIER =>
          //if (!symbolTable.contains(node.getText())) {
          //    reporter.reportError(
          //            node.getSymbol().getLine(),
          //            node.getSymbol().getCharPositionInLine() + 1,
          //            "Undefined identifier: " + node.getText());
          //}
      }
    }
  }

}
