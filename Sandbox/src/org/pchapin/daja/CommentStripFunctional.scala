package org.pchapin.daja

/**
 * The main class of the CommentStripFunctional utility.
 *
 * This program implements the following finite state machine:
 *
 * {{{
 *     NORMAL -- '/' --> MAYBE_COMMENT
 *     NORMAL -- '"' --> DOUBLE_QUOTE (print character)
 *     NORMAL -- '\'' --> SINGLE_QUOTE (print character)
 *     NORMAL -- others --> NORMAL (print character)
 *
 *     MAYBE_COMMENT -- '/' --> SLASH_SLASH_COMMENT
 *     MAYBE_COMMENT -- '*' --> BLOCK_COMMENT (print a space)
 *     MAYBE_COMMENT -- '"' --> DOUBLE_QUOTE (print slash; print character)
 *     MAYBE_COMMENT -- '\'' --> SINGLE_QUOTE (print slash; print character)
 *     MAYBE_COMMENT -- others --> NORMAL (print slash; print character)
 *
 *     SLASH_SLASH_COMMENT -- '\n'--> NORMAL (print '\n')
 *     SLASH_SLASH_COMMENT -- others --> SLASH_SLASH_COMMENT
 *
 *     BLOCK_COMMENT -- '*' --> MAYBE_UNCOMMENT
 *     BLOCK_COMMENT -- '\n' --> BLOCK_COMMENT (print character)
 *     BLOCK_COMMENT -- others --> BLOCK_COMMENT
 *
 *     MAYBE_UNCOMMENT -- '/' --> NORMAL
 *     MAYBE_UNCOMMENT -- '*' --> MAYBE_UNCOMMENT
 *     MAYBE_UNCOMMENT -- others --> BLOCK_COMMENT
 *
 *     DOUBLE_QUOTE -- '\\' --> ESCAPE_ONE_DOUBLE (print character)
 *     DOUBLE_QUOTE -- '"' --> NORMAL (print character)
 *     DOUBLE_QUOTE -- others --> DOUBLE_QUOTE (print character)
 *
 *     SINGLE_QUOTE -- '\\' --> ESCAPE_ONE_SINGLE (print character)
 *     SINGLE_QUOTE -- '\'' --> NORMAL (print character)
 *     SINGLE_QUOTE -- others --> SINGLE_QUOTE (print character)
 *
 *     ESCAPE_ONE_DOUBLE -- others --> DOUBLE_QUOTE (print character)
 *     ESCAPE_ONE_SINGLE -- others --> SINGLE_QUOTE (print character)
 * }}}
 */
object CommentStripFunctional {

  object StateType extends Enumeration {
    val NORMAL = Value
    val MAYBE_COMMENT = Value
    val SLASH_SLASH_COMMENT = Value
    val BLOCK_COMMENT = Value
    val MAYBE_UNCOMMENT = Value
    val DOUBLE_QUOTE = Value
    val SINGLE_QUOTE = Value
    val ESCAPE_ONE_DOUBLE = Value
    val ESCAPE_ONE_SINGLE = Value
  }


  type FSMState = (StateType.Value, List[Char])

  def combiner(accumulator: FSMState, ch: Char): FSMState = {
    val (state, currentOutput) = accumulator
    state match {
      case StateType.NORMAL =>
        ch match {
          case '/'  => (StateType.MAYBE_COMMENT, currentOutput)
          case '"'  => (StateType.DOUBLE_QUOTE, ch :: currentOutput)
          case '\'' => (StateType.SINGLE_QUOTE, ch :: currentOutput)
          case _    => (state, ch :: currentOutput)
        }

      case StateType.MAYBE_COMMENT =>
        ch match {
          case '/' => (StateType.SLASH_SLASH_COMMENT, currentOutput)
          case '*' => (StateType.BLOCK_COMMENT, ' ' :: currentOutput)
          case '"' => (StateType.DOUBLE_QUOTE, ch :: '/' :: currentOutput)
          case '\''=> (StateType.SINGLE_QUOTE, ch :: '/' :: currentOutput)
          case _   => (StateType.NORMAL, ch :: '/' :: currentOutput)
        }

      case StateType.SLASH_SLASH_COMMENT =>
        if (ch == '\n') {
          (StateType.NORMAL, '\n' :: currentOutput)
        }
        else {
          (state, currentOutput)
        }

      case StateType.BLOCK_COMMENT =>
        ch match {
          case '*'  => (StateType.MAYBE_UNCOMMENT, currentOutput)
          case '\n' => (state, ch :: currentOutput)
          case _    => (state, currentOutput)
        }

      case StateType.MAYBE_UNCOMMENT =>
        ch match {
          case '/' => (StateType.NORMAL, currentOutput)
          case '*' => (state, currentOutput)
          case _   => (StateType.BLOCK_COMMENT, currentOutput)
        }

      case StateType.DOUBLE_QUOTE =>
        ch match {
          case '\\' => (StateType.ESCAPE_ONE_DOUBLE, ch :: currentOutput)
          case '"'  => (StateType.NORMAL, ch :: currentOutput)
          case _    => (state, ch :: currentOutput)
        }

      case StateType.SINGLE_QUOTE =>
        ch match {
          case '\\' => (StateType.ESCAPE_ONE_SINGLE, ch :: currentOutput)
          case '\'' => (StateType.NORMAL, ch :: currentOutput)
          case _    => (state, ch :: currentOutput)
        }

      case StateType.ESCAPE_ONE_DOUBLE =>
        (StateType.DOUBLE_QUOTE, ch :: currentOutput)

      case StateType.ESCAPE_ONE_SINGLE =>
        (StateType.SINGLE_QUOTE, ch :: currentOutput)
    }
  }


  def main(args: Array[String]): Unit = {
    val text = io.Source.fromFile(args(0))
    val (_, output) = text.foldLeft(StateType.NORMAL, List[Char]())(combiner)
    output.reverse foreach { System.out.print }
  }

}
