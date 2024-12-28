package org.kelseymountain.daja

class CGenerator(
  private val symbolTable: SymbolTable,
  private val reporter   : Reporter) extends DajaBaseVisitor[Void]
