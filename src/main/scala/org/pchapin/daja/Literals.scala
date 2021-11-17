package org.pchapin.daja

object Literals {

  object StateType extends Enumeration {
    val Start      : StateType.Value = Value
    val LeadingZero: StateType.Value = Value
    val GetDigits  : StateType.Value = Value
    val GetSuffix  : StateType.Value = Value
    val AtEnd      : StateType.Value = Value
  }

  object BaseType extends Enumeration {
    val Decimal: BaseType.Value = Value
    val Binary : BaseType.Value = Value
    val Hex    : BaseType.Value = Value
  }

  class InvalidLiteralException(message: String) extends Exception(message)

  /**
   * Uses a finite state machine to find the integer value of an integer literal. Note that
   * the lexical analyzer has already verified the format so certain simplifying assumptions
   * can potentially be made in the implementation of this method.
   *
   * This program implements the following finite state machine:
   *
   * <pre>
   *     START -- '0' --> LEADING_ZERO
   *     START -- non-zero digit --> GET_DIGITS (update value)
   *
   *     LEADING_ZERO -- 'b' or 'B' --> GET_DIGITS (set binary flag)
   *     LEADING_ZERO -- 'x' or 'X' --> GET_DIGITS (set hex flag)
   *
   *     GET_DIGITS -- digit or '_' --> GET_DIGITS (update value)
   *     GET_DIGITS -- 'L' or 'u' or 'U' --> GET_SUFFIX
   *
   *     GET_SUFFIX -- 'L' or 'u' or 'U' --> AT_END
   * </pre>
   *
   * @param text The text of the literal (e.g., "1_234U")
   * @return The converted value (e.g., 1234)
   */
  def convertIntegerLiteral(text: String): Int = {
    // TODO: Make the error strings for invalid literals more specific.
    // TODO: Right now literals such as 0xL are allowed. They should be illegal.
    var state = StateType.Start
    var base = BaseType.Decimal
    var value = 0
    var ch = '\u0000'

    for (i <- 0 until text.length()) {
      // TODO: This allows 'l' to be used instead of 'L'. Instead 'l' should be an error.
      ch = Character.toUpperCase(text.charAt(i))
      state match {
        case StateType.Start =>
          if (ch == '0') {
            state = StateType.LeadingZero
          }
          else if (ch >= '1' && ch <= '9') {
            value = 10 * value + (ch - '0')
            state = StateType.GetDigits
          }
          else {
            throw new InvalidLiteralException("Invalid start of literal.")
          }

        case StateType.LeadingZero =>
          ch match {
            case 'b' | 'B' =>
              base = BaseType.Binary
              state = StateType.GetDigits

            case 'x' | 'X' =>
              base = BaseType.Hex
              state = StateType.GetDigits

            case _ =>
              throw new InvalidLiteralException("Invalid number prefix.");
          }

        case StateType.GetDigits =>
          if (ch == 'L' || ch == 'U') {
            state = StateType.GetSuffix
          }
          else {
            base match {
              case BaseType.Decimal =>
                if ((ch >= '0' && ch <= '9') || ch == '_') {
                  if (ch != '_') {
                    value = 10 * value + (ch - '0')
                  }
                }
                else {
                  throw new InvalidLiteralException("Invalid decimal digit.")
                }

              case BaseType.Binary =>
                if ((ch >= '0' && ch <= '1') || ch == '_') {
                  if (ch != '_') {
                    value = 2 * value + (ch - '0')
                  }
                }
                else {
                  throw new InvalidLiteralException("Invalid binary digit.")
                }

              case BaseType.Hex =>
                if ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') || ch == '_') {
                  if (ch != '_') {
                    val hexConversion = "0123456789ABCDEF"
                    val digitValue = hexConversion.indexOf(ch)
                    value = 16 * value + digitValue
                  }
                }
                else {
                  throw new InvalidLiteralException("Invalid hex digit.")
                }
            }
          }

        case StateType.GetSuffix =>
          if (ch == 'L' || ch == 'U') {
            state = StateType.AtEnd
          }
          else {
            throw new InvalidLiteralException("Invalid suffix character.")
          }

        case StateType.AtEnd =>
          throw new InvalidLiteralException("Extraneous characters after literal")
      }
    }

    // Now check the state at the end looking for errors.
    state match {
      case StateType.Start =>
        throw new InvalidLiteralException("Error: Empty literal")

      case StateType.LeadingZero =>
        // No error. A single leading zero is allowed (and has the valuel zero).

      case StateType.GetDigits =>
        // No error. This means there was no suffix.

      case StateType.GetSuffix =>
        // No error. This means there was only one suffix character.

      case StateType.AtEnd =>
        // No error. This means there were two suffix characters.
    }
    value
  }

}
