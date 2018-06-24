package ss.group3.programverifier;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

import ss.group3.programverifier.LanguageParser.BoolExprContext;
import ss.group3.programverifier.LanguageParser.CompareExprContext;
import ss.group3.programverifier.LanguageParser.ContractExprContext;
import ss.group3.programverifier.LanguageParser.EqualOrNotEqualExprContext;
import ss.group3.programverifier.LanguageParser.FunctionCallExprContext;
import ss.group3.programverifier.LanguageParser.IdExprContext;
import ss.group3.programverifier.LanguageParser.LogicBinOpExprContext;
import ss.group3.programverifier.LanguageParser.NotExprContext;
import ss.group3.programverifier.LanguageParser.NumExprContext;
import ss.group3.programverifier.LanguageParser.ParExprContext;
import ss.group3.programverifier.LanguageParser.PlusOrMinusExprContext;
import ss.group3.programverifier.LanguageParser.TernaryIfExprContext;
import ss.group3.programverifier.LanguageParser.TimesOrDivideExprContext;
import ss.group3.programverifier.LanguageParser.UnaryMinusExprContext;

/**
 * Traverses the AST and creates Z3 Objects corresponding to expressions of the language.
 */
public class Z3ExpressionParser extends LanguageBaseVisitor<Expr> {
	private Z3Generator generator;
	private Context context;
	
	public Z3ExpressionParser(Z3Generator generator, Context context) {
		this.generator = generator;
		this.context = context;
	}
	
	@Override
	public Expr visitParExpr(ParExprContext ctx) {
		return visit(ctx.expression());
	}
	
	@Override
	public Expr visitNumExpr(NumExprContext ctx) {
		return context.mkInt(ctx.getText());
	}
	
	@Override
	public Expr visitBoolExpr(BoolExprContext ctx) {
		return context.mkBool("true".equals(ctx.getText()) ? true : false);
	}
	
	@Override
	public Expr visitIdExpr(IdExprContext ctx) {
		return generator.getVar(ctx.ID().getText());
	}
	
	@Override
	public Expr visitUnaryMinusExpr(UnaryMinusExprContext ctx) {
		return context.mkUnaryMinus((ArithExpr) visit(ctx.expression()));
	}
	
	@Override
	public Expr visitNotExpr(NotExprContext ctx) {
		return context.mkNot((BoolExpr) visit(ctx.expression()));
	}
	
	@Override
	public Expr visitTimesOrDivideExpr(TimesOrDivideExprContext ctx) {
		ArithExpr c1 = (ArithExpr) visit(ctx.expression(0));
		ArithExpr c2 = (ArithExpr) visit(ctx.expression(1));
		
		if ("*".equals(ctx.getChild(1).getText())) {
			return context.mkMul(c1, c2);
		} else {
			return context.mkDiv(c1, c2);
		}
	}
	
	@Override
	public Expr visitPlusOrMinusExpr(PlusOrMinusExprContext ctx) {
		ArithExpr c1 = (ArithExpr) visit(ctx.expression(0));
		ArithExpr c2 = (ArithExpr) visit(ctx.expression(1));
		
		if ("+".equals(ctx.getChild(1).getText())) {
			return context.mkAdd(c1, c2);
		} else {
			return context.mkSub(c1, c2);
		}
	}
	
	@Override
	public Expr visitCompareExpr(CompareExprContext ctx) {
		ArithExpr c1 = (ArithExpr) visit(ctx.expression(0));
		ArithExpr c2 = (ArithExpr) visit(ctx.expression(1));
		
		switch (ctx.getChild(1).getText()) {
		case "<":
			return context.mkLt(c1, c2);
		case ">":
			return context.mkGt(c1, c2);
		case "<=":
			return context.mkLe(c1, c2);
		case ">=":
			return context.mkGe(c1, c2);
		default:
			return null;
		}
	}
	
	@Override
	public Expr visitEqualOrNotEqualExpr(EqualOrNotEqualExprContext ctx) {
		Expr c1 = visit(ctx.expression(0));
		Expr c2 = visit(ctx.expression(1));
		
		if ("==".equals(ctx.getChild(1).getText())) {
			return context.mkEq(c1, c2);
		} else {
			return context.mkNot(context.mkEq(c1, c2));
		}
	}
	
	@Override
	public Expr visitLogicBinOpExpr(LogicBinOpExprContext ctx) {
		BoolExpr c1 = (BoolExpr) visit(ctx.expression(0));
		BoolExpr c2 = (BoolExpr) visit(ctx.expression(1));

		switch (ctx.getChild(1).getText()) {
		case "&&":
			return context.mkAnd(c1, c2);
		case "||":
			return context.mkOr(c1, c2);
		case "=>":
			return context.mkImplies(c1, c2);
		default:
			return null;
		}
	}
	
	@Override
	public Expr visitTernaryIfExpr(TernaryIfExprContext ctx) {
		BoolExpr c1 = (BoolExpr) visit(ctx.expression(0));
		Expr c2 = visit(ctx.expression(1));
		Expr c3 = visit(ctx.expression(2));
		
		return context.mkITE(c1, c2, c3);
	}
	
	@Override
	public Expr visitFunctionCallExpr(FunctionCallExprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Expr visitContractExpr(ContractExprContext ctx) {
		// TODO Auto-generated method stub
		return null;
	}
}
