/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

parser grammar PiescriptAntlrParser;

options { tokenVocab = PiescriptLexer; }

// ──── Identifier helper (D-033) ────
ident : UPPER_IDENT | LOWER_IDENT ;

// ──── Program ────
program
    : topBinding* expr SEMICOLON? EOF
    ;

topBinding
    : LET ident (COLON type)? ASSIGN expr SEMICOLON    # TopLet
    | USE QUOTED_STRING AS LOWER_IDENT SEMICOLON        # TopUse
    ;

// ──── Expressions ────
expr
    : LET ident (COLON type)? ASSIGN expr IN expr        # LetExpr
    | FN param+ ARROW expr                               # LambdaExpr
    | SPAWN_BANG                                          # SpawnBangExpr
    | SPAWN expr                                         # SpawnExpr
    | SEND primary expr                                  # SendExpr
    | WHEN whenBinding (AMP whenBinding)* ARROW expr     # WhenExpr
    | MATCH expr alternative+                            # MatchExpr
    | pipeExpr                                           # ExprPipe
    ;

// ──── Match alternatives and patterns ────
alternative
    : BAR pattern ARROW expr
    ;

pattern
    : UNDERSCORE                                      # WildcardPattern
    | literal                                         # LitPattern
    | LOWER_IDENT                                     # VarPattern
    | LBRACE RBRACE                                   # EmptyRecordPattern
    | LBRACE recordPatField (COMMA recordPatField)* (BAR LOWER_IDENT)? RBRACE  # RecordPattern
    | LBRACKET RBRACKET                               # EmptyListPattern
    | LBRACKET pattern (COMMA pattern)* RBRACKET      # ExactListPattern
    | LBRACKET pattern BAR pattern RBRACKET           # ConsListPattern
    ;

recordPatField
    : ident COLON pattern                             // field with sub-pattern
    | LOWER_IDENT                                     // shorthand: `{ name }` = `{ name: name }`
    ;

// The channel position accepts a full expr, but in practice channels are always
// variables (from `let ch = spawn ...`). If parsing ambiguities arise, consider
// restricting to `ident ident` — which matches the Join Calculus more closely.
whenBinding
    : LPAREN expr ident RPAREN
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

// ──── Literals ────
literal
    : INTEGER_LITERAL                                    # IntegerLiteral
    | DECIMAL_LITERAL                                    # DecimalLiteral
    | QUOTED_STRING                                      # StringLiteral
    | TRUE                                               # TrueLiteral
    | FALSE                                              # FalseLiteral
    | NULL                                               # NullLiteral
    ;

// ──── Primary expressions ────
primary
    : DOT ident                                          # Accessor
    | literal                                            # LiteralExpr
    | ident                                              # Variable
    | LPAREN expr COLON type RPAREN                      # Ascription
    | LPAREN expr RPAREN                                 # ParenExpr
    | LBRACE RBRACE                                      # EmptyRecord
    | LBRACE recordField (COMMA recordField)* RBRACE     # RecordLiteral
    | LBRACE expr BAR recordUpdate (COMMA recordUpdate)* RBRACE  # RecordUpdateExpr
    | LBRACE UNDERSCORE BAR recordUpdate (COMMA recordUpdate)* RBRACE  # UpdateSugar
    | LBRACKET RBRACKET                                   # EmptyList
    | LBRACKET expr (COMMA expr)* RBRACKET               # ListLiteral
    | IF expr THEN expr ELSE expr                        # IfExpr
    | QUERY expr SEMICOLON                                # QueryExpr
    | block                                              # BlockExpr
    | primary DOT ident                                  # Projection
    ;

recordField
    : ident COLON expr
    ;

recordUpdate
    : ident ASSIGN expr
    ;

// ──── Blocks ────
block
    : LBRACE blockStmt+ expr RBRACE
    ;

blockStmt
    : LET ident (COLON type)? ASSIGN expr SEMICOLON       # BlockLet
    | expr SEMICOLON                                       # BlockExprStmt
    ;

// ──── Lambda parameters ────
param
    : ident                                               # UntypedParam
    | LPAREN ident COLON type RPAREN                      # TypedParam
    ;

// ──── Types ────
type
    : typeApp ARROW type                                  # FunctionType
    | typeApp                                             # TypeNonArrow
    ;

typeApp
    : typeApp typeAtom                                    # TypeApplication
    | typeAtom                                            # TypeAppPassthrough
    ;

typeAtom
    : UPPER_IDENT                                         # TypeCon
    | LOWER_IDENT                                         # TypeVar
    | LBRACE rowType RBRACE                               # RecordType
    | LPAREN type RPAREN                                  # ParenType
    ;

rowType
    : rowField (COMMA rowField)* (BAR LOWER_IDENT)?
    ;

rowField
    : ident COLON type
    ;
