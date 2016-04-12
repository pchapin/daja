package edu.vtc.daja

class Interpreter(
  private val symbolTable: BasicSymbolTable,
  private val reporter   : Reporter) extends DajaBaseListener
