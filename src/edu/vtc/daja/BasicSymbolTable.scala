package edu.vtc.daja

import edu.vtc.daja.SymbolTable._

class BasicSymbolTable extends SymbolTable {

  private var objectMap = Map[String, TypeRep.Rep]()
  private var typeMap = Map[String, TypeRep.Rep]()

  def addObjectName(name: String, typeRep: TypeRep.Rep): Unit = {
    // TODO: Include in the message the position of the error in the source text.
    if (typeMap.contains(name))
      throw new ConflictingNameException(s"$name already names a type")

    // TODO: Include in the message the position of the error in the source text.
    if (objectMap.contains(name))
      throw new DuplicateObjectNameException(s"$name already names an object")

    objectMap = objectMap + (name -> typeRep)
  }

  def addTypeName(name: String, typeRep: TypeRep.Rep): Unit = {
    // TODO: Include in the message the position of the error in the source text.
    if (objectMap.contains(name))
      throw new ConflictingNameException(s"$name already names an object")

    // TODO: Include in the message the position of the error in the source text.
    if (typeMap.contains(name))
      throw new DuplicateTypeNameException(s"$name already names a type")

    typeMap = typeMap + (name -> typeRep)
  }

  def getObjectNames: Iterable[String] = {
    objectMap.keys
  }

  def getObjectType(name: String): TypeRep.Rep = {
    // TODO: Include in the message the position of the error in the source text.
    if (!objectMap.contains(name))
      throw new UnknownObjectNameException(s"$name is not the name of an object")

    objectMap(name)
  }

  def getTypeRepresentation(name: String): TypeRep.Rep = {
    // TODO: Include in the message the position of the error in the source text.
    if (!typeMap.contains(name))
      throw new UnknownTypeNameException(s"$name is not the name of a type")

    typeMap(name)
  }

}
