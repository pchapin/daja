package org.pchapin.daja

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.tree._

/**
 * The main class of the Daja compiler.
 */
object Main {

  /**
    * Specifies the different modes of operation that are supported.
    *
    * CHECK    : Syntax and semantic check only, no code generation or execution.
    * LLVM     : Generate LLVM assembly language as output.
    */
  private object Mode extends Enumeration {
    val CHECK = Value
    val LLVM = Value
  }

  private val reporter = new BasicConsoleReporter

  /**
    * Process input files in the Daja language. This method runs the compiler pipeline.
    *
    * @param input The input file to compile.
    * @param mode The desired target or mode of operation.
    */
  private def processDaja(input: ANTLRFileStream, mode: Mode.Value): Unit = {
    // Parse the input file as Daja.
    val lexer  = new DajaLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new DajaParser(tokens)
    val tree: DajaParser.ModuleContext = parser.module()

    // Walk the tree created during the parse and analyze it for semantic errors.
    val symbolTable = new BasicSymbolTable
    val myAnalyzer  = new SemanticAnalyzer(symbolTable, reporter)
    myAnalyzer.visit(tree)

    if (reporter.getErrorCount > 0) {
      printf("%d errors found; compilation aborted!", reporter.getErrorCount)
    }
    else {
      // Construct the control flow graph of the module (really one function).
      val cfgBuilder = new CFGBuilder(symbolTable, reporter)
      val CFG = CFGBuilder.optimize(cfgBuilder.visit(tree))

      // Analyze program for the use of uninitialized variables.
      Analysis.liveness(CFG)
      val ControlFlowGraph(entryBlock, _, _) = CFG
      if (entryBlock.live.nonEmpty) {
        print("The following variables may be used uninitialized => ")
        for (varName <- entryBlock.live) {
          print(s"$varName ")
        }
        print("\n")
      }

      mode match {
        case Mode.CHECK =>
          // Do nothing. Semantic analysis is all that is required.

        case Mode.LLVM =>
          System.out.println("LLVM code generation is not implemented!")
          val myLLVMGenerator = new LLVMGenerator(symbolTable, reporter)
          myLLVMGenerator.visit(tree)
      }
    }
  }


  /**
    * The main program of the Daja educational compiler. This program accepts a file to compile
    * on the command line, along with some options the desired target, and produces error
    * messages or a compiled output file. Daja programs can also be interpreted in some cases.
    *
    * @param args The command line arguments.
    * @throws java.io.IOException If an I/O error occurs during File I/O.
    */
  def main(args: Array[String]): Unit = {
    println("Daja D Compiler (C) 2017 by Vermont Technical College")

    // Analyze the command line.
    if (args.length != 2) {
      println("Usage: java -jar Daja (-k | -l) source-file")
      System.exit(1)
    }

    val mode = args(0) match {
      case "-k" =>
        Mode.CHECK
      case "-l" =>
        Mode.LLVM
      case _ =>
        println("Error: Unknown mode, defaulting to CHECK!\n")
        Mode.CHECK
    }

    // Create a stream that reads from the specified file.
    val input = new ANTLRFileStream(args(1))
    processDaja(input, mode)
  }
}
