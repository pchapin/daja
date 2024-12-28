package org.kelseymountain.daja

import scala.jdk.CollectionConverters.*
import scalax.collection.mutable.Graph
import scalax.collection.OuterImplicits.toOuterEdge
import scalax.collection.OuterImplicits.anyToNode    // Is this really correct?

class CFGBuilder(
  symbolTable: SymbolTable,
  reporter   : Reporter) extends DajaBaseVisitor[ControlFlowGraph] {

  private def combineStatementSequence(statements: Iterable[DajaParser.StatementContext]): ControlFlowGraph = {

    val graphList = statements map { visit(_) }

    graphList reduce { (left: ControlFlowGraph, right: ControlFlowGraph) =>
      (left, right) match {
        case (ControlFlowGraph(leftEntry, leftGraph, leftExit),
              ControlFlowGraph(rightEntry, rightGraph, rightExit)) =>

          ControlFlowGraph(
            leftEntry,
            (leftGraph union rightGraph) union Graph(ControlFlowEdge('U', leftExit, rightEntry)),
            rightExit)
      }
    }
  }

  override def visitModule(ctx: DajaParser.ModuleContext): ControlFlowGraph = {
    // TODO: Initialized declarations should be the first basic block of the procedure's CFG.
    visit(ctx.block_statement)
  }


  override def visitBlock_statement(ctx: DajaParser.Block_statementContext): ControlFlowGraph = {
    combineStatementSequence(ctx.statement.asScala)
  }


  override def visitExpression_statement(ctx: DajaParser.Expression_statementContext): ControlFlowGraph = {

    val primitiveBlock = new BasicBlock(List(ctx), None)
    ControlFlowGraph(primitiveBlock, Graph(primitiveBlock), primitiveBlock)
  }


  // TODO: Implement the CFG construction of conditional statements.
  override def visitIf_statement(
    ctx: DajaParser.If_statementContext): ControlFlowGraph = {

    val nullBlock = new BasicBlock(List(), None)
    ControlFlowGraph(nullBlock, Graph(nullBlock), nullBlock)
  }


  override def visitWhile_statement(ctx: DajaParser.While_statementContext): ControlFlowGraph = {

    val expressionBlock = new BasicBlock(List(), Some(ctx.expression))
    val nullBlock = new BasicBlock(List(), None)
    val ControlFlowGraph(bodyEntry, bodyGraph, bodyExit) =
      combineStatementSequence(ctx.block_statement.statement.asScala)

    val allNodesGraph: Graph[BasicBlock, ControlFlowEdge[BasicBlock]] =
      Graph(expressionBlock, nullBlock) union bodyGraph

    val overallGraph = allNodesGraph union
      Graph(ControlFlowEdge('T', expressionBlock, bodyEntry),
            ControlFlowEdge('F', expressionBlock, nullBlock),
            ControlFlowEdge('U', bodyExit, expressionBlock))

    ControlFlowGraph(expressionBlock, overallGraph, nullBlock)
  }
}

object CFGBuilder {

  /**
   * Method that optimizes the CFG by 1) removing all possible null blocks, and 2) combining
   * blocks when possible to eliminate or minimize the number of blocks containing just one
   * assignment statement.
   *
   * @param CFG The control flow graph to optimize.
   * @return The optimized control flow graph.
   */
  def optimize(CFG: ControlFlowGraph): ControlFlowGraph = {
    // TODO: Implement CFG optimization.
    CFG
  }

}
