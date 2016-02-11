package edu.vtc.daja.lev0;

import java.util.Iterator;

/**
 * An interface describing Daja symbol tables. Symbols are divided into object names and type
 * names. Note that symbol table objects are mutable. The built-in types int and bool are known
 * to all symbol tables after they are constructed.
 */
public interface SymbolTable {

     /**
     * Adds the name of an object along with its type to this symbol table. The type name must
     * already be known to the symbol table. Duplicate object names, or object names that
     * conflict with existing type names are not allowed. A SymbolTableException is thrown if
     * these rules are violated.
     *
     * @param name The name of the object to add.
     * @param typeName The name of its type.
     */
    void addObjectName(String name, String typeName);

    /**
     * Adds the name of a type along with information about its representation to this symbol
     * table. Duplicate type names, or type names that conflict with existing object names are
     * not allowed. A SymbolTableException is throw if these rules are violated.
     *
     * @param name The name of the type to add.
     * @param representation The representation of the type.
     */
    void addTypeName(String name, DajaTypes.TypeRep representation);

    /**
     * @return An iterator that allows visitation of all the objects in this symbol table.
     */
    Iterator<String> getObjectNames();

    /**
     * Looks up the type associated with a given object name. Throws an exception if the object
     * name is not known.
     *
     * @param name The name object to look up.
     * @return The name of the type associated with the named object.
     */
    String getObjectType(String name);

    /**
     * Looks up the representation of a type given its name. Throws an exception if the type
     * name is not known.
     *
     * @param name The name of the type to look up.
     * @return The representation of the named type.
     */
    DajaTypes.TypeRep getTypeRepresentation(String name);


    static class SymbolTableException extends Exception {
        public SymbolTableException(String message) { super(message); }
    }

    static class UnknownObjectNameException extends SymbolTableException {
        public UnknownObjectNameException(String message) { super(message); }
    }

    static class UnknownTypeNameException extends SymbolTableException {
        public UnknownTypeNameException(String message) { super(message); }
    }

    static class DuplicateObjectNameException extends SymbolTableException {
        public DuplicateObjectNameException(String message) { super(message); }
    }

    static class DuplicateTypeNameException extends SymbolTableException {
        public DuplicateTypeNameException(String message) { super(message); }
    }

    static class ConflictingNameException extends SymbolTableException {
        public ConflictingNameException(String message) { super(message); }
    }
}
