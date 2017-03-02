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

    public static class ConjunctExpr extends Node {
        private Node simple, helper;

        public ConjunctExpr(Node simple, Node helper)
        {
            this.simple = simple;
            this.helper = helper;
        }

        @Override
        public boolean evaluate(SymbolTable table)
        {
            if (helper != null) {
                return simple.evaluate(table) && helper.evaluate(table);
            }
            else {
                return simple.evaluate(table);
            }
        }
    }

    public static class ConjunctHelper extends Node {
        private Node simple, helper;

        public ConjunctHelper(Node simple, Node helper)
        {
            this.simple = simple;
            this.helper = helper;
        }

        @Override
        public boolean evaluate(SymbolTable table)
        {
            boolean result;
            if (helper != null) {
                result = simple.evaluate(table) && helper.evaluate(table);
            }
            else {
                result = simple.evaluate(table);
            }
            return result;
        }
    }

}
