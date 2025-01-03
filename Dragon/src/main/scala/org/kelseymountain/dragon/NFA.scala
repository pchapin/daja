package org.kelseymountain.dragon

/**
  * This class represents nondeterministic finite automata. The states of the NFA are numbered
  * with integers starting at startState and going to acceptState. The range of states is
  * contiguous.
  *
  * NFAs represented by this class contain only a single accept state. While this is a
  * restriction over the formal definition of an NFA, it is all that is necessary in this
  * application. The restriction does not limit expressiveness; for an NFA with multiple accept
  * states, a new NFA could be built that has a single accept state reachable from the original
  * accept states via epsilon transitions.
  */
class NFA(
  private val startState : Int,
  private val acceptState: Int,
  private val transitionFunction: Map[NFATransitionFunctionArgument, Set[Int]]):

  type TransitionFunctionType = Map[NFATransitionFunctionArgument, Set[Int]]

  /**
    * Blends the 'other' transition function into 'target' while renumbering states as necessary.
    * The combined transition function does not include any transitions from between the two
    * collections of states; they would have to be added later.
    *
    * For example, suppose 'target' is from an NFA with states numbered 11 .. 20 (inclusive) and
    * 'other' is from an NFA with states numbered 51 .. 55 (inclusive). It is not necessary for
    * all of these states to actually participate in the transition function(s). In this case,
    * otherStartState would be 51 and newStartState would be 21. The merged transition function
    * would use states 11 .. 25.
    *
    * More commonly, the NFAs might be using overlapping ranges of states such as 0 .. 9 and
    * 0 .. 14. In that case otherStartState would be 0 and newStartState would be 10, resulting
    * in a transition function that ranges (potentially) over state numbers 0 .. 24.
    *
    * @param target The base transition function. No states are renumbered.
    * @param other The transition function that is added to target. State renumbering occurs.
    * @param otherStartState The "start state" of the 'other' transition function. This state
    * is a reference point. It is not necessary for there to be a transition from this state in
    * the 'other' transition function.
    * @param newStartState The new effective start state of the transitions that are renumbered.
    * As with otherStartState this is a reference point only.
    * @return A new, combined transition function.
    */
  private def mergeTransitionFunctions(
    target: TransitionFunctionType,
    other : TransitionFunctionType,
    otherStartState: Int,
    newStartState  : Int): TransitionFunctionType =

    // Create transformed associations for the 'other' transition function, modifying the state
    // numbers in the process.
    val newAssociations = for ((otherArgument, otherResult) <- other) yield {

      // Convert state numbers in the other NFA's transition function argument.
      val newArgument =
        NFATransitionFunctionArgument(
          otherArgument.state - otherStartState + newStartState,
          otherArgument.inputCharacter)

      // Convert state numbers in the other NFA's transition function result set.
      val newResult = otherResult map { state => state - otherStartState + newStartState }
      newArgument -> newResult
    }

    target ++ newAssociations
  end mergeTransitionFunctions

  /**
    * Returns an NFA that is the concatenation of this NFA followed by the other NFA. Neither
    * NFA input to this operation is modified.
    */
  def concatenate(other: NFA): NFA =
    val secondEntry = acceptState + 1
    val newStartState = startState
    val newAcceptState = acceptState + (other.acceptState - other.startState + 1)

    val newTransitionFunction =
      mergeTransitionFunctions(
        transitionFunction,
        other.transitionFunction,
        other.startState,
        secondEntry)

    // Add an epsilon transition between the original final state and the other start state.
    val extraArgument = NFATransitionFunctionArgument(acceptState, '\u0000')
    val extraResult = Set[Int](secondEntry)
    val augmentedTransitionFunction =
      if newTransitionFunction.contains(extraArgument) then
        // Handle the case where there is already an epsilon transition from acceptState.
        val augmentedSet = newTransitionFunction(extraArgument) ++ extraResult
        newTransitionFunction + (extraArgument -> augmentedSet)
      else
        // Handle the case where there was no epsilon transition from acceptState.
        newTransitionFunction + (extraArgument -> extraResult)

    // Create the new NFA.
    new NFA(newStartState, newAcceptState, augmentedTransitionFunction)
  end concatenate

  /**
    * Returns an NFA that is the union of this NFA followed and the other NFA. Neither NFA
    * input to this operation is modified.
    */
  def union(other: NFA): NFA =
    // TODO: This method body is just a place holder!
    new NFA(startState, acceptState, transitionFunction)


  /**
    * Returns an NFA that is the Kleene closure of this NFA. This NFA is not modified.
    */
  def kleeneClosure: NFA =
    // TODO: This method body is just a place holder!
    new NFA(startState, acceptState, transitionFunction)


  /**
    * Returns true if this NFA is really a DFA (no use of epsilon transitions and only a single
    * state as the target of each transition.
    */
  def isDFA: Boolean =
    transitionFunction forall { association => {
      val (key, value) = association
      key.inputCharacter != '\u0000' && value.size == 1
    }}


  /**
    * Returns a DFA obtained from Subset Construction on this NFA. Note that the return value
    * will be such that isDFA() is true.
    */
  def toDFA: DFA =
    // TODO: This method body is just a place holder!
    new DFA(startState, acceptState, Map())

end NFA
