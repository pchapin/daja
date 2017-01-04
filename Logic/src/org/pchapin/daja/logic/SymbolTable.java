package org.pchapin.daja.logic;

import java.util.Map;
import java.util.TreeMap;

public class SymbolTable {

    private Map<String, Boolean> table = new TreeMap<>();

    public void add(String name)
    {
        table.put(name, false);
    }

    public boolean getValue(String name)
    {
        return table.get(name);
    }

}
