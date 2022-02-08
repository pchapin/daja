package edu.vtc.daja

import scala.jdk.CollectionConverters.*
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * Class to do type checking of Daja programs.
 */
class RuleChecker(
  private val symbolTable: SymbolTable,
  private val reporter   : Reporter) extends DajaBaseVisitor[Void] {

  override def visitModule(ctx: DajaParser.ModuleContext): Void = {
    val name: TerminalNode = ctx.IDENTIFIER
    if (name.getText != "main") {
      reporter.reportError(
        name.getSymbol.getLine,
        name.getSymbol.getCharPositionInLine + 1,
        "Main function must be named 'main'")
    }
    visit(ctx.block_statement)
  }

}
