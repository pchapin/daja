package org.pchapin.daja

/**
  * A representation of basic blocks. Here the assignment statements are "full" expression
  * statements with arbitrary expressions (and not, for example, SSA form assignments).
  * Similarly the expression used at the end of the basic block is an arbitrary expression.
  *
  * @param assignments A list of expression statements forming the basic block.
  * @param condition The condition at the end of the basic block or None if the block ends with
  * an unconditional jump.
  * @param upwardlyExposed The set of variables used in this block without being defined.
  * @param killed The set of variables defined in this block.
  * @param live The set of variables that are live at the end of this block.
  */
class BasicBlock(val assignments: List[DajaParser.Expression_statementContext],
                 val condition  : Option[DajaParser.ExpressionContext],
                 var upwardlyExposed: Set[String] = Set(),
                 var killed         : Set[String] = Set(),
                 var live           : Set[String] = Set())
// TODO: Do I need to override equals and hashCode for this class?
