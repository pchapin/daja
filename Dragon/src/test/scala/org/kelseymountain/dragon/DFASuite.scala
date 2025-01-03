package org.kelseymountain.dragon

import org.scalatest.*
import funsuite.*
import matchers.*

class DFASuite extends AnyFunSuite with Assertions with should.Matchers:

  test("Overall Test") {
    // Consider the RE: (a|b)*c

    // Let's build an NFA!
    // We will need primitive NFAs for the three strings "a", "b", and "c".
    val primitiveA = new NFA(0, 1, Map(NFATransitionFunctionArgument(0, 'a') -> Set(1)))
    val primitiveB = new NFA(0, 1, Map(NFATransitionFunctionArgument(0, 'b') -> Set(1)))
    val primitiveC = new NFA(0, 1, Map(NFATransitionFunctionArgument(0, 'c') -> Set(1)))

    // Create the combined NFA using Thompson's Construction.
    val rawNFA = (primitiveA union primitiveB).kleeneClosure concatenate primitiveC

    // Convert the NFA to a DFA using Subset Construction.
    val rawDFA = rawNFA.toDFA

    // Minimize the raw DFA using Hopcroft's Algorithm.
    val minimalDFA = rawDFA.minimize

    // TODO: Fix these failing tests.
    // These tests are failing because the DFA and NFA classes are not completely implemented.
    //assert(minimalDFA.`match`("c"))
    //assert(minimalDFA.`match`("ac"))
    //assert(minimalDFA.`match`("bc"))
    //assert(minimalDFA.`match`("aac"))
    //assert(minimalDFA.`match`("bbc"))
    //assert(minimalDFA.`match`("abc"))
    //assert(minimalDFA.`match`("abaabbc"))
  }

end DFASuite
