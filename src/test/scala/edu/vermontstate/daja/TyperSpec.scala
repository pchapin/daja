package edu.vermontstate.daja

import java.io.File
import org.antlr.v4.runtime.{CharStreams, CommonTokenStream}

class TyperSpec extends UnitSpec {

  private val testRoot   = new File("testData")
  private val testTyper = new File(testRoot, "TypeCheck")
  private val typerPositive = new File(testTyper, "Positive")
  private val typerNegative = new File(testTyper, "Negative")

  /**
    * Execute positive test cases. These are test cases that are intended to be working code.
    *
    * @param testCaseNames An array of file names representing the test cases.
    */
  private def doPositiveTests(testCaseNames: Array[String]): Unit = {
    for (testCaseName <- testCaseNames) {
      val testCase = new File(typerPositive, testCaseName)
      val lexer  = new DajaLexer(CharStreams.fromFileName(testCase.getPath))
      val tokens = new CommonTokenStream(lexer)
      val parser = new DajaParser(tokens)
      val tree: DajaParser.ModuleContext = parser.module()
      val mySymbolTable = new BasicSymbolTable
      val reporter = new BasicConsoleReporter
      val myTypeChecker = new TypeChecker(mySymbolTable, reporter)
      myTypeChecker.visit(tree)
      // Positive tests should have no type errors.
      assert(reporter.getErrorCount == 0)
    }
  }


  /**
   * Execute negative test cases. These are test cases that are intended to be non-working code.
   *
   * @param testCaseNames An array of file names representing the test cases.
   */
  private def doNegativeTests(testCaseNames: Array[String]): Unit = {
    for (testCaseName <- testCaseNames) {
      val testCase = new File(typerNegative, testCaseName)
      val lexer  = new DajaLexer(CharStreams.fromFileName(testCase.getPath))
      val tokens = new CommonTokenStream(lexer)
      val parser = new DajaParser(tokens)
      val tree: DajaParser.ModuleContext = parser.module()
      val mySymbolTable = new BasicSymbolTable
      val reporter = new BasicConsoleReporter
      val myTypeChecker = new TypeChecker(mySymbolTable, reporter)
      myTypeChecker.visit(tree)
      // Negative tests should have errors.
      // TODO: Better would be to check that the error messages are as expected.
      // What is needed is a Reporter class that holds the messages internally so we can later
      // compare the reported messages against a list of expected messages.
      assert(reporter.getErrorCount != 0)
    }
  }


  "The Daja Type Checker" should "type check valid integer expressions" in {
    val testCaseNames =
      Array("IntegerExpr0000.daja")
    doPositiveTests(testCaseNames)
  }


  it should "type check valid boolean expressions" in {
    val testCaseNames =
      Array("BooleanExpr0000.daja")
    doPositiveTests(testCaseNames)
  }


  it should "type check valid array expressions" in {
    val testCaseNames =
      Array("ArrayExpr0000.daja")
    doPositiveTests(testCaseNames)
  }


  it should "type check valid simple statements" in {
    val testCaseNames =
      Array("Statement0000.daja")
    doPositiveTests(testCaseNames)
  }


  it should "report errors for invalid integer expressions" in {
    val testCaseNames =
      Array("IntegerExpr0000.daja")
    doNegativeTests(testCaseNames)
  }


  it should "report errors for invalid simple statements" in {
    val testCaseNames =
      Array("Statement0000.daja")
    doNegativeTests(testCaseNames)
  }

}
