package ss.group3.programverifier;

import java.io.IOException;
import java.util.HashMap;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;

import ss.group3.programverifier.LanguageParser.AssignStatContext;
import ss.group3.programverifier.LanguageParser.BoolExprContext;
import ss.group3.programverifier.LanguageParser.CompareExprContext;
import ss.group3.programverifier.LanguageParser.ContractExprContext;
import ss.group3.programverifier.LanguageParser.DeclarationStatContext;
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

public class Z3Generator extends LanguageBaseListener {
	private HashMap<String, Integer> idCount = new HashMap<>();

	private ParseTreeProperty<Expr> exprs = new ParseTreeProperty<>();
	
	private Context context;
	private Solver solver;
	
	public Z3Generator() {
		context = new Context();
		solver = context.mkSolver();
	}
	
	private void put(ParseTree ctx, Expr expr) {
		exprs.put(ctx, expr);
	}
	
	// helper functions
	
	private Expr get(ParseTree ctx) {
		return exprs.get(ctx);
	}
	
	private ArithExpr getArith(ParseTree ctx) {
		Expr expr = exprs.get(ctx);
		if (expr instanceof ArithExpr) {
			return (ArithExpr) expr;
		} else {
			return null;
		}
	}
	
	private BoolExpr getBool(ParseTree ctx) {
		Expr expr = exprs.get(ctx);
		if (expr instanceof BoolExpr) {
			return (BoolExpr) expr;
		} else {
			return null;
		}
	}
	
	// Expressions
	
	@Override
	public void exitParExpr(ParExprContext ctx) {
		put(ctx, get(ctx.expression()));
	}
	
	@Override
	public void exitNumExpr(NumExprContext ctx) {
		String num = ctx.getText();
		put(ctx, context.mkInt(num));
	}
	
	@Override
	public void exitBoolExpr(BoolExprContext ctx) {
		String val = ctx.getText();
		put(ctx, context.mkBool("true".equals(val) ? true : false));
	}
	
	@Override
	public void exitIdExpr(IdExprContext ctx) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void exitUnaryMinusExpr(UnaryMinusExprContext ctx) {
		ArithExpr unaryMinus = context.mkUnaryMinus(getArith(ctx.expression()));
		put(ctx, unaryMinus);
	}
	
	@Override
	public void exitNotExpr(NotExprContext ctx) {
		BoolExpr not = context.mkNot(getBool(ctx.expression()));
		put(ctx, not);
	}
	
	@Override
	public void exitTimesOrDivideExpr(TimesOrDivideExprContext ctx) {
		ArithExpr c1 = getArith(ctx.expression(0));
		ArithExpr c2 = getArith(ctx.expression(1));

		ArithExpr expr = "*".equals(ctx.getChild(1).getText()) ? context.mkMul(c1, c2) : context.mkDiv(c1, c2);

		put(ctx, expr);
	}
	
	@Override
	public void exitPlusOrMinusExpr(PlusOrMinusExprContext ctx) {
		ArithExpr c1 = getArith(ctx.expression(0));
		ArithExpr c2 = getArith(ctx.expression(1));

		ArithExpr expr = "+".equals(ctx.getChild(1).getText()) ? context.mkAdd(c1, c2) : context.mkSub(c1, c2);

		put(ctx, expr);
	}
	
	@Override
	public void exitCompareExpr(CompareExprContext ctx) {
		ArithExpr c1 = getArith(ctx.expression(0));
		ArithExpr c2 = getArith(ctx.expression(1));

		BoolExpr r = null;
		
		switch (ctx.getChild(1).getText()) {
		case "<":
			r = context.mkGt(c1, c2);
			break;
		case ">":
			r = context.mkLt(c1, c2);
			break;
		case "<=":
			r = context.mkGe(c1, c2);
			break;
		case ">=":
			r = context.mkLe(c1, c2);
			break;
		default:
			break;
		}
		
		put(ctx, r);
	}
	
	@Override
	public void exitEqualOrNotEqualExpr(EqualOrNotEqualExprContext ctx) {
		// equality can be with both arith and bool expressions
		Expr c1 = get(ctx.expression(0));
		Expr c2 = get(ctx.expression(1));

		BoolExpr expr = "+".equals(ctx.getChild(1).getText()) ? context.mkEq(c1, c2) : context.mkNot(context.mkEq(c1, c2));

		put(ctx, expr);
	}
	
	@Override
	public void exitLogicBinOpExpr(LogicBinOpExprContext ctx) {
		BoolExpr c1 = getBool(ctx.expression(0));
		BoolExpr c2 = getBool(ctx.expression(1));

		BoolExpr r = null;
		
		switch (ctx.getChild(1).getText()) {
		case "&&":
			r = context.mkAnd(c1, c2);
			break;
		case "||":
			r = context.mkOr(c1, c2);
			break;
		case "=>":
			r = context.mkImplies(c1, c2);
			break;
		default:
			break;
		}
		
		put(ctx, r);
	}
	
	@Override
	public void exitTernaryIfExpr(TernaryIfExprContext ctx) {
		BoolExpr c1 = getBool(ctx.expression(0));
		Expr c2 = get(ctx.expression(1));
		Expr c3 = get(ctx.expression(2));
		
		put(ctx, context.mkITE(c1, c2, c3));
	}
	
	@Override
	public void exitFunctionCallExpr(FunctionCallExprContext ctx) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void exitContractExpr(ContractExprContext ctx) {
		// TODO Auto-generated method stub
		super.enterContractExpr(ctx);
	}
	
	// statements
	
	private HashMap<String, Expr> variables = new HashMap<>();
	
	@Override
	public void exitDeclarationStat(DeclarationStatContext ctx) {
		IntExpr intConst = context.mkIntConst(ctx.ID().getText());
		
		BoolExpr expr = context.mkEq(intConst, get(ctx.expression()));
		solver.add(expr);
		
		System.out.println(solver);
	}
	
	@Override
	public void exitAssignStat(AssignStatContext ctx) {
		Expr expr = variables.get(ctx.ID().getText());
		
		// TODO: SSA renaming
		
		context.mkEq(expr, get(ctx.expression()));
	}
	
	public static void main(String[] args) throws IOException {
		CharStream input = new ANTLRFileStream("src/main/resources/assign.hello");
		LanguageLexer lexer = new LanguageLexer(input);
		TokenStream stream = new CommonTokenStream(lexer);
		LanguageParser parser = new LanguageParser(stream);

		//parse tree
		LanguageParser.ProgramContext programTree = parser.program();
		
		Z3Generator generator = new Z3Generator();
		new ParseTreeWalker().walk(generator, programTree);
	}
}
