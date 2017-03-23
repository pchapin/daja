package org.pchapin.daja

import java.io.File
import org.antlr.v4.runtime.{ANTLRFileStream, CommonTokenStream}

class ParserSpec extends UnitSpec {

  private val testRoot   = new File("testData")
  private val testSyntax = new File(testRoot, "Syntax")
  private val syntaxPositive = new File(testSyntax, "Positive")
  private val syntaxNegative = new File(testSyntax, "Negative")

  /**
    * Execute test cases.
    *
    * @param testCaseNames An array of file names representing the test cases.
    * @param doParse A function that takes a DajaParser and executes the parse at a particular start symbol.
    */
  private def doTests(testCaseNames: Array[String], doParse: DajaParser => Unit) {
    for (testCaseName <- testCaseNames) {
      val testCase = new File(syntaxPositive, testCaseName)
      val lexer  = new DajaLexer(new ANTLRFileStream(testCase.getPath))
      val tokens = new CommonTokenStream(lexer)
      val parser = new DajaParser(tokens)
      doParse(parser)
    }
  }


  private def doExpressionTests(testCaseNames: Array[String]) {
    doTests(testCaseNames, _.expression())
  }


  private def doDeclarationTests(testCaseNames: Array[String]) {
    doTests(testCaseNames, _.declaration())
  }


  private def doStatementTests(testCaseNames: Array[String]) {
    doTests(testCaseNames, _.statement())
  }


  private def doModuleTests(testCaseNames: Array[String]) {
    doTests(testCaseNames, _.module())
  }


  "DajaParser" should "parse simple expressions" in {
    val testCaseNames =
      Array("Expression0000.daja")
    doExpressionTests(testCaseNames)
  }


  "DajaParser" should "parse a basic declaration" in {
    val testCaseNames =
      Array("Declaration0000.daja")
    doDeclarationTests(testCaseNames)
  }


  "DajaParser" should "parse simple statements" in {
    val testCaseNames =
      Array("Statement0000.daja")
    doStatementTests(testCaseNames)
  }


  "DajaParser" should "parse a basic module" in {
    val testCaseNames =
      Array("Module0000.daja", "Module0001.daja")
    doModuleTests(testCaseNames)
  }

}
