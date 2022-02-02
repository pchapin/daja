package edu.vtc.daja

class LLVMGenerator(
  private val symbolTable: SymbolTable,
  private val reporter   : Reporter) extends DajaBaseVisitor[Void]
