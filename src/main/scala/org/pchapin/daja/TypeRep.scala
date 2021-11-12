package org.pchapin.daja

object TypeRep {

  abstract class Rep

  // Placeholder for situations where there is no type.
  case object NoTypeRep extends Rep

  // Primitive types.
  case object BoolRep extends Rep
  case object IntRep extends Rep
  case object DoubleRep extends Rep

  // Constructed types.
  // Note that an array's size is not part of its type.
  case class ArrayRep(elementType: Rep) extends Rep
  case class FunctionRep(resultType: Rep, parameters: List[Rep]) extends Rep
  case class PointerRep(pointedAtType: Rep) extends Rep

}
