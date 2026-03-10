/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

parser grammar PiescriptAntlrParser;

options { tokenVocab = PiescriptLexer; }

// ──── Program ────
program
    : topBinding* expr EOF
    ;

topBinding
    : LET IDENTIFIER (COLON type)? ASSIGN expr SEMICOLON
    ;

// ──── Expressions ────
expr
    : LET IDENTIFIER (COLON type)? ASSIGN expr IN expr   # LetExpr
    | FN param+ ARROW expr                               # LambdaExpr
    | pipeExpr                                           # ExprPipe
    ;

pipeExpr
    : orExpr                                             # PipePassthrough
    | pipeExpr PIPE_OP orExpr                            # PipeOp
    ;

orExpr
    : andExpr                                            # OrPassthrough
    | orExpr OR_OP andExpr                               # OrOp
    ;

andExpr
    : eqExpr                                             # AndPassthrough
    | andExpr AND_OP eqExpr                              # AndOp
    ;

eqExpr
    : cmpExpr op=(EQ | NEQ) cmpExpr                      # EqualityOp
    | cmpExpr                                            # EqPassthrough
    ;

cmpExpr
    : addExpr op=(LTE | GTE | LT | GT) addExpr           # ComparisonOp
    | addExpr                                            # CmpPassthrough
    ;

addExpr
    : mulExpr                                            # AddPassthrough
    | addExpr op=(PLUS | MINUS) mulExpr                  # AdditiveOp
    ;

mulExpr
    : unaryExpr                                          # MulPassthrough
    | mulExpr op=(ASTERISK | SLASH | PERCENT) unaryExpr  # MultiplicativeOp
    ;

unaryExpr
    : op=(BANG | MINUS) unaryExpr                        # UnaryOp
    | appExpr                                            # UnaryPassthrough
    ;

appExpr
    : primary                                            # AppPassthrough
    | appExpr primary                                    # Application
    ;

// ──── Primary expressions ────
primary
    : DOT IDENTIFIER                                     # Accessor
    | INTEGER_LITERAL                                    # IntegerLiteral
    | DECIMAL_LITERAL                                    # DecimalLiteral
    | QUOTED_STRING                                      # StringLiteral
    | TRUE                                               # TrueLiteral
    | FALSE                                              # FalseLiteral
    | NULL                                               # NullLiteral
    | IDENTIFIER                                         # Variable
    | LPAREN expr COLON type RPAREN                      # Ascription
    | LPAREN expr RPAREN                                 # ParenExpr
    | LBRACE RBRACE                                      # EmptyRecord
    | LBRACE recordField (COMMA recordField)* RBRACE     # RecordLiteral
    | LBRACE expr BAR recordUpdate (COMMA recordUpdate)* RBRACE  # RecordUpdateExpr
    | LBRACE UNDERSCORE BAR recordUpdate (COMMA recordUpdate)* RBRACE  # UpdateSugar
    | IF expr THEN expr ELSE expr                        # IfExpr
    | block                                              # BlockExpr
    | primary DOT IDENTIFIER                             # Projection
    ;

recordField
    : IDENTIFIER COLON expr
    ;

recordUpdate
    : IDENTIFIER ASSIGN expr
    ;

// ──── Blocks ────
block
    : LBRACE blockStmt+ expr RBRACE
    ;

blockStmt
    : LET IDENTIFIER (COLON type)? ASSIGN expr SEMICOLON  # BlockLet
    | expr SEMICOLON                                       # BlockExprStmt
    ;

// ──── Lambda parameters ────
param
    : IDENTIFIER                                          # UntypedParam
    | LPAREN IDENTIFIER COLON type RPAREN                 # TypedParam
    ;

// ──── Types ────
type
    : typePrimary ARROW type                              # FunctionType
    | typePrimary                                         # TypeAtom
    ;

typePrimary
    : IDENTIFIER                                          # TypeCon
    | LBRACE rowType RBRACE                               # RecordType
    | LPAREN type RPAREN                                  # ParenType
    ;

rowType
    : rowField (COMMA rowField)* (BAR IDENTIFIER)?
    ;

rowField
    : IDENTIFIER COLON type
    ;
