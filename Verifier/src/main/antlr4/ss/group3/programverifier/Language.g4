// Define a grammar called Hello
grammar Language;

program     : statement*
            ;

statement   : type ID ';'                                                   #declarationStat
            | type? ID ':=' expression ';'                                  #assignStat
            | 'if' '(' expression ')' statement ('else' statement)?         #ifStat
            | 'while' '(' expression ')' statement                          #whileStat
            | 'return' expression ';'                                       #returnStat
            | return_type ID '(' (type ID)? (',' type ID)* ')' statement    #functionDefStat
            | '{' statement* '}'                                            #blockStat
            | '#' contract                                                  #contractStat
            ;

contract    : 'requires' expression
            | 'ensures' expression
            | 'loopinvariant' expression
            ;

type        : 'int'         #intType
            | 'boolean'     #booleanType
            ;

return_type : 'void'    #voidReturnType
            | type      #normalReturnType
            ;

expression  : '(' expression ')'                                #parExpr
            | NUM                                               #numExpr
            | BOOL                                              #boolExpr
            | ID                                                #idExpr
            | '-' expression                                    #unaryMinusExpr
            | '!' expression                                    #notExpr
            | expression ('*' | '/') expression                 #timesOrDevideExpr
            | expression ('+' | '-') expression                 #plusOrMinusExpr
            | expression ('<' | '>' | '<=' | '>=') expression   #compareExpr
            | expression ('==' | '!=') expression               #equalOrNotEqualExpr
            |  ID '(' expression? (',' expression)* ')'         #functionCallExpr
            ;

BOOL    : 'true'
        | 'false'
        ;

ID      : [a-z]+ ;                              // match lower-case identifiers
NUM     : [0-9]+ ;                              // match lower-case identifiers
WS      : [ \t\r\n]+ -> skip ;                  // skip spaces, tabs, newlines
COMMENT : '//' .*? ('\n' | EOF)    -> skip;     // skip single line comment

