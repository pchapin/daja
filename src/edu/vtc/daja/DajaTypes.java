package edu.vtc.daja;

import java.util.List;

public class DajaTypes {

    static abstract class TypeRep { }

    static class IntegerRepClass extends TypeRep {
        @Override
        public String toString()
        {
            return "int";
        }
    }
    static class BooleanRepClass extends TypeRep {
        @Override
        public String toString()
        {
            return "bool";
        }
    }

    public static IntegerRepClass IntegerRep = new IntegerRepClass();
    public static BooleanRepClass BooleanRep = new BooleanRepClass();

    static class ArrayTypeRep extends TypeRep {
        public int size;
        public TypeRep elementType;

        public ArrayTypeRep(int size, TypeRep elementType)
        {
            this.size = size;
            this.elementType = elementType;
        }

        @Override
        public String toString()
        {
            return elementType.toString() + "[" + size + "]";
        }
    }

    static class StructureTypeRep extends TypeRep {
        public List<TypeRep> members;

        public StructureTypeRep(List<TypeRep> members)
        {
            this.members = members;
        }
    }
}
