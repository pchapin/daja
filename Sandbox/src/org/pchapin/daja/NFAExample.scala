package org.pchapin.daja

object NFAExample {

    def main(args: Array[String]): Unit = {
        // Consider the RE: (a|b)*c

        // Let's build an NFA!
        // First, we will need primitive NFAs for the three strings "a", "b", and "c".
        val primitiveA = new NFA(0, 1, Map(TransitionFunctionArgument(0, 'a') -> Set(1)))
        val primitiveB = new NFA(0, 1, Map(TransitionFunctionArgument(0, 'b') -> Set(1)))
        val primitiveC = new NFA(0, 1, Map(TransitionFunctionArgument(0, 'c') -> Set(1)))

        // Now we can create the combined NFA using Thompson's Construction.
        val result = primitiveA.union(primitiveB).kleeneClosure().concatenate(primitiveC)

        // Now we can convert the NFA to a DFA using Subset Construction.
        val resultDFA = result.toDFA

        // We could minimize the resultDFA here using Hopcroft's Algorithm (but we won't).

        if (resultDFA.`match`("abbaabc")) {
            System.out.println("match [this is correct]")
        }
        else {
            System.out.println("no match [this is wrong]")
        }
    }

}
