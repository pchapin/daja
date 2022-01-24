package org.pchapin.daja

import scala.math.{BigInt, BigDecimal}

object Literals {

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
   * @return The converted value (e.g., 1234) and type (e.g., TypeRep.UIntRep)
   */
  def convertIntegerLiteral(text: String): (BigInt, TypeRep.Rep) = {

    // Enumeration type to define the states of the DFA.
    object StateType extends Enumeration {
      val Start      : StateType.Value = Value
      val LeadingZero: StateType.Value = Value
      val GetDigits  : StateType.Value = Value
      val GetSuffix  : StateType.Value = Value
      val AtEnd      : StateType.Value = Value
    }

    // Enumeration type to define the various allowed bases.
    object BaseType extends Enumeration {
      val Decimal: BaseType.Value = Value
      val Binary : BaseType.Value = Value
      val Hex    : BaseType.Value = Value
    }

    // TODO: Make the error strings for invalid literals more specific.
    // TODO: Literals such as 0xUL are allowed. They should be illegal.
    // TODO: Literals with leading or trailing underscores are allowed. They should be illegal.
    var state = StateType.Start
    var base  = BaseType.Decimal
    var value = BigInt(0)

    for (ch <- text) {
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
            throw new InvalidLiteralException("Invalid start of literal")
          }

        case StateType.LeadingZero =>
          ch match {
            case 'b' | 'B' =>
              base = BaseType.Binary
              state = StateType.GetDigits

            case 'x' | 'X' =>
              base = BaseType.Hex
              state = StateType.GetDigits

            // This case arises for literals like '0L', '0U', '0LU', etc.
            case 'L' | 'U' | 'u' =>
              state = StateType.GetSuffix

            case _ =>
              throw new InvalidLiteralException("Invalid number prefix in literal");
          }

        case StateType.GetDigits =>
          if (ch == 'L' || ch == 'U' || ch == 'u') {
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
                  throw new InvalidLiteralException("Invalid decimal digit in literal")
                }

              case BaseType.Binary =>
                if ((ch >= '0' && ch <= '1') || ch == '_') {
                  if (ch != '_') {
                    value = 2 * value + (ch - '0')
                  }
                }
                else {
                  throw new InvalidLiteralException("Invalid binary digit in literal")
                }

              case BaseType.Hex =>
                if ((ch >= '0' && ch <= '9') || (ch.toUpper >= 'A' && ch.toUpper <= 'F') || ch == '_') {
                  if (ch != '_') {
                    val hexConversion = "0123456789ABCDEF"
                    val digitValue = hexConversion.indexOf(ch.toUpper)
                    value = 16 * value + digitValue
                  }
                }
                else {
                  throw new InvalidLiteralException("Invalid hex digit in literal")
                }
            }
          }

        case StateType.GetSuffix =>
          if (ch == 'L' || ch == 'U' || ch == 'u') {
            state = StateType.AtEnd
          }
          else {
            throw new InvalidLiteralException("Invalid suffix character in literal")
          }

        case StateType.AtEnd =>
          throw new InvalidLiteralException("Extraneous characters after literal")
      }
    }

    // Now check the state at the end looking for errors.
    state match {
      case StateType.Start =>
        throw new InvalidLiteralException("Empty literal")

      case StateType.LeadingZero =>
        // No error. A single leading zero is allowed (and has the value zero).

      case StateType.GetDigits =>
        // No error. This means there was no suffix.

      case StateType.GetSuffix =>
        // No error. This means there was only one suffix character.

      case StateType.AtEnd =>
        // No error. This means there were two suffix characters.
    }
    // TODO: Return proper type information about this integer literal.
    (value, TypeRep.NoTypeRep)
  }


  /**
   * Uses a finite state machine to find the floating value of a floating literal. Note that
   * the lexical analyzer has already verified the format so certain simplifying assumptions
   * can potentially be made in the implementation of this method.
   *
   * This program implements the following finite state machine:
   *
   * <pre>
   *     DOCUMENT ME!
   * </pre>
   *
   * @param text The text of the literal (e.g., "1.2345678e+4F")
   * @return The converted value (e.g., 12345.678) and type (e.g., TypeRep.FloatRep)
   */
  def convertFloatingLiteral(text: String): (BigDecimal, TypeRep.Rep) = {
    var value = BigDecimal(0)

    // TODO: FINISH ME!
    (value, TypeRep.NoTypeRep)
  }

}
