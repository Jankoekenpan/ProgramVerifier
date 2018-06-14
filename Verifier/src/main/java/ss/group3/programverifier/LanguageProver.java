package ss.group3.programverifier;

import com.microsoft.z3.*;
import ss.group3.programverifier.ast.*;
import ss.group3.programverifier.ast.Number;
import ss.group3.programverifier.ast.Boolean;
import ss.group3.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanguageProver {

    private static final String PATH_CONDITION_IDENTIFIER = "_PATH";

    private final Map<Integer, Map<String, String>> conditionsToVariablesToRenamedVariables = new HashMap<>();
    private int currentPathCondition;

    /** The Z3 main context */
    private final Context context;
    /** The Z3 solver */
    private final Solver solver;

    /** Mapping of smt identifiers to function declaration,
     * used for program identifier evaluation of smt expressions */
    private final Map<String, Expr> smtIdentifiers = new HashMap<>();

    public LanguageProver() {
        this.context = new Context();
        this.solver = context.mkSolver();
    }

    public boolean proveProgram(Program program) {
        context.mkBoolConst(getSmtIdentifier(PATH_CONDITION_IDENTIFIER, currentPathCondition));

        return program.getContents().stream().allMatch(statement -> proveStatement(statement, currentPathCondition));
    }

    private boolean proveStatement(Statement statement, final int pathCondition) {
        if (statement instanceof Declaration) {
            return proveDeclaration((Declaration) statement, pathCondition);
        } else if (statement instanceof FunctionDef) {
            return proveFunctionDef((FunctionDef) statement, pathCondition);
        } else if (statement instanceof Block) {
            return ((Block) statement).getStatements().stream().allMatch(s -> proveStatement(s, pathCondition));
        } else if (statement instanceof Assign) {
            return proveAssign((Assign) statement, pathCondition);
        } else if (statement instanceof If) {
            //TODO
            return true;
        }

        //TODO other cases

        return false;
    }

    private boolean proveDeclaration(Declaration declaration, final int pathCondition) {
        Type type = declaration.getType();
        String id = declaration.getIdentifier();

        String smtIdentifier = getSmtIdentifier(id, pathCondition);
        Expr expr = context.mkConst(smtIdentifier, toSMTType(type));
        smtIdentifiers.put(smtIdentifier, expr);

        if (declaration.hasExpression()) {
            Assign assign = new Assign(id, declaration.getExpression());
            return proveAssign(assign, pathCondition);
        }

        return true;
    }

    private BoolExpr makePathCondition(int pathCondition, Expr smtExpression) {
        String smtPathConditionIdentifier = toSMTIdentifier(PATH_CONDITION_IDENTIFIER, pathCondition);
        BoolExpr smtPathCondition = context.mkBoolConst(smtPathConditionIdentifier);
        smtIdentifiers.put(smtPathConditionIdentifier, smtPathCondition);
        return smtPathCondition;
    }

    private boolean proveIf(If ifStatement, final int pathCondition) {
        Expression condition = ifStatement.getCondition();
        Statement thanBranch = ifStatement.getThenBranch();

        //declare a new path condition variable
        Expr smtThanCondition = toSMTExpression(condition, pathCondition);
        int thanPathCondition = nextPathCondition();
        BoolExpr smtThenPathCondition = makePathCondition(thanPathCondition, smtThanCondition);

        //TODO use results?
        proveStatement(thanBranch, thanPathCondition);

        if (ifStatement.hasElseBranch()) {
            Statement elseBranch = ifStatement.getElseBranch();

            Expr smtElseCondition = context.mkNot(smtThenPathCondition);
            int elsePathCondition = nextPathCondition();
            makePathCondition(elsePathCondition, smtElseCondition);

            //TODO use results?
            proveStatement(elseBranch, elsePathCondition);
        }

        return true;
    }

    private boolean proveAssign(Assign assign, final int pathCondition) {
        //TODO make conditional assign

        String identifier = assign.getIdentifier();
        Expression expression = assign.getExpression();

        String c = toSMTIdentifier(PATH_CONDITION_IDENTIFIER, pathCondition);
        //TODO
        //context.mkITE();

        return true;
    }

    private boolean proveFunctionDef(FunctionDef functionDef, final int pathCondition) {
        int canBeExecutedPathCondition = nextPathCondition();
        for (Pair<Type, String> pair : functionDef.getParameterPairs()) {
            Type type = pair.getFirst();
            String identifier = pair.getSecond();

            Declaration declaration = new Declaration(type, identifier);
            proveDeclaration(declaration, pathCondition);
            //TODO `and` the result?
        }

        Statement body = functionDef.getBody();
        proveStatement(body, canBeExecutedPathCondition);
        //TODO `and` the result?

        //TODO contracts
        //Set<Contract> contracts = functionDef.getContracts();

        return true;
    }


    public Expr toSMTExpression(final Expression expression, final int pathCondition) {
        if (expression instanceof Number) {
            return context.mkInt(((Number) expression).getValue().toString(10));
        } else if (expression instanceof Boolean) {
            return context.mkBool(((Boolean) expression).getValue());
        } else if (expression instanceof Identifier) {
            //use cached fun decl and evaluate with zero arguments
            Identifier identifier = (Identifier) expression;
            String id = identifier.getValue();
            String smtId = getSmtIdentifier(id, pathCondition);
            return smtIdentifiers.get(smtId);
        } else if (expression instanceof GreaterThan) {
            GreaterThan greaterThan = (GreaterThan) expression;
            return context.mkGt(
                    (ArithExpr) toSMTExpression(greaterThan.getFirst(), pathCondition),
                    (ArithExpr) toSMTExpression(greaterThan.getSecond(), pathCondition));
        } else if (expression instanceof GreaterThanOrEqual) {
            GreaterThanOrEqual greaterThanOrEqual = (GreaterThanOrEqual) expression;
            return context.mkGe(
                    (ArithExpr) toSMTExpression(greaterThanOrEqual.getFirst(), pathCondition),
                    (ArithExpr) toSMTExpression(greaterThanOrEqual.getSecond(), pathCondition));
        } else if (expression instanceof LessThan) {
            LessThan lessThan = (LessThan) expression;
            return context.mkLt(
                    (ArithExpr) toSMTExpression(lessThan.getFirst(), pathCondition),
                    (ArithExpr) toSMTExpression(lessThan.getSecond(), pathCondition));
        } else if (expression instanceof LessThanOrEqual) {
            LessThanOrEqual lessThanOrEqual = (LessThanOrEqual) expression;
            return context.mkLe(
                    (ArithExpr) toSMTExpression(lessThanOrEqual.getFirst(), pathCondition),
                    (ArithExpr) toSMTExpression(lessThanOrEqual.getSecond(), pathCondition));
        } else if (expression instanceof Equals) {
            Equals equals = (Equals) expression;
            return context.mkEq(
                    toSMTExpression(equals.getFirst(), pathCondition),
                    toSMTExpression(equals.getSecond(), pathCondition));
        } else if (expression instanceof NotEquals) {
            NotEquals notEquals = (NotEquals) expression;
            return context.mkNot(context.mkEq(
                    toSMTExpression(notEquals.getFirst(), pathCondition),
                    toSMTExpression(notEquals.getSecond(), pathCondition)));
        } else if (expression instanceof And) {
            And and = (And) expression;
            return context.mkAnd(
                    (BoolExpr) toSMTExpression(and.getFirst(), pathCondition),
                    (BoolExpr) toSMTExpression(and.getSecond(), pathCondition));
        } else if (expression instanceof Or) {
            Or or = (Or) expression;
            return context.mkOr(
                    (BoolExpr) toSMTExpression(or.getFirst(), pathCondition),
                    (BoolExpr) toSMTExpression(or.getSecond(), pathCondition));
        } else if (expression instanceof Implies) {
            Implies implies = (Implies) expression;
            return context.mkImplies(
                    (BoolExpr) toSMTExpression(implies.getFirst(), pathCondition),
                    (BoolExpr) toSMTExpression(implies.getSecond(), pathCondition));
        } else if (expression instanceof TernaryIf) {
            TernaryIf ternaryIf = (TernaryIf) expression;
            return context.mkITE(
                    (BoolExpr) toSMTExpression(ternaryIf.getCondition(), pathCondition),
                    toSMTExpression(ternaryIf.getThenExpression(), pathCondition),
                    toSMTExpression(ternaryIf.getElseExpression(), pathCondition));
        } else if (expression instanceof Plus) {
            Plus plus = (Plus) expression;
            return context.mkAdd(
                    (ArithExpr) toSMTExpression(plus.getFirst(), pathCondition),
                    (ArithExpr) toSMTExpression(plus.getSecond(), pathCondition));
        } else if (expression instanceof Minus) {
            Minus minus = (Minus) expression;
            return context.mkSub(
                    (ArithExpr) toSMTExpression(minus.getFirst(), pathCondition),
                    (ArithExpr) toSMTExpression(minus.getSecond(), pathCondition));
        } else if (expression instanceof Times) {
            Times times = (Times) expression;
            return context.mkMul(
                    (ArithExpr) toSMTExpression(times.getFirst(), pathCondition),
                    (ArithExpr) toSMTExpression(times.getSecond(), pathCondition));
        } else if (expression instanceof Divide) {
            Divide divide = (Divide) expression;
            return context.mkDiv(
                    (ArithExpr) toSMTExpression(divide.getFirst(), pathCondition),
                    (ArithExpr) toSMTExpression(divide.getSecond(), pathCondition));
        } else if (expression instanceof Not) {
            Not not = (Not) expression;
            return context.mkNot((BoolExpr) toSMTExpression(not.getExpression(), pathCondition));
        } else if (expression instanceof UnaryMinus) {
            UnaryMinus unaryMinus = (UnaryMinus) expression;
            return context.mkUnaryMinus((ArithExpr) toSMTExpression(unaryMinus.getExpression(), pathCondition));
        } else if (expression instanceof FunctionCall) {
            FunctionCall functionCall = (FunctionCall) expression;

            throw new UnsupportedOperationException("Function call expressions are not implemented.");

            //TODO is this the right way to do it?
//            String functionId = functionCall.getFunctionIdentifier();
//            List<Expression> arguments = functionCall.getArguments();
//
//            FuncDecl funDecl = null; //TODO
//            Expr[] args = arguments.stream().map(e -> toSMTExpression(e, pathCondition)).toArray(size -> new Expr[size]);
//
//            return funDecl.apply(args);
        }

        //TODO contract expressions

        else {
            throw new UnsupportedOperationException("Tried to convert Expression " + expression + " to an SMT expression, but didn't know how to.");
        }
    }

    private Sort toSMTType(Type type) {
        switch(type) {
            case INT: return context.getIntSort();
            case BOOLEAN: return context.getBoolSort();
            default: return null;
        }
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

}
