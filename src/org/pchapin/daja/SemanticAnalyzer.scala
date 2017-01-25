package org.pchapin.daja

import org.antlr.v4.runtime.tree.TerminalNode
import org.pchapin.daja.DajaParser.Add_expressionContext

import scala.collection.JavaConverters._

/**
 * Class to do semantic analysis of Daja programs.
 */
class SemanticAnalyzer(
  private val symbolTable: BasicSymbolTable,
  private val reporter   : Reporter) extends DajaBaseVisitor[TypeRep.Rep] {

  private var expressionLevel = 0

  override def visitModule(ctx: DajaParser.ModuleContext): TypeRep.Rep = {
    val name: TerminalNode = ctx.IDENTIFIER()
    if (name.getText != "main") {
      reporter.reportError(
        name.getSymbol.getLine,
        name.getSymbol.getCharPositionInLine + 1,
        "Main function must be named 'main'")
    }
    visit(ctx.block_statement)
  }


  override def visitDeclaration(ctx: DajaParser.DeclarationContext): TypeRep.Rep = {
    val initDeclarators = ctx.init_declarator.asScala
    val basicType = ctx.basic_type.getText
    val basicTypeRep = basicType match {
      case "int"    => TypeRep.IntRep
      case "bool"   => TypeRep.BoolRep
      case "double" => TypeRep.DoubleRep
    }
    for (initDeclarator <- initDeclarators) {
      val identifierName = initDeclarator.IDENTIFIER.getText
      // TODO: Deal with the possibility that the initDeclarator is for an array.
      // TODO: Type check the initialization expression and the array dimension expression.
      // TODO: Verify that the array dimension is a constant expression.
      symbolTable.addObjectName(identifierName, basicTypeRep)
    }
    TypeRep.NoTypeRep
  }


  override def visitExpression(ctx: DajaParser.ExpressionContext): TypeRep.Rep = {
    // Keep track of the number of nested open expressions.
    expressionLevel += 1
    val expressionType = visit(ctx.assignment_expression)
    expressionLevel -= 1
    expressionType
  }


  override def visitAdd_expression(ctx: Add_expressionContext): TypeRep.Rep = {
    val addExressionType = visit(ctx.add_expression)
    if (ctx.multiply_expression == null) {
      addExressionType
    }
    else {
      val multExpressionType = visit(ctx.multiply_expression)
      if (addExressionType == multExpressionType)
        addExressionType
      else
        // TODO: Deal with implicit type conversions!
        TypeRep.NoTypeRep
    }
  }


  override def visitTerminal(node: TerminalNode): TypeRep.Rep = {
    if (expressionLevel == 0)
      TypeRep.NoTypeRep
    else {
      // We are only concerned about identifiers in expressions.
      node.getSymbol.getType match {
        case DajaLexer.IDENTIFIER =>
          try {
            symbolTable.getObjectType(node.getText)
          }
          catch {
            case  ex: SymbolTable.UnknownObjectNameException =>
              reporter.reportError(
                node.getSymbol.getLine,
                node.getSymbol.getCharPositionInLine + 1,
                "Undefined identifier: " + node.getText)
              TypeRep.NoTypeRep
          }

        // In an expression, things that are not identifiers don't have a type.
        case _ =>
          TypeRep.NoTypeRep
      }
    }
  }

}
