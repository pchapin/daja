package edu.vermontstate.daja

import scala.collection.mutable

class Interpreter(
  private val symbolTable: SymbolTable,
  private val reporter   : Reporter) extends DajaBaseVisitor[Option[Int]] {

  private var expressionLevel = 0

  // For now assume all identifiers are integers.
  // TODO: Enrich this data structure so it can handle different types in a reasonable way.
  val valueMap: mutable.Map[String, Option[Int]] = mutable.Map()

  override def visitModule(ctx: DajaParser.ModuleContext): Option[Int] = {
    // Iterate over the symbol table and set up the value map.
    // Objects are initially uninitialized.
    for (name <- symbolTable.getObjectNames) {
      valueMap += (name -> None)
    }

    // Execute the program.
    visitChildren(ctx)

    // Iterate over the symbol table and print values of all declared variables.
    for (name <- symbolTable.getObjectNames) {
      println(s"$name = ${valueMap(name)}")
    }
    None
  }

  override def visitInit_declarator(ctx: DajaParser.Init_declaratorContext): Option[Int] = {
    if (ctx.expression != null ) {
      val name = ctx.IDENTIFIER.getText
      val initializer = visit(ctx.expression)
      valueMap.put(name, initializer)
    }
    None
  }

  override def visitExpression(ctx: DajaParser.ExpressionContext): Option[Int] = {
    // Keep track of the number of nested open expressions. (Do we care??)
    expressionLevel += 1
    val expressionValue = visit(ctx.comma_expression)
    expressionLevel -= 1
    expressionValue
  }

  override def visitAssignment_expression(ctx: DajaParser.Assignment_expressionContext): Option[Int] = {
    if (ctx.assignment_expression == null) {
      visit(ctx.relational_expression)
    }
    else {
      val variableName = ctx.relational_expression.add_expression.multiply_expression.postfix_expression.primary_expression.IDENTIFIER.getText
      val newValue = visit(ctx.assignment_expression)
      valueMap.put(variableName, newValue)
      newValue
    }
  }

  override def visitAdd_expression(ctx: DajaParser.Add_expressionContext): Option[Int] = {
    if (ctx.add_expression == null) {
      visit(ctx.multiply_expression)
    }
    else {
      val leftValue = visit(ctx.add_expression)
      val rightValue = visit(ctx.multiply_expression)
      if (leftValue.isEmpty || rightValue.isEmpty) {
        None
      }
      else if (ctx.PLUS != null) {
        leftValue.map(_ + rightValue.get)
      }
      else {
        leftValue.map(_ - rightValue.get)
      }
    }
  }

  override def visitMultiply_expression(ctx: DajaParser.Multiply_expressionContext): Option[Int] = {
    if (ctx.multiply_expression == null) {
      visit(ctx.postfix_expression)
    }
    else {
      val leftValue = visit(ctx.multiply_expression)
      val rightValue = visit(ctx.postfix_expression)
      if (leftValue.isEmpty || rightValue.isEmpty) {
        None
      }
      else if (ctx.MULTIPLY != null) {
        leftValue.map(_ * rightValue.get)
      }
      else {
        leftValue.map(_ / rightValue.get)
      }
    }
  }

  override def visitPrimary_expression(ctx: DajaParser.Primary_expressionContext): Option[Int] = {
    if (ctx.INTEGER_LITERAL != null) {
      val (literalValue, _) = Literals.convertIntegerLiteral(ctx.INTEGER_LITERAL.getText)
      Some(literalValue.toInt)
    }
    else if (ctx.IDENTIFIER != null) {
      val variableName = ctx.IDENTIFIER.getText
      valueMap(variableName)
    }
    else if (ctx.expression != null) {
      visit(ctx.expression)
    }
    else {
      None
    }
  }

}
