package edu.vtc.daja

import org.antlr.v4.runtime.tree.TerminalNode

import java.io._

class JVMGenerator(
  private val symbolTable: BasicSymbolTable,
  private val reporter   : Reporter) extends DajaBaseVisitor[Void] {

  private var localVariables = Map[String, Int]()
  private var localVariableCounter = 0
  private var output: PrintWriter = null
  private var expressionLevel = 0;  // Number of open 'expression' rules that are active.

  for (symbol <- symbolTable.getObjectNames) {
    localVariableCounter += 1
    localVariables += (symbol -> localVariableCounter)
  }

  /**
    * Computes a load/store instruction given the local variable name. This method tries to use
    * the abbreviated instruction forms if possible, falling back to the instructions that take
    * an operand only when necessary.
    *
    * @param baseInstruction The base name of instruction. Should be either "iload" or "istore."
    * @param localVariableName The name of the local variable.
    * @return The appropriate instruction, possibly with an operand.
    */
  private def getLoadStoreInstruction(baseInstruction: String, localVariableName: String): String = {
    val Some(localVariableNumber) = localVariables.get(localVariableName)
    localVariableNumber match {
      case 0 | 1 | 2 | 3 =>
        baseInstruction + "_" + localVariableNumber

      case _ =>
        baseInstruction + " " + localVariableNumber;
    }
  }


  override def visitModule(ctx: DajaParser.ModuleContext): Void = {
    try {
      val className = ctx.IDENTIFIER.getText
      output =
        new PrintWriter(
          new BufferedWriter(
            new OutputStreamWriter(
              new FileOutputStream(className + ".j"), "US-ASCII")))

      output.println(".class public " + className)
      output.println(".super java/lang/Object")
      output.println()
      output.println(".method public <init>()V")
      output.println("    aload_0")
      output.println("    invokenonvirtual java/lang/Object/<init>()V")
      output.println("    return")
      output.println(".end method")
      output.println()
      output.println(".method public static main([Ljava/lang/String;)V")
      output.println("    .limit locals " + (localVariableCounter + 1)) // +1 for the argument?
      // TODO: Compute an appropriate size for the operand stack.
      output.println("    .limit stack 4")
      output.println("    ;")
      output.println("    ; Local Variable Table")
      output.println("    ; ====================")
      for ( (name, number) <- localVariables) {
        output.println("    ;  " + name + ": " + number)
      }
      output.println("    ;")

      visitChildren(ctx)

      output.println("    return")
      output.println(".end method")
      output.close()
    }
    catch {
      case ex: FileNotFoundException =>
        // TODO: Do something sensible here!

      case ex: UnsupportedEncodingException =>
        // TODO: Do something sensible here!
    }
    null
  }


  override def visitTerminal(node: TerminalNode): Void = {
    try {
      if (expressionLevel > 0) {
        node.getSymbol.getType match {
          case DajaLexer.IDENTIFIER =>
            output.println("    " + getLoadStoreInstruction("iload", node.getText))

          case DajaLexer.NUMERIC_LITERAL =>
            val literalValue = Literals.convertIntegerLiteral(node.getText)
            val instruction =
              if (literalValue >= -128 && literalValue <= 127)
                "bipush"
              else if (literalValue >= -32768 && literalValue <= 32767)
                "sipush"
              else
                // TODO: The operand of ldc is really an index into the constant pool!
                "ldc"
            output.println(
              "    " + instruction + " " + Literals.convertIntegerLiteral(node.getText))
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
          ex.getMessage);
    }
    null
  }


  override def visitMultiply_expression(ctx: DajaParser.Multiply_expressionContext): Void = {
    if (ctx.MULTIPLY() != null) {
      visit(ctx.multiply_expression())
      visit(ctx.postfix_expression())
      output.println("    imul")
    }
    else if (ctx.DIVIDE() != null) {
      visit(ctx.multiply_expression())
      visit(ctx.postfix_expression())
      output.println("    idiv")
    }
    else {
      visit(ctx.postfix_expression())
    }
    null
  }


  override def visitAdd_expression(ctx: DajaParser.Add_expressionContext): Void = {
    if (ctx.PLUS() != null) {
      visit(ctx.add_expression())
      visit(ctx.multiply_expression())
      output.println("    iadd")
    }
    else if (ctx.MINUS() != null) {
      visit(ctx.add_expression())
      visit(ctx.multiply_expression())
      output.println("    isub")
    }
    else {
      visit(ctx.multiply_expression())
    }
    null
  }


  override def visitExpression(ctx: DajaParser.ExpressionContext): Void = {
    expressionLevel += 1
    visitChildren(ctx)
    expressionLevel -= 1
    null
  }

}
