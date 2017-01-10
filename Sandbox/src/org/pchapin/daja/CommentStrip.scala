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
object CommentStrip {

  object StateType extends Enumeration {
    val NORMAL = Value
    val MAYBE_COMMENT = Value
    val MAYBE_UNCOMMENT = Value
    val SLASH_SLASH_COMMENT = Value
    val BLOCK_COMMENT = Value
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
    var ch: Int = 0

    while ({ ch = input.read(); ch != -1 }) {
      state match {
        case StateType.NORMAL =>
          ch match {
            case '/'  =>            state = StateType.MAYBE_COMMENT
            case '"'  => print(ch); state = StateType.DOUBLE_QUOTE
            case '\'' => print(ch); state = StateType.SINGLE_QUOTE
            _: Char   => print(ch)
          }

        case StateType.MAYBE_COMMENT =>
          ch match {
            case '/' =>                        state = StateType.SLASH_SLASH_COMMENT
            case '*' => print(' ');            state = StateType.BLOCK_COMMENT
            case '"' => print('/'); print(ch); state = StateType.DOUBLE_QUOTE
            case '\''=> print('/'); print(ch); state = StateType.SINGLE_QUOTE
            _: Char  => print('/'); print(ch); state = StateType.NORMAL
          }

        case StateType.MAYBE_UNCOMMENT =>
          ch match {
            case '/' => state = StateType.NORMAL
            case '*' =>
            _: Char  => state = StateType.BLOCK_COMMENT
          }

        case StateType.SLASH_SLASH_COMMENT =>
          if (ch == '\n') {
            print('\n')
            state = StateType.NORMAL
          }

        case StateType.BLOCK_COMMENT =>
          ch match {
            case '*'  =>            state = StateType.MAYBE_UNCOMMENT
            case '\n' => print(ch)
          }

        case StateType.DOUBLE_QUOTE =>
          ch match {
            case '\\' => print(ch); state = StateType.ESCAPE_ONE_DOUBLE
            case '"'  => print(ch); state = StateType.NORMAL
            _: Char   => print(ch)
          }

        case StateType.SINGLE_QUOTE =>
          ch match {
            case '\\' => print(ch); state = StateType.ESCAPE_ONE_SINGLE
            case '\'' => print(ch); state = StateType.NORMAL
            _: Char   => print(ch)
          }

        case StateType.ESCAPE_ONE_DOUBLE =>
          print(ch)
          state = StateType.DOUBLE_QUOTE

        case StateType.ESCAPE_ONE_SINGLE =>
          print(ch)
          state = StateType.SINGLE_QUOTE
      }
    }
  }

}
