package org.pchapin.daja

import java.io.FileReader

/**
 * The main class of the CommentStrip utility.
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
 *     MAYBE_COMMENT -- '+' --> NESTED_COMMENT (print a space; set count to 1)
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
 *     NESTED_COMMENT -- '/' --> MAYBE_NESTED_COMMENT
 *     NESTED_COMMENT -- '+' --> MAYBE_NESTED_UNCOMMENT
 *     NESTED_COMMENT -- '\n' --> NESTED_COMMENT (print character)
 *     NESTED_COMMENT -- others --> NESTED_COMMENT
 *
 *     MAYBE_NESTED_COMMENT -- '+' --> NESTED_COMMENT (inc count)
 *     MAYBE_NESTED_COMMENT -- '/' --> MAYBE_NESTED_COMMENT
 *     MAYBE_NESTED_COMMENT -- others --> NESTED_COMMENT
 *
 *     MAYBE_NESTED_UNCOMMENT -- '/' --> (dec count; if (count == 0) NORMAL else NESTED_COMMENT
 *     MAYBE_NESTED_UNCOMMENT -- '+' --> MAYBE_NESTED_UNCOMMENT
 *     MAYBE_NESTED_UNCOMMENT -- others --> NESTED_COMMENT
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
object CommentStrip {

  object StateType extends Enumeration {
    val NORMAL = Value
    val MAYBE_COMMENT = Value
    val SLASH_SLASH_COMMENT = Value
    val BLOCK_COMMENT = Value
    val MAYBE_UNCOMMENT = Value
    val NESTED_COMMENT = Value
    val MAYBE_NESTED_COMMENT = Value
    val MAYBE_NESTED_UNCOMMENT = Value
    val DOUBLE_QUOTE = Value
    val SINGLE_QUOTE = Value
    val ESCAPE_ONE_DOUBLE = Value
    val ESCAPE_ONE_SINGLE = Value
  }


  private def print(ch: Int): Unit = {
    System.out.print(ch.asInstanceOf[Char])
  }


  def main(args: Array[String]): Unit = {
    var state = StateType.NORMAL
    val input = new FileReader(args(0))
    var count = 0          // The nesting depth of nesting comments.
    var lineNumber = 1     // The current line.
    var columnNumber = 0   // The current column.
    var commentLine = 0    // The line coordinate of the last block comment opening.
    var commentColumn = 0  // The column coordinate of the last block comment opening.
    var ch: Int = 0        // A character from the input file.

    while ({ ch = input.read(); ch != -1 }) {
      // Adjust file coordinates.
      if (ch == '\n')
        { lineNumber += 1; columnNumber = 0 }
      else
        columnNumber += 1

      // Process this character.
      state match {
        case StateType.NORMAL =>
          ch match {
            case '/'  =>            state = StateType.MAYBE_COMMENT
            case '"'  => print(ch); state = StateType.DOUBLE_QUOTE
            case '\'' => print(ch); state = StateType.SINGLE_QUOTE
            case _    => print(ch)
          }

        case StateType.MAYBE_COMMENT =>
          ch match {
            case '/'  => state = StateType.SLASH_SLASH_COMMENT

            case '*'  =>
              print(' ')
              state = StateType.BLOCK_COMMENT
              commentLine = lineNumber
              commentColumn = columnNumber - 1

            case '+'  =>
              print(' ')
              count = 1
              state = StateType.NESTED_COMMENT
              commentLine = lineNumber
              commentColumn = columnNumber - 1

            case '"'  => print('/'); print(ch); state = StateType.DOUBLE_QUOTE
            case '\'' => print('/'); print(ch); state = StateType.SINGLE_QUOTE
            case _    => print('/'); print(ch); state = StateType.NORMAL
          }

        case StateType.SLASH_SLASH_COMMENT =>
          if (ch == '\n') {
            print('\n')
            state = StateType.NORMAL
          }

        case StateType.BLOCK_COMMENT =>
          ch match {
            case '*'  =>            state = StateType.MAYBE_UNCOMMENT
            case '\n' => print(ch);
            case _    =>
          }

        case StateType.MAYBE_UNCOMMENT =>
          ch match {
            case '/'  => state = StateType.NORMAL
            case '*'  =>
            case _    => state = StateType.BLOCK_COMMENT
          }

        case StateType.NESTED_COMMENT =>
          ch match {
            case '/'  =>            state = StateType.MAYBE_NESTED_COMMENT
            case '+'  =>            state = StateType.MAYBE_NESTED_UNCOMMENT
            case '\n' => print(ch)
            case _    =>
          }

        case StateType.MAYBE_NESTED_COMMENT =>
          ch match {
            case '+' => count += 1; state = StateType.NESTED_COMMENT
            case '/' =>
            case _   =>             state = StateType.NESTED_COMMENT
          }

        case StateType.MAYBE_NESTED_UNCOMMENT =>
          ch match {
            case '/' =>
              count -= 1
              if (count == 0) state = StateType.NORMAL else state = StateType.NESTED_COMMENT
            case '+' =>
            case _   => state = StateType.NESTED_COMMENT
          }

        case StateType.DOUBLE_QUOTE =>
          ch match {
            case '\\' => print(ch); state = StateType.ESCAPE_ONE_DOUBLE
            case '"'  => print(ch); state = StateType.NORMAL
            case _    => print(ch)
          }

        case StateType.SINGLE_QUOTE =>
          ch match {
            case '\\' => print(ch); state = StateType.ESCAPE_ONE_SINGLE
            case '\'' => print(ch); state = StateType.NORMAL
            case _    => print(ch)
          }

        case StateType.ESCAPE_ONE_DOUBLE =>
          print(ch)
          state = StateType.DOUBLE_QUOTE

        case StateType.ESCAPE_ONE_SINGLE =>
          print(ch)
          state = StateType.SINGLE_QUOTE
      }
    }

    // Check the state at the end of the input.
    state match {
      case StateType.BLOCK_COMMENT        |
           StateType.MAYBE_UNCOMMENT      |
           StateType.NESTED_COMMENT       |
           StateType.MAYBE_NESTED_COMMENT |
           StateType.MAYBE_NESTED_UNCOMMENT =>
        printf("Unclosed block comment at end of input. Comment starts here: (%d, %d)", commentLine, commentColumn)

      case StateType.DOUBLE_QUOTE |
           StateType.ESCAPE_ONE_DOUBLE =>
        printf("Unclosed double quoted string at end of input")

      case StateType.SINGLE_QUOTE |
           StateType.ESCAPE_ONE_SINGLE =>
        printf("Unclosed single quoted string at end of input")

      case _ =>
        // No error.
    }
  }

}
