package org.kelseymountain.daja

import scala.jdk.CollectionConverters.*
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * Class to do type checking of Daja programs.
 */
class TypeChecker(
  private val symbolTable: SymbolTable,
  private val reporter   : Reporter) extends DajaBaseVisitor[TypeRep.Rep] {

  private var expressionLevel = 0

  override def visitDeclaration(ctx: DajaParser.DeclarationContext): TypeRep.Rep = {
    val initDeclarators = ctx.init_declarator.asScala
    val basicType = ctx.basic_type.getText
    val basicTypeRep = basicType match {
      case "bool"   => TypeRep.BoolRep
      case "int"    => TypeRep.IntRep
      case "uint"   => TypeRep.UIntRep
      case "long"   => TypeRep.LongRep
      case "ulong"  => TypeRep.ULongRep
      case "float"  => TypeRep.FloatRep
      case "double" => TypeRep.DoubleRep
      case "real"   => TypeRep.RealRep
    }

    val isArrayType = ctx.LBRACKET != null
    if (isArrayType) {
      // TODO: Does D allow arrays to be indexed by any integral type or just int?
      // Daja requires arrays to be indexed by int (only).
      val dimensionExpressionType = visit(ctx.expression)
      if (dimensionExpressionType != TypeRep.IntRep) {
        reporter.reportError(
          ctx.LBRACKET.getSymbol.getLine,
          ctx.LBRACKET.getSymbol.getCharPositionInLine + 1,
          "Array dimension must have type int")
      }
      else {
        // TODO: Verify that the array dimension is a constant expression (or integer literal?).
        // TODO: Arrange to add the size of the array to the symbol table.
      }
    }

    for (initDeclarator <- initDeclarators) {
      val identifierName = initDeclarator.IDENTIFIER.getText

      // Are we dealing with an array declaration?
      if (isArrayType) {
        // TODO: Type check the initialization expression (if present).
        symbolTable.addObjectName(identifierName, TypeRep.ArrayRep(basicTypeRep))
      }
      // ... otherwise it is a simple variable declaration.
      else {
        // TODO: Type check the initialization expression (if present).
        symbolTable.addObjectName(identifierName, basicTypeRep)
      }
    }
    TypeRep.NoTypeRep
  }

  // TODO: Type check statement forms

  override def visitExpression(ctx: DajaParser.ExpressionContext): TypeRep.Rep = {
    // Keep track of the number of nested open expressions.
    expressionLevel += 1
    val expressionType = visit(ctx.comma_expression)
    expressionLevel -= 1
    expressionType
  }

  // TODO: Comma expression
  // TODO: Assignment expression
  // TODO: Relational expression

  override def visitAdd_expression(ctx: DajaParser.Add_expressionContext): TypeRep.Rep = {
    val multiplyExpressionType = visit(ctx.multiply_expression)
    if (ctx.add_expression == null) {
      multiplyExpressionType
    }
    else {
      val addExpressionType = visit(ctx.add_expression)
      if (addExpressionType == multiplyExpressionType)
        addExpressionType
      else
        // TODO: Deal with implicit type conversions and report errors as necessary!
        TypeRep.NoTypeRep
    }
  }


  override def visitMultiply_expression(ctx: DajaParser.Multiply_expressionContext): TypeRep.Rep = {
    val postfixExpressionType = visit(ctx.postfix_expression)
    if (ctx.multiply_expression == null) {
      postfixExpressionType
    }
    else {
      val multiplyExpressionType = visit(ctx.multiply_expression)
      if (multiplyExpressionType == postfixExpressionType)
        multiplyExpressionType
      else
        // TODO: Deal with implicit type conversions and report errors as necessary!
        TypeRep.NoTypeRep
    }
  }


  override def visitPostfix_expression(ctx: DajaParser.Postfix_expressionContext): TypeRep.Rep = {
    // If this postfix expression is just a primary expression...
    if (ctx.primary_expression != null) {
      visit(ctx.primary_expression)
    }
    // Otherwise we are trying to access an array...
    else {
      // Check that the postfix expression has an array type.
      val postfixExpressionType = visit(ctx.postfix_expression)
      val elementType = postfixExpressionType match {
        case TypeRep.ArrayRep(elementType) =>
          elementType
        case _ =>
          reporter.reportError(
            ctx.LBRACKET.getSymbol.getLine,
            ctx.LBRACKET.getSymbol.getCharPositionInLine + 1,
            "Indexing a non-array")
          TypeRep.NoTypeRep
      }
      // Now check that the index expression has type int (Daja allows only int).
      if (visit(ctx.expression) != TypeRep.IntRep) {
        reporter.reportError(
          ctx.LBRACKET.getSymbol.getLine,
          ctx.LBRACKET.getSymbol.getCharPositionInLine + 1,
          "Index type must be int")
        TypeRep.NoTypeRep
      }
      else {
        elementType
      }
    }
  }


  override def visitPrimary_expression(ctx: DajaParser.Primary_expressionContext): TypeRep.Rep = {
    if (ctx.LPARENS == null) {
      // This simple approach works because there is only one child, which is a terminal.
      // Thus visitTerminal is called on that child and the (single) result is what is
      // returned by visitChildren.
      visitChildren(ctx)
    }
    else {
      // This case handles parenthesized subexpressions.
      visitExpression(ctx.expression)
    }
  }


  override def visitTerminal(node: TerminalNode): TypeRep.Rep = {
    if (expressionLevel == 0)
      TypeRep.NoTypeRep
    else {
      // We are only concerned about identifiers in expressions.
      node.getSymbol.getType match {
        case DajaLexer.INTEGER_LITERAL =>
          val (_, literalType) = Literals.convertIntegerLiteral(node.getText)
          literalType

        //case DajaLexer.FLOATING_LITERAL =>
        //  // TODO: Decode the token to obtain the type of the literal.
        //  TypeRep.FloatRep

        case DajaLexer.TRUE =>
          TypeRep.BoolRep

        case DajaLexer.FALSE =>
          TypeRep.BoolRep

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

        // In an expression, things that are not covered above don't have a type.
        case _ =>
          TypeRep.NoTypeRep
      }
    }
  }

}
