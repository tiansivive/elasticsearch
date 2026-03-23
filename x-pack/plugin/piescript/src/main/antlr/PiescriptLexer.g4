/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

lexer grammar PiescriptLexer;

// ──── Keywords ────
LET       : 'let';
IN        : 'in';
FN        : 'fn';
IF        : 'if';
THEN      : 'then';
ELSE      : 'else';
TRUE      : 'true';
FALSE     : 'false';
NULL      : 'null';
MATCH     : 'match';
QUERY     : 'query';
SPAWN_BANG: 'spawn!';
SPAWN     : 'spawn';
SEND      : 'send';
WHEN      : 'when';
USE       : 'use';
AS        : 'as';
PAR       : 'par';
DO        : 'do';
UNDERSCORE: '_';

// ──── Multi-character operators ────
PIPE_OP   : '|>';
OR_OP     : '||';
AND_OP    : '&&';
ARROW     : '->';
EQ        : '==';
NEQ       : '!=';
LTE       : '<=';
GTE       : '>=';

// ──── Single-character operators ────
LT        : '<';
GT        : '>';
PLUS      : '+';
MINUS     : '-';
ASTERISK  : '*';
SLASH     : '/';
PERCENT   : '%';
BANG      : '!';
DOT       : '.';
COMMA     : ',';
COLON     : ':';
SEMICOLON : ';';
ASSIGN    : '=';
AMP       : '&';
BAR       : '|';

// ──── Brackets ────
LPAREN    : '(';
RPAREN    : ')';
LBRACE    : '{';
RBRACE    : '}';
LBRACKET  : '[';
RBRACKET  : ']';

// ──── Literals ────
INTEGER_LITERAL
    : DIGIT+
    ;

DECIMAL_LITERAL
    : DIGIT+ '.' DIGIT+
    | '.' DIGIT+
    | DIGIT+ ('.' DIGIT+)? EXPONENT
    | '.' DIGIT+ EXPONENT
    ;

QUOTED_STRING
    : '"' (ESCAPE_SEQUENCE | ~["\\\r\n])* '"'
    ;

// ──── Identifiers (D-033: lexer split for type variable convention) ────
UPPER_IDENT
    : [A-Z] (LETTER | DIGIT | '_')*
    ;

LOWER_IDENT
    : [a-z] (LETTER | DIGIT | '_')*
    | '_' (LETTER | DIGIT | '_')+
    ;

// ──── Comments ────
LINE_COMMENT
    : '//' ~[\r\n]* '\r'? '\n'? -> skip
    ;

MULTILINE_COMMENT
    : '/*' .*? '*/' -> skip
    ;

// ──── Whitespace ────
WS
    : [ \t\r\n]+ -> skip
    ;

// ──── Fragments ────
fragment DIGIT
    : [0-9]
    ;

fragment LETTER
    : [a-zA-Z]
    ;

fragment ESCAPE_SEQUENCE
    : '\\' ["\\nrt]
    ;

fragment EXPONENT
    : [eE] [+-]? DIGIT+
    ;

