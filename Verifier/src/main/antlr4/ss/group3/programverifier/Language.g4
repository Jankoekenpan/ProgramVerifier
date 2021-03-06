// Define a grammar called Hello
grammar Language;

program     : statement*
            ;

statement   : type ID (':=' expression)? ';'                                            #declarationStat
            | ID ':=' expression ';'                                                    #assignStat
            | 'if' '(' expression ')' statement ('else' statement)?                     #ifStat
            | 'while' '(' expression ')' contract* statement                            #whileStat
            | 'return' expression? ';'                                                  #returnStat
            | return_type ID '(' parameter? (',' parameter)* ')' contract* statement    #functionDefStat
            | contract                                                                  #contractStat
            | '{' statement* '}'                                                        #blockStat
            ;

contract    : '#' contract_type expression '#'
            ;

contract_type   : 'requires'
                | 'ensures'
                | 'invariant'
                | 'decreases'
                | 'assert'
                ;

type        : 'int'         #intType        //TODO decide on whether this is a bounded bitvector or a true mathematical integer (I prefer the latter)
            | 'boolean'     #booleanType
            ;

return_type : 'void'        #voidReturnType
            | type          #normalReturnType
            ;

parameter: type ID;

expression  : '(' expression ')'                                        #parExpr
            | NUM                                                       #numExpr
            | BOOL                                                      #boolExpr
            | ID                                                        #idExpr
            | '-' expression                                            #unaryMinusExpr
            | '!' expression                                            #notExpr
            | expression ('*' | '/') expression                         #timesOrDivideExpr
            | expression ('+' | '-') expression                         #plusOrMinusExpr
            | expression ('<' | '>' | '<=' | '>=') expression           #compareExpr
            | expression ('==' | '!=') expression                       #equalOrNotEqualExpr
            | expression ('&&' | '||' | '=>') expression                #logicBinOpExpr
            | <assoc=right> expression '?' expression ':' expression    #ternaryIfExpr
            | ID '(' expression? (',' expression)* ')'                  #functionCallExpr
            | contract_expression                                       #contractExpr    //can only occur in contracts (a compiler/typechecker would check this)
            ;

contract_expression : '\\result'                     #resultContrExpr        //denotes the result of a function
                    | '\\old' '(' ID ')'             #oldContrExpr           //in an ensures clause, the result of this expression is the initial value (old value) of the expression instead of the new value.
                    ;

BOOL    : 'true'
        | 'false'
        ;

ID                      : [a-z]+ ;                              // match lower-case identifiers
NUM                     : [0-9]+ ;                              // match lower-case identifiers
WS                      : [ \t\r\n]+                -> skip;    // skip spaces, tabs, newlines
SINGLE_LINE_COMMENT     : '//' .*? ('\n' | EOF)     -> skip;    // skip single line comment
MULTI_LINE_COMMENT      : '/*' .*? '*/'             -> skip;    // skip multi line comment
