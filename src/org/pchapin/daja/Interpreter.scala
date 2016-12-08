package org.pchapin.daja

class Interpreter(
  private val symbolTable: BasicSymbolTable,
  private val reporter   : Reporter) extends DajaBaseListener
