package ss.group3.programverifier;

import ss.group3.programverifier.ast.*;
import ss.group3.programverifier.smt.*;
import ss.group3.util.Pair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SMTGenerator {

    private static final String PATH_CONDITION_IDENTIFIER = "_PATH";

    private final Map<Integer, Map<String, String>> conditionsToVariablesToRenamedVariables = new HashMap<>();

    private int currentPathCondition;

    public SMTGenerator() {
    }

    private int nextPathCondition() {
        return ++currentPathCondition;
    }

    private String getSmtIdentifier(String identifier, int pathCondition) {
        return conditionsToVariablesToRenamedVariables
                .computeIfAbsent(pathCondition, s -> new HashMap<>())
                .computeIfAbsent(identifier, id -> toSMTIdentifier(id, pathCondition));
    }

    private String toSMTIdentifier(String identifier, int pathCondition) {
        return identifier + "_" + pathCondition;
    }

    private String getSmtIdentifier(String identifier) {
        return getSmtIdentifier(identifier, currentPathCondition);
    }

    public List<SmtStatement> toSMT(Statement statement, int pathCondition) {

        if (statement instanceof Declaration) {
            return toSMT((Declaration) statement, pathCondition);
        } else if (statement instanceof Assign) {
            return toSMT((Assign) statement, pathCondition);
        } else if (statement instanceof FunctionDef) {
            return toSMT((FunctionDef) statement, pathCondition);
        } else if (statement instanceof If) {
            return toSMT((If) statement, pathCondition);
        } else if (statement instanceof While) {
            return toSMT((While) statement, pathCondition);
        }

        else {
            return null;
        }
    }

    public List<SmtStatement> toSMT(Declaration declaration, int pathCondition) {
        List<SmtStatement> smtStatements = new ArrayList<>();
        Type type = declaration.getType();
        String identifier = declaration.getIdentifier();

        SmtFunDecl smtFunDecl = new SmtFunDecl(identifier, toSMTType(type));
        smtStatements.add(smtFunDecl);

        if (declaration.hasExpression()) {
            Expression expression = declaration.getExpression();
            SmtAssert smtAssert = new SmtAssert(toSMTExpression(expression));
            smtStatements.add(smtAssert);
        }

        return smtStatements;
    }

    public List<SmtStatement> toSMT(If ifStatement, final int pathCondition) {
        List<SmtStatement> smtStatements = new ArrayList<>();

        //TODO create new path conditions
        Expression condition = ifStatement.getCondition();
        SmtExpr smtCondition = toSMTExpression(condition);

        //TODO make a new identifier for the path condition :-)

        nextPathCondition();
        Declaration declaration = new Declaration(Type.BOOLEAN, toSMTIdentifier(PATH_CONDITION_IDENTIFIER, pathCondition), condition);
        smtStatements.addAll(toSMT(declaration, pathCondition));

        Statement thanBranch = ifStatement.getThenBranch();
        //TODO in the than branch every assignment should have the path condition and identifier built int

        if (ifStatement.hasElseBranch()) {
            //TODO
        }

        return smtStatements;
    }

    public List<SmtStatement> toSMT(Assign assign, final int pathCondition) {
        List<SmtStatement> smtStatements = new ArrayList<>();

        //TODO take path condition into account somehow


        return smtStatements;
    }

    public List<SmtStatement> toSMT(FunctionDef functionDef, final int pathCondition) {
        List<SmtStatement> smtStatements = new ArrayList<>();

        for (Pair<Type, String> pair : functionDef.getParameterPairs()) {
            Statement declaration = new Declaration(pair.getFirst(), pair.getSecond());
            smtStatements.addAll(toSMT(declaration, pathCondition));
        }

        smtStatements.addAll(toSMT(functionDef.getBody(), pathCondition));

        return smtStatements;
    }

    public List<SmtStatement> toSMT(While whileStatement, final int pathCondition) {
        List<SmtStatement> smtStatements = new ArrayList<>();

        //TODO while loop weirdness


        return smtStatements;
    }


    private SmtType toSMTType(Type type) {
        switch(type) {
            case INT: return SmtType.INT;
            case BOOLEAN: return SmtType.BOOL;
            default: return null;
        }
    }


    public SmtExpr toSMTExpression(Expression expression) {
        if (expression instanceof ss.group3.programverifier.ast.Number) {
            return new SmtIntLiteralExpr(((ss.group3.programverifier.ast.Number) expression).getValue());
        } else if (expression instanceof ss.group3.programverifier.ast.Boolean) {
            return new SmtBoolLiteralExpr(((ss.group3.programverifier.ast.Boolean) expression).getValue());
        } else if (expression instanceof Identifier) {
            return new SmtIdentExpr(((Identifier) expression).getValue());
        } else if (expression instanceof GreaterThan) {
            GreaterThan greaterThan = (GreaterThan) expression;
            return new SmtGreaterThanExpr(
                    toSMTExpression(greaterThan.getFirst()),
                    toSMTExpression(greaterThan.getSecond()));
        } else if (expression instanceof GreaterThanOrEqual) {
            GreaterThanOrEqual greaterThanOrEqual = (GreaterThanOrEqual) expression;
            return new SmtGreaterThanOrEqualExpr(
                    toSMTExpression(greaterThanOrEqual.getFirst()),
                    toSMTExpression(greaterThanOrEqual.getSecond()));
        } else if (expression instanceof LessThan) {
            LessThan lessThan = (LessThan) expression;
            return new SmtLessThanExpr(
                    toSMTExpression(lessThan.getFirst()),
                    toSMTExpression(lessThan.getSecond()));
        } else if (expression instanceof LessThanOrEqual) {
            LessThanOrEqual lessThanOrEqual = (LessThanOrEqual) expression;
            return new SmtLessThanOrEqualExpr(
                    toSMTExpression(lessThanOrEqual.getFirst()),
                    toSMTExpression(lessThanOrEqual.getSecond()));
        } else if (expression instanceof Equals) {
            Equals equals = (Equals) expression;
            return new SmtEqualsExpr(toSMTExpression(equals.getFirst()),
                    toSMTExpression(equals.getSecond()));
        } else if (expression instanceof NotEquals) {
            NotEquals notEquals = (NotEquals) expression;
            return new SmtNotExpr(new SmtEqualsExpr(
                    toSMTExpression(notEquals.getFirst()),
                    toSMTExpression(notEquals.getSecond())));
        } else if (expression instanceof And) {
            And and = (And) expression;
            return new SmtAndExpr(
                    toSMTExpression(and.getFirst()),
                    toSMTExpression(and.getSecond()));
        } else if (expression instanceof Or) {
            Or or = (Or) expression;
            return new SmtOrExpr(
                    toSMTExpression(or.getFirst()),
                    toSMTExpression(or.getSecond()));
        } else if (expression instanceof Implies) {
            Implies implies = (Implies) expression;
            return new SmtImpliesExpr(
                    toSMTExpression(implies.getFirst()),
                    toSMTExpression(implies.getSecond()));
        } else if (expression instanceof TernaryIf) {
            TernaryIf ternaryIf = (TernaryIf) expression;
            return new SmtIfThenElseExpr(
                    toSMTExpression(ternaryIf.getCondition()),
                    toSMTExpression(ternaryIf.getThenExpression()),
                    toSMTExpression(ternaryIf.getElseExpression()));
        } else if (expression instanceof Plus) {
            Plus plus = (Plus) expression;
            return new SmtPlusExpr(
                    toSMTExpression(plus.getFirst()),
                    toSMTExpression(plus.getSecond()));
        } else if (expression instanceof Minus) {
            Minus minus = (Minus) expression;
            return new SmtMinusExpr(
                    toSMTExpression(minus.getFirst()),
                    toSMTExpression(minus.getSecond()));
        } else if (expression instanceof Times) {
            Times times = (Times) expression;
            return new SmtTimesExpr(
                    toSMTExpression(times.getFirst()),
                    toSMTExpression(times.getSecond()));
        } else if (expression instanceof Divide) {
            Divide divide = (Divide) expression;
            return new SmtDivideExpr(
                    toSMTExpression(divide.getFirst()),
                    toSMTExpression(divide.getSecond()));
        } else if (expression instanceof Not) {
            return new SmtNotExpr(toSMTExpression(((Not) expression).getExpression()));
        } else if (expression instanceof UnaryMinus) {
            return new SmtMinusExpr(
                    new SmtIntLiteralExpr(new BigInteger("0")),
                    toSMTExpression(((UnaryMinus) expression).getExpression()));
        } else if (expression instanceof FunctionCall) {
            FunctionCall functionCall = (FunctionCall) expression;
            throw new UnsupportedOperationException("NOT IMPLEMENTED: trying to convert function call expression to smt expression");
            //TODO Z3 has support for functions. use that? :O or just 'unroll/inline' the function?
        }

        else {
            return null;
        }
    }
}
