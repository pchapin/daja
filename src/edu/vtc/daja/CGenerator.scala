package edu.vtc.daja

import org.antlr.v4.runtime.tree.TerminalNode

class CGenerator(
  private val symbolTable: BasicSymbolTable,
  private val reporter   : Reporter) extends DajaBaseVisitor[Void] {

  private val out = System.out
  private var indentationLevel = 0  // The number of indentations where output lines start.
  private var expressionLevel = 0   // The number of open expression rules that are active.


  private def doIndentation(): Unit = {
    for (i <- 1 to indentationLevel) {
      out.print("    ")
    }
  }

  override def visitModule(ctx: DajaParser.ModuleContext): Void = {
    out.println("#include <stdio.h>")
    out.println("")
    out.println("int main( void )")
    out.println("{")
    indentationLevel += 1

    visitChildren(ctx)

    out.println("")
    for (symbolName <- symbolTable.getObjectNames) {
      doIndentation()
      out.println("printf(\"" + symbolName + " => %d\\n\", " + symbolName + ");")
    }
    out.println("}")
    indentationLevel -= 1
    null
  }


  override def visitStatement(ctx: DajaParser.StatementContext): Void = {
    doIndentation()
    visitChildren(ctx)
    out.println("")
    null
  }


  override def visitExpression_statement(ctx: DajaParser.Expression_statementContext): Void = {
    visitExpression(ctx.expression())
    out.print(";")
    null
  }


  override def visitAssignment_expression(ctx: DajaParser.Assignment_expressionContext): Void = {
    out.print("(")
    expressionLevel += 1
    visitRelational_expression(ctx.relational_expression)
    if (ctx.EQUALS() != null) {
      out.print(" = ")
      visitAssignment_expression(ctx.assignment_expression())
    }
    expressionLevel -= 1
    out.print(")")

    null
  }


  override def visitTerminal(node: TerminalNode): Void = {
    try {
      if (expressionLevel > 0) {
        node.getSymbol.getType match {
          case DajaLexer.DIVIDE
             | DajaLexer.IDENTIFIER
             | DajaLexer.MINUS
             | DajaLexer.MULTIPLY
             | DajaLexer.PLUS =>
            out.print(node.getText)

          case DajaLexer.NUMERIC_LITERAL =>
            out.print(Literals.convertIntegerLiteral(node.getText))
        }
      }
    }

    // This exception should normally never arise if illegal literals are ruled out during
    // semantic analysis. However, literal analysis is currently not being done there.
    //
    // TODO: Check literal format during semantic analysis.
    catch {
      case ex: Literals.InvalidLiteralException =>
        reporter.reportError(
          node.getSymbol.getLine,
          node.getSymbol.getCharPositionInLine + 1,
          ex.getMessage)
    }
    null
  }

}
