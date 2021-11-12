package org.pchapin.daja

class LLVMGenerator(
  private val symbolTable: BasicSymbolTable,
  private val reporter   : Reporter) extends DajaBaseVisitor[Void]
