// Define a grammar called Hello
grammar Language;

program: statement*;

statement: (assign | if_stat) ';';

assign: ID ':=' expression;

if_stat: 'if' '(' expression ')' statement;

expression: NUM;

ID : [a-z]+ ;             // match lower-case identifiers
NUM : [0-9]+ ;             // match lower-case identifiers
WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
