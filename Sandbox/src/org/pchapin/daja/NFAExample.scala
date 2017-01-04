package org.pchapin.daja

object NFAExample {

    def main(args: Array[String]) = {
        // Consider the RE: (a|b)*c

        // Let's build an NFA!
        // First, we will need primitive NFAs for the three strings "a", "b", and "c".
        // To get started we'll need maps to hold the transition functions for those primitives.
        var aTransitionFunction = Map[TransitionFunctionArgument, Set[Integer]]()
        var bTransitionFunction = Map[TransitionFunctionArgument, Set[Integer]]()
        var cTransitionFunction = Map[TransitionFunctionArgument, Set[Integer]]()

        // The transition functions return sets so we'll need some sets.
        var aSet = Set[Integer]()
        var bSet = Set[Integer]()
        var cSet = Set[Integer]()

        // In each case the transition function's only transition return the set { 1 } (because
        // all the primitive transition functions only go from state 0 to state 1.
        aSet += 1
        bSet += 1
        cSet += 1

        // Let's initialize the transition function maps.
        aTransitionFunction += (TransitionFunctionArgument(0, 'a') -> aSet)
        bTransitionFunction += (TransitionFunctionArgument(0, 'b') -> bSet)
        cTransitionFunction += (TransitionFunctionArgument(0, 'c') -> cSet)

        // There is probably a more elegant way to initialize all these maps and sets (let's
        // hope!). The method here is ungainly and doesn't scale well. I found some approaches
        // online but they didn't seem much better for this small example. This is an area where
        // a functional language like Scala excels. Even C++ 2011 provides more convenient
        // initialization syntax (than this) for complex data structures!

        // Now we are ready to create the primitive NFAs.
        val primitiveA = new NFA(0, 1, aTransitionFunction)
        val primitiveB = new NFA(0, 1, bTransitionFunction)
        val primitiveC = new NFA(0, 1, cTransitionFunction)

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
