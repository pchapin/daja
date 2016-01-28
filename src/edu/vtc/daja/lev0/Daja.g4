grammar Daja;

@parser::header {
    package edu.vtc.daja.lev0;
}

@lexer::header {
    package edu.vtc.daja.lev0;
}


/* ======= */
/* Grammar */
/* ======= */

module
    : VOID IDENTIFIER LPARENS RPARENS block;

block
    : LBRACE statement* RBRACE;

// Statement grammar...
// --------------------

statement
    : expression_statement;

expression_statement
    : expression SEMI;

// Expression grammar...
// ---------------------

expression
    : assignment_expression;

assignment_expression
    : add_expression
    | add_expression EQUALS assignment_expression;

add_expression
    : multiply_expression
    | add_expression (PLUS | MINUS) multiply_expression;

multiply_expression
    : primary_expression
    | multiply_expression (MULTIPLY | DIVIDE) primary_expression;

primary_expression
    : IDENTIFIER
    | NUMERIC_LITERAL
    | LPARENS expression RPARENS;

/* =========== */
/* Lexer rules */
/* =========== */

// --------------
// Reserved Words
// --------------

// Daja reserves all the keywords of D to prevent programmers from accidentally creating
// identifiers using those keywords. This avoids incompatibilities with D; it doesn't matter
// if Daja doesn't yet implement all of the relevant features.

ABSTRACT     : 'abstract';
ALIAS        : 'alias';
ALIGN        : 'align';
ASM          : 'asm';
ASSERT       : 'assert';
AUTO         : 'auto';
BODY         : 'body';
BOOL         : 'bool';
BREAK        : 'break';
BYTE         : 'byte';
CASE         : 'case';
CAST         : 'cast';
CATCH        : 'catch';
CDOUBLE      : 'cdouble';
CENT         : 'cent';
CFLOAT       : 'cfloat';
CHAR         : 'char';
CLASS        : 'class';
CONST        : 'const';
CONTINUE     : 'continue';
CREAL        : 'creal';
DCHAR        : 'dchar';
DEBUG        : 'debug';
DEFAULT      : 'default';
DELEGATE     : 'delegate';
DELETE       : 'delete';
DEPRECATED   : 'deprecated';
DO           : 'do';
DOUBLE       : 'double';
ELSE         : 'else';
ENUM         : 'enum';
EXPORT       : 'export';
EXTERN       : 'extern';
FALSE        : 'false';
FINAL        : 'final';
FINALLY      : 'finally';
FLOAT        : 'float';
FOR          : 'for';
FOREACH      : 'foreach';
FOREACH_REVERSE : 'foreach_reverse';
FUNCTION     : 'function';
GOTO         : 'goto';
IDOUBLE      : 'idouble';
IF           : 'if';
IFLOAT       : 'ifloat';
IMMUTABLE    : 'immutable';
IMPORT       : 'import';
IN           : 'in';
INOUT        : 'inout';
INT          : 'int';
INTERFACE    : 'interface';
INVARIANT    : 'invariant';
IREAL        : 'ireal';
IS           : 'is';
LAZY         : 'lazy';
LONG         : 'long';
MACRO        : 'macro';
MIXIN        : 'mixin';
MODULE       : 'module';
NEW          : 'new';
NOTHROW      : 'nothrow';
NULL         : 'null';
OUT          : 'out';
OVERRIDE     : 'override';
PACKAGE      : 'package';
PRAGMA       : 'pragma';
PRIVATE      : 'private';
PROTECTED    : 'protected';
PUBLIC       : 'public';
PURE         : 'pure';
REAL         : 'real';
REF          : 'ref';
RETURN       : 'return';
SCOPE        : 'scope';
SHARED       : 'shared';
SHORT        : 'short';
STATIC       : 'static';
STRUCT       : 'struct';
SUPER        : 'super';
SWITCH       : 'switch';
SYNCHRONIZED : 'synchronized';
TEMPLATE     : 'template';
THIS         : 'this';
THROW        : 'throw';
TRUE         : 'true';
TRY          : 'try';
TYPEDEF      : 'typedef';
TYPEID       : 'typeid';
TYPEOF       : 'typeof';
UBYTE        : 'ubyte';
UCENT        : 'ucent';
UINT         : 'uint';
ULONG        : 'ulong';
UNION        : 'union';
UNITTEST     : 'unittest';
USHORT       : 'ushort';
VERSION      : 'version';
VOID         : 'void';
VOLATILE     : 'volatile';
WCHAR        : 'wchar';
WHILE        : 'while';
WITH         : 'with';

// What about the keywords involving the '_' symbol?
// Should they be handled here too or treated as special identifiers in the symbol table?

// Various punctuation and operator symbols.
DIVIDE   : '/';
EQUALS   : '=';
LBRACE   : '{';
LPARENS  : '(';
MINUS    : '-';
MULTIPLY : '*';
PLUS     : '+';
RBRACE   : '}';
RPARENS  : ')';
SEMI     : ';';

IDENTIFIER
    :   [a-zA-Z_][a-zA-Z0-9_]*;

WHITESPACE
    :   [ \t\f\r\n]+  -> skip;

COMMENT1
    :    '/*' .*? '*/' -> skip;

COMMENT2
    :   '//' .*? [\r\n] -> skip;

STRING_LITERAL
    :    '"' .*? '"';

CHARACTER_LITERAL
    :    '\'' .*? '\'';

NUMERIC_LITERAL
    :    DEC_NUMBER
    |    HEX_NUMBER;

fragment DEC_NUMBER
    :    ( DIGIT )+ ( NUMBER_SUFFIX )?;

fragment HEX_NUMBER
    :    NUMBER_PREFIX ( HEX_DIGIT )+ ( NUMBER_SUFFIX )?;

fragment DIGIT
    :    [0-9];

fragment HEX_DIGIT
    :    [0-9a-fA-F];

fragment NUMBER_PREFIX
    :    '0x';

fragment NUMBER_SUFFIX
    :    ('L' | 'u' | 'U' | 'Lu' | 'uL' | 'LU' | 'UL');
