package edu.vermontstate.daja

class TrivialSpec extends UnitSpec {

  // "Subject" should "statement of specification" in ...
  "The universe" should "work correctly" in {
    val s = "Hello"

    // Use 'assert' to check expected true conditions.
    assert(true)

    // Use 'assetResult' to check that the result of an expression is as expected.
    assertResult(2) {
      1 + 1
    }

    // Use 'assertThrows' to check that a particular exception is thrown by an expression.
    assertThrows[IndexOutOfBoundsException] {
      s.charAt(5)
    }

    // Use 'intercept' to catch an expression and then assert something about it.
    val caught = intercept[IndexOutOfBoundsException] {
      s.charAt(5)
    }
    assert(caught.getMessage.indexOf("5") != -1)
  }
}
