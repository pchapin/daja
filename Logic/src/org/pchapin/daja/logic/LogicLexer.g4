lexer grammar LogicLexer;

@lexer::header {
    package org.pchapin.daja.logic;
}


/* =========== */
/* Lexer rules */
/* =========== */

// --------------
// Reserved Words
// --------------

// None.

// Various punctuation and operator symbols.
AND      : 'and';
OR       : 'or';
NOT      : 'not';
COND     : '-->';
BICOND   : '<-->';
LPARENS  : '(';
RPARENS  : ')';
SEMI     : ';';
TRUE     : 'true';
FALSE    : 'false';

IDENTIFIER
    :   [a-zA-Z_][a-zA-Z0-9_]*;

WHITESPACE
    :   [ \t\f\r\n]+  -> skip;

COMMENT1
    :    '/*' .*? '*/' -> skip;

COMMENT2
    :   '//' .*? [\r\n] -> skip;
