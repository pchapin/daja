package org.pchapin.daja.logic;

import org.antlr.v4.runtime.*;

import java.util.List;

public class Logic {

    private static List<Token> tokenList;

    private static void consumeToken()
    {
        int type = tokenList.get(0).getType();
        // System.out.println("Consuming token: " + type);
        tokenList.remove(0);
    }


    public static void main(String[] args)
            throws java.io.IOException
    {
        SymbolTable table = new SymbolTable();

        ANTLRFileStream input = new ANTLRFileStream(args[0]);
        LogicLexer lexer = new LogicLexer(input);
        CommonTokenStream tokens  = new CommonTokenStream(lexer);

        tokens.fill();
        tokenList = tokens.getTokens();

        //for (Token token : tokenList) {
        //    System.out.println("[" +
        //            token.getLine() + ", " +
        //            (token.getCharPositionInLine() + 1) + "] " + token.getText());
        //}

        statement_list(table);
    }


    private static void statement_list(SymbolTable table)
    {
        // TODO: Check explicitly for the <EOF> token.
        if (tokenList.size() == 1) return;
        statement(table);
        statement_list(table);
    }


    private static void statement(SymbolTable table)
    {
        full_expr(table);
        if (tokenList.size() < 1) {
            // Error! Unexpected end of file.
        }
        else if (tokenList.get(0).getType() != LogicLexer.SEMI) {
            // Error! Missing semicolon.
            // Print message pointing at unexpected token.
            // Conceptually insert the semicolon in the stream for error recovery.
        }
        else {
            // Remove the semicolon (because it is what we expected to see)
            consumeToken();
        }
    }


    private static Node full_expr(SymbolTable table)
    {
        // full_expr -> disjunct_expr full_helper2

        disjunct_expr(table);
        full_helper2(table);
        return null;
    }


    private static void full_helper2(SymbolTable table)
    {
        //full_helper2 -> full_helper1 full_helper2
        //             -> e

        int lookAheadToken = tokenList.get(0).getType();
        if (lookAheadToken == LogicLexer.COND || lookAheadToken == LogicLexer.BICOND) {
            full_helper1(table);
            full_helper2(table);
        }
    }


    private static void full_helper1(SymbolTable table)
    {
        //full_helper1 -> '-->' disjunct_expr
        //             -> '<-->' disjunct_expr

        switch (tokenList.get(0).getType()) {
            case LogicLexer.COND:
                consumeToken();
                disjunct_expr(table);
                break;

            case LogicLexer.BICOND:
                consumeToken();
                disjunct_expr(table);
                break;

            default:
                // Error! Unexpected token!
                break;
        }
    }


    private static void disjunct_expr(SymbolTable table)
    {
        conjunct_expr(table);
        disjunct_helper(table);
    }


    private static void disjunct_helper(SymbolTable table)
    {
        Token headToken = tokenList.get(0);
        if (headToken.getType() == LogicLexer.OR) {
            consumeToken();
            conjunct_expr(table);
            disjunct_helper(table);
        }
    }


    private static void conjunct_expr(SymbolTable table)
    {
        simple_expr(table);
        conjunct_helper(table);
    }


    private static void conjunct_helper(SymbolTable table)
    {
        Token headToken = tokenList.get(0);
        if (headToken.getType() == LogicLexer.AND) {
            consumeToken();
            simple_expr(table);
            conjunct_helper(table);
        }
    }


    private static Node simple_expr(SymbolTable table)
    {
        //simple_expr  -> IDENTIFIER
        //             -> 'true'
        //             -> 'false'
        //             -> 'not' simple_expr
        //             -> '(' full_expr ')'

        switch (tokenList.get(0).getType()) {
            case LogicLexer.IDENTIFIER:
                consumeToken();
                table.add(tokenList.get(0).getText());
                return new Node.Identifier(tokenList.get(0).getText());

            case LogicLexer.TRUE:
                consumeToken();
                return new Node.Literal(true);

            case LogicLexer.FALSE:
                consumeToken();
                return new Node.Literal(false);

            case LogicLexer.NOT:
                consumeToken();
                return new Node.Not(simple_expr(table));

            case LogicLexer.LPARENS:
                consumeToken();
                Node subtree = full_expr(table);
                if (tokenList.get(0).getType() != LogicLexer.RPARENS) {
                    // Error! Expected ')'
                }
                else {
                    consumeToken();
                }
                return subtree;

            default:
                // Error! Unexpected token!
                // TODO: Decide on some suitable error recovery plan.
                return null;
        }
    }
}
