package org.kelseymountain.daja

import scalax.collection.edges.labeled.LDiEdge

case class ControlFlowEdge[N](
    override val label: Char,
    override val source: N,
    override val target: N) extends LDiEdge[N, Char]:

  require(label == 'U' || label == 'T' || label == 'F')

end ControlFlowEdge
