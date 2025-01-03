package org.kelseymountain.daja

import org.antlr.v4.runtime.*

/**
 * The main class of the Daja compiler.
 */
object Main:

  /**
   * Specifies the different code generation modes that are supported.
   *
   * CHECK : Syntax and semantic check only, no code generation or execution.
   * INTER : Interpret the program. No generated output.
   * C     : Generate C source code as output.
   * JVM   : Generate JVM assembly language as output.
   * LLVM  : Generate LLVM assembly language as output.
   */
  enum Mode:
    case Check, Interpret, C, JVM, LLVM

  private val reporter = new BasicConsoleReporter

  /**
    * Process input files in the Daja language. This method runs the compiler pipeline.
    *
    * @param input The input file to compile/analyze/process.
    * @param mode The desired code generation target.
    */
  private def processDaja(input: CharStream, mode: Mode): Unit =
    // Parse the input file as Daja.
    val lexer  = new DajaLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new DajaParser(tokens)
    val tree: DajaParser.ModuleContext = parser.module()

    // Walk the tree created during the parse and analyze it for semantic errors.
    val mySymbolTable = new BasicSymbolTable
    val myTypeChecker = new TypeChecker(mySymbolTable, reporter)
    myTypeChecker.visit(tree)
    val myRuleChecker = new RuleChecker(mySymbolTable, reporter)
    myRuleChecker.visit(tree)

    if reporter.getErrorCount > 0 then
      // Errors have already been reported.
      printf("%d errors found; compilation aborted!", reporter.getErrorCount)
    else
      // No parse or semantic errors. Do various forms of analysis...

      // Construct the control flow graph of the module (really one function).
      val cfgBuilder = new CFGBuilder(mySymbolTable, reporter)
      val CFG = CFGBuilder.optimize(cfgBuilder.visit(tree))

      // Analyze program for the use of uninitialized variables.
      Analysis.liveness(CFG)
      val ControlFlowGraph(entryBlock, _, _) = CFG
      if entryBlock.live.nonEmpty then
        print("The following variables may be used uninitialized => ")
        for varName <- entryBlock.live do
          print(s"$varName ")
        print("\n")

      // Finally, code generation (if requested)...
      mode match
        case Mode.Check =>
          // Do nothing. Semantic analysis is all that is required.

        case Mode.Interpret =>
          val myInterpreter = new Interpreter(mySymbolTable, reporter)
          myInterpreter.visit(tree)

        case Mode.C =>
          println("C code generation is not implemented")
          val myCGenerator = new CGenerator(mySymbolTable, reporter)
          myCGenerator.visit(tree)

        case Mode.JVM =>
          println("JVM code generation is not implemented")
          val myJVMGenerator = new JVMGenerator(mySymbolTable, reporter)
          myJVMGenerator.visit(tree)

        case Mode.LLVM =>
          val myLLVMGenerator = new LLVMGenerator(mySymbolTable, CFG, reporter)
          myLLVMGenerator.generateCodeForCFG()
  end processDaja


  /**
   * The main program of the Daja compiler. This program accepts a file to compile on the
   * command line, along with some options for the desired target, and produces error messages
   * or a compiled output file. Daja programs can also be interpreted in some cases.
   *
   * @param args The command line arguments.
   * @throws java.io.IOException If an I/O error occurs during File I/O.
   */
  def main(args: Array[String]): Unit =
    println("Daja D Compiler (C) 2025 by Kelsey Mountain Software")

    // Analyze the command line.
    // TODO: Provide a more comprehensive and full-featured command line processing step.
    // Most compilers have a LOT of options. Daja will be no different eventually.
    if args.length != 2 then
      println("Usage: java -jar Daja (-k | -interp | -genc | -genj | -genl) source-file")
      System.exit(1)

    val mode = args(0) match
      case "-k" =>
        Mode.Check
      case "-interp" =>
        Mode.Interpret
      case "-genc" =>
        Mode.C
      case "-genj" =>
        Mode.JVM
      case "-genl" =>
        Mode.LLVM
      case _ =>
        println("Warning: Unknown mode, defaulting to Check!\n")
        Mode.Check

    // Create a stream that reads from the specified file.
    // TODO: Add support for processing multiple files.
    val input = CharStreams.fromFileName(args(1))
    processDaja(input, mode)
  end main

end Main
