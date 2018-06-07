// Define a grammar called Hello
grammar Language;

program     : statement*
            ;

statement   : declaration_stat ';'  #declarationStat
            | assign_stat ';'       #assignStat
            | if_stat               #ifStat
            | while_stat            #whileStat
            | block_stat            #blockStat
            | return_stat ';'       #returnStat
            | function_definition   #functionDefStat
            | contract_stat         #contractStat
            ;

block_stat          : '{' statement* '}'
                    ;

declaration_stat    : type ID
                    ;

assign_stat         : type? ID ':=' expression
                    ;

if_stat             : 'if' '(' expression ')' statement ('else' statement)?
                    ;

while_stat          : 'while' '(' expression ')' block_stat
                    ;

return_stat         : 'return' expression
                    ;

contract_stat       : 'CONTRACTS_ARE_NOT_IMPLEMENTED_YET'
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
            | function_call                                     #functionCallExpr
            ;

type        : 'int'         #intType
            | 'boolean'     #booleanType
            ;

return_type : 'void'    #voidReturnType
            | type      #normalReturnType
            ;

function_definition : return_type ID '(' (type ID)? (',' type ID)* ')' block_stat
                    ;

function_call       : ID '(' expression? (',' expression)* ')'
                    ;

BOOL    : 'true'
        | 'false'
        ;

ID      : [a-z]+ ;                              // match lower-case identifiers
NUM     : [0-9]+ ;                              // match lower-case identifiers
WS      : [ \t\r\n]+ -> skip ;                  // skip spaces, tabs, newlines
COMMENT : '//' .*? ('\n' | EOF)    -> skip;     // skip single line comment

