package ss.group3.programverifier;

import org.antlr.v4.runtime.tree.TerminalNode;
import ss.group3.programverifier.ast.*;
import ss.group3.programverifier.ast.Boolean;
import ss.group3.programverifier.ast.Number;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

public class ToAstCompiler extends LanguageBaseVisitor<AstNode> {

    @Override
    public Program visitProgram(LanguageParser.ProgramContext context) {
        return new Program(context.statement().stream()
                .map(this::visit)
                .map(Statement.class::cast)
                .collect(Collectors.toList()));
    }

    @Override
    public Declaration visitDeclarationStat(LanguageParser.DeclarationStatContext context) {
        LanguageParser.TypeContext typeContext = context.type();
        Type type = Type.getByName(typeContext.getText());
        String identifier = context.ID().getText();
        return new Declaration(type, identifier);
    }

    @Override
    public Assign visitAssignStat(LanguageParser.AssignStatContext context) {
        Expression expression = (Expression) visit(context.expression());
        String identifier = context.ID().getText();
        LanguageParser.TypeContext typeContext = context.type();
        return typeContext == null
                ? new Assign(identifier, expression)
                : new Assign(Type.getByName(typeContext.getText()), identifier, expression);
    }

    @Override
    public If visitIfStat(LanguageParser.IfStatContext context) {
        BooleanExpression expression = (BooleanExpression) visit(context.expression());
        List<LanguageParser.StatementContext> thanElseBranches = context.statement();
        return thanElseBranches.size() == 1
                ? new If(expression, (Statement) visit(thanElseBranches.get(0)))
                : new If(expression, (Statement) visit(thanElseBranches.get(0)), (Statement) visit(thanElseBranches.get(1)));
    }

    @Override
    public While visitWhileStat(LanguageParser.WhileStatContext context) {
        BooleanExpression expression = (BooleanExpression) visit(context.expression());
        Statement statement = (Statement) visit(context.statement());
        return new While(expression, statement);
    }

    @Override
    public Return visitReturnStat(LanguageParser.ReturnStatContext context) {
        return new Return((Expression) visit(context.expression()));
    }

    @Override
    public FunctionDef visitFunctionDefStat(LanguageParser.FunctionDefStatContext context) {
        Type returnType = Type.getByName(context.return_type().getText());
        String identifier = context.ID(0).getText();
        List<TerminalNode> ids = context.ID();
        ids = ids.subList(1, ids.size());
        List<String> parameterIds = ids.stream().map(TerminalNode::getText).collect(Collectors.toList());
        List<Type> parameterTypes = context.type().stream()
                .map(LanguageParser.TypeContext::getText)
                .map(Type::getByName)
                .collect(Collectors.toList());
        Statement body = (Statement) visit(context.statement());
        return new FunctionDef(identifier, returnType, body, parameterTypes, parameterIds);
    }

    @Override
    public Block visitBlockStat(LanguageParser.BlockStatContext context) {
        return new Block(context.statement().stream()
                .map(this::visit)
                .map(Statement.class::cast)
                .collect(Collectors.toList()));
    }

    @Override
    public Contract visitContractStat(LanguageParser.ContractStatContext context) {
        ContractKind type = ContractKind.getByName(context.contract_type().getText());
        BooleanExpression expression = (BooleanExpression) visit(context.expression());
        return new Contract(type, expression);
    }

    @Override
    public Expression visitParExpr(LanguageParser.ParExprContext context) {
        return (Expression) visit(context.expression());
    }

    @Override
    public Number visitNumExpr(LanguageParser.NumExprContext context) {
        return new Number(new BigInteger(context.getText()));
    }

    @Override
    public Boolean visitBoolExpr(LanguageParser.BoolExprContext context) {
        return new Boolean(java.lang.Boolean.parseBoolean(context.getText()));
    }

    @Override
    public Identifier visitIdExpr(LanguageParser.IdExprContext context) {
        return new Identifier(context.getText());
    }

    @Override
    public UnaryMinus visitUnaryMinusExpr(LanguageParser.UnaryMinusExprContext context) {
        return new UnaryMinus((IntExpression) visit(context.expression()));
    }

    @Override
    public Not visitNotExpr(LanguageParser.NotExprContext context) {
        return new Not((BooleanExpression) visit(context.expression()));
    }

    @Override
    public IntExpression visitTimesOrDivideExpr(LanguageParser.TimesOrDivideExprContext context) {
        IntExpression first = (IntExpression) visit(context.expression(0));
        IntExpression second = (IntExpression) visit(context.expression(1));
        switch (context.getChild(1).getText()) {
            case "*": return new Times(first, second);
            case "/": return new Divide(first, second);
        }
        return null;
    }

    @Override
    public IntExpression visitPlusOrMinusExpr(LanguageParser.PlusOrMinusExprContext context) {
        IntExpression first = (IntExpression) visit(context.expression(0));
        IntExpression second = (IntExpression) visit(context.expression(1));
        switch (context.getChild(1).getText()) {
            case "+": return new Plus(first, second);
            case "-": return new Minus(first, second);
        }
        return null;
    }

    @Override
    public BooleanExpression visitCompareExpr(LanguageParser.CompareExprContext context) {
        IntExpression first = (IntExpression) visit(context.expression(0));
        IntExpression second = (IntExpression) visit(context.expression(1));
        switch (context.getChild(1).getText()) {
            case "<": return new LessThan(first, second);
            case "<=": return new LessThanOrEqual(first, second);
            case ">": return new GreaterThan(first, second);
            case ">=": return new GreaterThanOrEqual(first, second);
        }
        return null;
    }

    @Override
    public BooleanExpression visitEqualOrNotEqualExpr(LanguageParser.EqualOrNotEqualExprContext context) {
        Expression first = (Expression) visit(context.expression(0));
        Expression second = (Expression) visit(context.expression(1));
        switch (context.getChild(1).getText()) {
            case "==": return new Equals<>(first, second);
            case "!=": return new NotEquals<>(first, second);
        }
        return null;
    }

    @Override
    public Implies visitImpliesExpr(LanguageParser.ImpliesExprContext context) {
        return new Implies((BooleanExpression) visit(context.expression(0)),
                (BooleanExpression) visit(context.expression(1)));
    }

    @Override
    public TernaryIf<? extends Expression> visitTernaryIfExpr(LanguageParser.TernaryIfExprContext context) {
        return new TernaryIf<>((BooleanExpression) visit(context.expression(0)),
                (Expression) visit(context.expression(1)),
                (Expression) visit(context.expression(2)));
    }

    @Override
    public FunctionCall visitFunctionCallExpr(LanguageParser.FunctionCallExprContext context) {
        return new FunctionCall(context.ID().getText(), context.expression().stream()
                .map(this::visit)
                .map(Expression.class::cast)
                .collect(Collectors.toList()));
    }

    @Override
    public ContractExpression visitContractExpr(LanguageParser.ContractExprContext context) {
        return (ContractExpression) visit(context.contract_expression());
    }

    @Override
    public Result visitResultContrExpr(LanguageParser.ResultContrExprContext context) {
        return new Result();
    }

    @Override
    public Old visitOldContrExpr(LanguageParser.OldContrExprContext context) {
        return new Old(context.ID().getText());
    }

}
