package org.kelseymountain.daja

import LLVMAbstractSyntax.Instruction
import LLVMAbstractSyntax.*

class LLVMGenerator(
  private val symbolTable: SymbolTable,
  private val CFG        : ControlFlowGraph,
  private val reporter   : Reporter) extends DajaBaseVisitor[Instruction] {

  def generateCodeForCFG(): Unit = {
    val ControlFlowGraph(entryBlock, graph, _) = CFG

    // For starters, let's create the necessary allocations...
    for (name <- symbolTable.getObjectNames) {
      makeConcrete(AllocaInstruction(name))
    }

    // Now process all the basic blocks in the CFG...
    for (someNode <- graph.outerNodeTraverser(graph get entryBlock)) {
      println("\nNew Block!") // TODO: Come up with proper block names (probably in the CFG).
      val instructions = for (statement <- someNode.assignments) yield {
        println(statement.toStringTree)
        visit(statement)
      }
      instructions foreach { makeConcrete }
      // TODO: Need to deal with the terminating condition and branch instructions.
    }
  }

  override def visitComma_expression(ctx: DajaParser.Comma_expressionContext): Instruction = {
    if (ctx.comma_expression == null) {
      visit(ctx.assignment_expression)
    }
    else {
      // TODO: We should process all the assignment expressions in the comma expression.
      visit(ctx.assignment_expression)
    }
  }

  override def visitAssignment_expression(ctx: DajaParser.Assignment_expressionContext): Instruction = {
    if (ctx.assignment_expression == null) {
      visit(ctx.relational_expression)
    }
    else {
      val rightHandSide = visit(ctx.assignment_expression)
      val targetName = ctx.relational_expression.add_expression.multiply_expression.postfix_expression.primary_expression.IDENTIFIER.getText
      StoreInstruction(rightHandSide, targetName)
    }
  }

  override def visitRelational_expression(ctx: DajaParser.Relational_expressionContext): Instruction = {
    // TODO: This is incomplete, but we are not supporting relational operators right now.
    visit(ctx.add_expression)
  }

  override def visitAdd_expression(ctx: DajaParser.Add_expressionContext): Instruction = {
    if (ctx.add_expression == null) {
      visit(ctx.multiply_expression)
    }
    else {
      val leftOperand = visit(ctx.add_expression)
      val rightOperand = visit(ctx.multiply_expression)
      if (ctx.PLUS != null) {
        AddInstruction(leftOperand, rightOperand)
      }
      else {
        SubInstruction(leftOperand, rightOperand)
      }
    }
  }

  override def visitMultiply_expression(ctx: DajaParser.Multiply_expressionContext): Instruction = {
    if (ctx.multiply_expression == null) {
      visit(ctx.postfix_expression)
    }
    else {
      val leftOperand = visit(ctx.multiply_expression)
      val rightOperand = visit(ctx.postfix_expression)
      if (ctx.MULTIPLY != null) {
        MulInstruction(leftOperand, rightOperand)
      }
      else {
        SDivInstruction(leftOperand, rightOperand)
      }
    }
  }

  override def visitPrimary_expression(ctx: DajaParser.Primary_expressionContext): Instruction = {
    if (ctx.IDENTIFIER != null) {
      LoadInstruction(ctx.IDENTIFIER.getText)
    }
    else if (ctx.INTEGER_LITERAL != null) {
      val (value, _) = Literals.convertIntegerLiteral(ctx.INTEGER_LITERAL.getText)
      ConstantInstruction(value.toInt)
    }
    else if (ctx.LPARENS != null) {
      visit(ctx.expression)
    }
    else {
      NullInstruction("PLACEHOLDER: Boolean literals not supported")
    }
  }
}
