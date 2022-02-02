package edu.vtc.daja

class Interpreter(
  private val symbolTable: SymbolTable,
  private val reporter   : Reporter) extends DajaBaseVisitor[Void]
