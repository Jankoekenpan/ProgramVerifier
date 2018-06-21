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
        LanguageParser.ExpressionContext expressionContext = context.expression();
        return expressionContext == null
                ? new Declaration(type, identifier)
                : new Declaration(type, identifier, (Expression) visit(expressionContext));
    }

    @Override
    public Assign visitAssignStat(LanguageParser.AssignStatContext context) {
        String identifier = context.ID().getText();
        Expression expression = (Expression) visit(context.expression());
        return new Assign(identifier, expression);
    }

    @Override
    public If visitIfStat(LanguageParser.IfStatContext context) {
        Expression expression = (Expression) visit(context.expression());
        List<LanguageParser.StatementContext> thanElseBranches = context.statement();
        return thanElseBranches.size() == 1
                ? new If(expression, (Statement) visit(thanElseBranches.get(0)))
                : new If(expression, (Statement) visit(thanElseBranches.get(0)), (Statement) visit(thanElseBranches.get(1)));
    }

    @Override
    public While visitWhileStat(LanguageParser.WhileStatContext context) {
        Expression expression = (Expression) visit(context.expression());
        Statement statement = (Statement) visit(context.statement());
        List<Contract> contracts = context.contract().stream()
                .map(this::visit)
                .map(Contract.class::cast)
                .collect(Collectors.toList());

        return new While(expression, statement, contracts);
    }

    @Override
    public Return visitReturnStat(LanguageParser.ReturnStatContext context) {
        LanguageParser.ExpressionContext expressionContext = context.expression();
        return expressionContext == null
                ? new Return()
                : new Return((Expression) visit(context.expression()));
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
        List<Contract> contracts = context.contract().stream()
                .map(this::visit)
                .map(Contract.class::cast)
                .collect(Collectors.toList());
        return new FunctionDef(identifier, returnType, body, parameterTypes, parameterIds, contracts);
    }

    @Override
    public Block visitBlockStat(LanguageParser.BlockStatContext context) {
        return new Block(context.statement().stream()
                .map(this::visit)
                .map(Statement.class::cast)
                .collect(Collectors.toList()));
    }

    @Override
    public Contract visitContract(LanguageParser.ContractContext context) {
        ContractKind type = ContractKind.getByName(context.contract_type().getText());
        Expression expression = (Expression) visit(context.expression());
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
        return new UnaryMinus((Expression) visit(context.expression()));
    }

    @Override
    public Not visitNotExpr(LanguageParser.NotExprContext context) {
        return new Not((Expression) visit(context.expression()));
    }

    @Override
    public Expression visitTimesOrDivideExpr(LanguageParser.TimesOrDivideExprContext context) {
        Expression first = (Expression) visit(context.expression(0));
        Expression second = (Expression) visit(context.expression(1));
        switch (context.getChild(1).getText()) {
            case "*": return new Times(first, second);
            case "/": return new Divide(first, second);
        }
        return null;
    }

    @Override
    public Expression visitPlusOrMinusExpr(LanguageParser.PlusOrMinusExprContext context) {
        Expression first = (Expression) visit(context.expression(0));
        Expression second = (Expression) visit(context.expression(1));
        switch (context.getChild(1).getText()) {
            case "+": return new Plus(first, second);
            case "-": return new Minus(first, second);
        }
        return null;
    }

    @Override
    public Expression visitCompareExpr(LanguageParser.CompareExprContext context) {
        Expression first = (Expression) visit(context.expression(0));
        Expression second = (Expression) visit(context.expression(1));
        switch (context.getChild(1).getText()) {
            case "<": return new LessThan(first, second);
            case "<=": return new LessThanOrEqual(first, second);
            case ">": return new GreaterThan(first, second);
            case ">=": return new GreaterThanOrEqual(first, second);
        }
        return null;
    }

    @Override
    public Expression visitEqualOrNotEqualExpr(LanguageParser.EqualOrNotEqualExprContext context) {
        Expression first = (Expression) visit(context.expression(0));
        Expression second = (Expression) visit(context.expression(1));
        switch (context.getChild(1).getText()) {
            case "==": return new Equals(first, second);
            case "!=": return new NotEquals(first, second);
        }
        return null;
    }

    @Override
    public Expression visitLogicBinOpExpr(LanguageParser.LogicBinOpExprContext context) {
        Expression first = (Expression) visit(context.expression(0));
        Expression second = (Expression) visit(context.expression(1));
        switch (context.getChild(1).getText()) {
            case "&&": return new And(first, second);
            case "||": return new Or(first, second);
            case "=>": return new Implies(first, second);
        }
        return null;
    }

    @Override
    public TernaryIf visitTernaryIfExpr(LanguageParser.TernaryIfExprContext context) {
        return new TernaryIf((Expression) visit(context.expression(0)),
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

    @Override
    public Contract visitContractStat(LanguageParser.ContractStatContext context) {
        LanguageParser.ContractContext contractContext = context.contract();
        LanguageParser.ExpressionContext contractExpressionContext = contractContext.expression();
        LanguageParser.Contract_typeContext contractTypeContext = contractContext.contract_type();

        ContractKind kind = ContractKind.getByName(contractTypeContext.getText());
        Expression expression = (Expression) visit(contractExpressionContext);
        return new Contract(kind, expression);
    }

}
