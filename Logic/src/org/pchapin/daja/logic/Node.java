package org.pchapin.daja.logic;

public abstract class Node {

    abstract boolean evaluate(SymbolTable table);

    public static class Literal extends Node {
        private boolean value;

        public Literal(boolean value) {
            this.value = value;
        }

        @Override
        public boolean evaluate(SymbolTable table)
        {
            return value;
        }
    }


    public static class Identifier extends Node {
        String name;

        public Identifier(String name)
        {
            this.name = name;
        }

        @Override
        public boolean evaluate(SymbolTable table)
        {
            return table.getValue(name);
        }
    }

    public static class Not extends Node {
        private Node n;

        public Not(Node n)
        {
            this.n = n;
        }

        @Override
        public boolean evaluate(SymbolTable table)
        {
            return !n.evaluate(table);
        }
    }

    public static class And extends Node {
        private Node left, right;

        public And(Node left, Node right)
        {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean evaluate(SymbolTable table)
        {
            return left.evaluate(table) && right.evaluate(table);
        }
    }

}
