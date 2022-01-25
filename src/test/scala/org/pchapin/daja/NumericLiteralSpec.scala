package org.pchapin.daja

import Literals.*

class NumericLiteralSpec extends UnitSpec {

  "An integer literal" should "be decoded and typed correctly" in {
    val testCases = Array(
      // Let's explore legal uses of the underscore.
      ("1_2_3_4", BigInt("1234"), TypeRep.IntRep),
      ("1_____2", BigInt("12"), TypeRep.IntRep),

      // Let's explore decimal values without suffixes.
      ("0", BigInt("0"), TypeRep.IntRep),
      ("1", BigInt("1"), TypeRep.IntRep),
      ("32767", BigInt("32767"), TypeRep.IntRep),
      ("32768", BigInt("32768"), TypeRep.IntRep),
      ("2_147_483_647", BigInt("2147483647"), TypeRep.IntRep),
      ("2_147_483_648", BigInt("2147483648"), TypeRep.LongRep),
      ("9_223_372_036_854_775_807", BigInt("9223372036854775807"), TypeRep.LongRep),
      ("9_223_372_036_854_775_808", BigInt("9223372036854775808"), TypeRep.ULongRep),
      ("18_446_744_073_709_551_615", BigInt("18446744073709551615"), TypeRep.ULongRep),

      // Let's explore decimal values with suffixes.
      ("0L", BigInt("0"), TypeRep.LongRep),
      ("9_223_372_036_854_775_807L", BigInt("9223372036854775807"), TypeRep.LongRep),
      ("0u", BigInt("0"), TypeRep.UIntRep),
      ("4_294_967_295U", BigInt("4294967295"), TypeRep.UIntRep),
      ("4_294_967_296U", BigInt("4294967296"), TypeRep.ULongRep),
      ("18_446_744_073_709_551_615U", BigInt("18446744073709551615"), TypeRep.ULongRep),
      ("0uL", BigInt("0"), TypeRep.ULongRep),
      ("0Lu", BigInt("0"), TypeRep.ULongRep),
      ("18_446_744_073_709_551_615UL", BigInt("18446744073709551615"), TypeRep.ULongRep)

      // TODO: Finish these!
      // Let's explore hexadecimal values without suffixes...
      // Let's explore hexadecimal values with suffixes...
      // Let's explore binary values without suffixes...
      // Let's explore binary values with suffixes...
    )

    for (testCase <- testCases) {
      val (literalValue, literalType) = convertIntegerLiteral(testCase._1)
      assert(literalValue == testCase._2)
      assert(literalType  == testCase._3)
    }
  }

  // Strictly speaking we only need to test for invalid literals that would pass the RE used
  // by the lexical analyzer. The lexical analyzer will not tokenize anything that doesn't match
  // one of its regular expressions. It may be reasonable to let the lexical analyzer match a
  // variety of illegal things that nevertheless appear approximately like integer literals so
  // that better error messages can be produced by 'convertIntegerLiteral' than the lexical
  // analyzer might otherwise produce.
  it should "be detected as invalid where appropriate" in {
    val testCases = Array(
      ("xyz", "Invalid start of literal") // Should never occur. The lexer won't tokenize this.
      // TODO: Add more! Out of range values, bad suffix combinations, bad use of underscore...
    )

    for (testCase <- testCases) {
      val caught = intercept[InvalidLiteralException] {
        val (_, _) = convertIntegerLiteral(testCase._1)
      }
      assert(caught.getMessage == testCase._2)
    }
  }


  "A floating literal" should "be decoded and typed correctly" in {
    // TODO: Finish me!
  }

  it should "be detected as invalid where appropriate" in {
    // TODO: Finish me!
  }

}
