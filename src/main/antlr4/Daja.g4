grammar Daja;

@parser::header {
}

@lexer::header {
}


/* ======= */
/* Grammar */
/* ======= */

module
    : VOID IDENTIFIER LPARENS RPARENS block_statement;

// Declaration grammar...
// ----------------------

// For now, we support only single dimensional arrays.
declaration
    : basic_type (LBRACKET expression RBRACKET)?
        init_declarator (COMMA init_declarator)* SEMI;

basic_type
    : BOOL
    | INT
    | UINT
    | LONG
    | ULONG
    | FLOAT
    | DOUBLE
    | REAL;

init_declarator
    : IDENTIFIER (EQUALS expression)?;

// Statement grammar...
// --------------------

statement
    : expression_statement
    | block_statement
    | if_statement
    | while_statement;

expression_statement
    : expression SEMI;

block_statement
    : LBRACE (declaration|statement)* RBRACE;

if_statement
    : IF LPARENS expression RPARENS block_statement
    | IF LPARENS expression RPARENS block_statement ELSE block_statement;

while_statement
    : WHILE LPARENS expression RPARENS block_statement;

// Expression grammar...
// ---------------------

expression
    : comma_expression;

comma_expression
    : assignment_expression
    | assignment_expression COMMA comma_expression;

assignment_expression
    : relational_expression
    | relational_expression EQUALS assignment_expression;

relational_expression
    : add_expression
    | relational_expression (EQEQ | NOTEQ | LESS | LESSEQ | GREATER | GREATEREQ) add_expression;

add_expression
    : multiply_expression
    | add_expression (PLUS | MINUS) multiply_expression;

multiply_expression
    : postfix_expression
    | multiply_expression (MULTIPLY | DIVIDE) postfix_expression;

postfix_expression
    : primary_expression
    | postfix_expression LBRACKET expression RBRACKET;

primary_expression
    : IDENTIFIER
    | INTEGER_LITERAL
    | TRUE
    | FALSE
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
CENT         : 'cent';
CHAR         : 'char';
CLASS        : 'class';
CONST        : 'const';
CONTINUE     : 'continue';
DCHAR        : 'dchar';
DEBUG        : 'debug';
DEFAULT      : 'default';
DELEGATE     : 'delegate';
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
IF           : 'if';
IMMUTABLE    : 'immutable';
IMPORT       : 'import';
IN           : 'in';
INOUT        : 'inout';
INT          : 'int';
INTERFACE    : 'interface';
INVARIANT    : 'invariant';
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
WCHAR        : 'wchar';
WHILE        : 'while';
WITH         : 'with';

// What about the keywords involving the '_' symbol?
// Should they be handled here too or treated as special identifiers in the symbol table?

// Various punctuation and operator symbols.
// The order here follows that in the D language specification, section 2.7.
LBRACE   : '{';
RBRACE   : '}';
DIVIDE   : '/';
MINUS    : '-';
PLUS     : '+';
LESS     : '<';
LESSEQ   : '<=';
GREATER  : '>';
GREATEREQ: '>=';
NOTEQ    : '!=';
LPARENS  : '(';
RPARENS  : ')';
LBRACKET : '[';
RBRACKET : ']';
COMMA    : ',';
SEMI     : ';';
EQUALS   : '=';
EQEQ     : '==';
MULTIPLY : '*';

IDENTIFIER
    :   [a-zA-Z_][a-zA-Z0-9_]*;

WHITESPACE
    :   [ \t\f\r\n]+  -> skip;

COMMENT1
    :    '/*' .*? '*/' -> skip;

COMMENT2
    :   '//' .*? [\r\n] -> skip;

// This isn't quite right because it won't handle enclosed \" escaped double quotes.
STRING_LITERAL
    :    '"' .*? '"';

CHARACTER_LITERAL
    :    '\'' .*? '\'';

INTEGER_LITERAL
    :    DEC_NUMBER
    |    BIN_NUMBER
    |    HEX_NUMBER;

fragment DEC_NUMBER
    :    [0-9_]+ ( INT_NUMBER_SUFFIX )?;

fragment BIN_NUMBER
    :    ('0b' | '0B') [0-1_]+ ( INT_NUMBER_SUFFIX )?;

fragment HEX_NUMBER
    :    ('0x' | '0X') [0-9a-fA-F_]+ ( INT_NUMBER_SUFFIX )?;

// Lower case 'l' is not actually allowed.
// We will rule this out later when we can produce a nice error message.
fragment INT_NUMBER_SUFFIX
    :    ('l' | 'L' | 'u' | 'U' | 'lu' | 'lU' | 'Lu' | 'LU' | 'ul' | 'uL' | 'Ul' | 'UL');

FLOAT_LITERAL
    :    DEC_FLOAT;

fragment DEC_FLOAT
    :    [0-9_]+ '.' [0-9_]+ ( FLT_EXPONENT )? ( FLT_NUMBER_SUFFIX )?;

fragment FLT_EXPONENT
    :    ( 'e' | 'E' ) ( '+' | '-' )? [0-9]+;

// Lower case 'l' is not actually allowed.
// We will rule this out later when we can produce a nice error message.
fragment FLT_NUMBER_SUFFIX
    :    ('f' | 'F' | 'l' | 'L');
