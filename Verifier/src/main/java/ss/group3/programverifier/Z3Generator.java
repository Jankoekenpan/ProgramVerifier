package ss.group3.programverifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.antlr.v4.runtime.tree.ParseTree;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import ss.group3.programverifier.LanguageParser.AssignStatContext;
import ss.group3.programverifier.LanguageParser.ContractContext;
import ss.group3.programverifier.LanguageParser.DeclarationStatContext;
import ss.group3.programverifier.LanguageParser.IfStatContext;
import ss.group3.programverifier.LanguageParser.ProgramContext;

/**
 * Traverses the AST and generates the SMT statements.
 */
public class Z3Generator extends LanguageBaseVisitor<Void> {
	/**
	 * A map from variable id to a counter, which is incremented with every assignment.
	 */
	private HashMap<String, Integer> idCount = new HashMap<>();
	
	/**
	 * map variable id to its type.
	 */
	private HashMap<String, String> types = new HashMap<>();
	
	/**
	 * Used to keep track of what scope and what path condition holds during traversal.
	 */
	private Stack<Scope> scopeStack = new Stack<>();
	
	/**
	 * A counter that is incremented with every new path condition.
	 */
	private int conditionCount = 0;
	
	private List<ProgramError> errors = new ArrayList<>();
	
	private Context c;
	private Solver solver;

	private Z3ExpressionParser expressionParser;
	
	public Z3Generator() {
		c = new Context();
		solver = c.mkSolver();
		
		expressionParser = new Z3ExpressionParser(this, c);
	}
	
	private Expr expr(ParseTree ctx) {
		return expressionParser.visit(ctx);
	}
	
	/**
	 * Creates a Z3 const based on a type.
	 */
	private Expr makeConst(String id, String type) {
		switch (type) {
		case "int":
			return c.mkIntConst(id);
		case "bool":
			return c.mkBoolConst(id);
		default:
			throw new RuntimeException("Unknown type " + type);
		}
	}
	
	private BoolExpr newPathCond() {
		String id = "$c$" + conditionCount++;
		return c.mkBoolConst(id);
	}
	
	/**
	 * @return The current path condition.
	 */
	private BoolExpr currentCond() {
		return scopeStack.peek().pathCondition;
	}
	
	/**
	 * @return Whether the given variable id is already declared.
	 */
	private boolean isVarDeclared(String id) {
		for (Scope scope : scopeStack) {
			if (scope.renaming.containsKey(id)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return An expression corresponding to the current assignment of the given variable.
	 */
	Expr getVar(String id) {
		for (Scope scope : scopeStack) {
			if (scope.renaming.containsKey(id)) {
				return scope.renaming.get(id);
			}
		}
		
		throw new IllegalArgumentException(String.format("Variable \"%s\" not in scope", id));
	}
	
	/**
	 * Generates and registers a new Z3 const of the given variable.
	 */
	private Expr newVar(String id) {
		int count = idCount.getOrDefault(id, 1);
		String newId = id + "$" + count;
		
		Expr newConst = makeConst(newId, types.get(id));
		
		idCount.put(id, count + 1);
		
		scopeStack.peek().renaming.put(id, newConst);
		
		return newConst;
	}
	
	// statements
	
	@Override
	public Void visitDeclarationStat(DeclarationStatContext ctx) {
		String id = ctx.ID().getText();
		String type = ctx.type().getText();
		
		// the fist counter value is 0, the $ symbol is used because it is not in the grammar, guaranteeing no 
		// collisions.
		Expr constExpr = makeConst(id + "$0", type);
		
		if (isVarDeclared(id)) {
			// TODO: scopes?
			throw new RuntimeException("Duplicate variable " + id);
		}
		
		types.put(id, type);
		scopeStack.peek().renaming.put(id, constExpr);
		
		// assignment is optional
		if(ctx.expression() != null) {
			BoolExpr expr = c.mkEq(constExpr, expr(ctx.expression()));
			solver.add(expr);
		}
		
		return null;
	}
	
	@Override
	public Void visitAssignStat(AssignStatContext ctx) {
		String id = ctx.ID().getText();
		
		// TODO: not sure if it's needed to set it to the old value if the path condition does not hold.
		Expr prevConst = getVar(id);
		Expr newConst = newVar(id);
		
		BoolExpr eq = c.mkEq(newConst, c.mkITE(currentCond(), expr(ctx.expression()), prevConst));
		solver.add(eq);
		
		return null;
	}
	
	/**
	 * @return The set of variable id's that are changed in the scope of the branch, compared to the current scope.
	 */
	private Set<String> getChangedVars(Scope branch, Scope curScope) {
		HashSet<String> result = new HashSet<>();
		
		for (String var : branch.renaming.keySet()) {
			if (curScope.renaming.containsKey(var)) {
				boolean trueChanged = curScope.renaming.get(var) != branch.renaming.get(var);
				if (trueChanged) {
					result.add(var);
				}
			}
		}
		
		return result;
	}
	
	@Override
	public Void visitIfStat(IfStatContext ctx) {
		BoolExpr condition = (BoolExpr) expr(ctx.expression());
		
		BoolExpr trueCond = newPathCond();
		BoolExpr falseCond = newPathCond();

		solver.add(c.mkEq(trueCond, c.mkAnd(currentCond(), condition)));
		solver.add(c.mkEq(falseCond, c.mkAnd(currentCond(), c.mkNot(trueCond))));

		Scope curScope = scopeStack.peek();
		
		Scope trueScope = new Scope(trueCond);
		trueScope.renaming.putAll(curScope.renaming);
		
		Scope falseScope = new Scope(falseCond);
		falseScope.renaming.putAll(curScope.renaming);
		
		scopeStack.push(trueScope);
		visit(ctx.statement(0));
		scopeStack.pop();
		
		if(ctx.statement().size() > 1) {
			scopeStack.push(falseScope);
			visit(ctx.statement(1));
			scopeStack.pop();
		}
		
		// update path condition
		
		BoolExpr pathCond = newPathCond();
		solver.add(c.mkEq(pathCond, c.mkOr(trueScope.pathCondition, falseScope.pathCondition)));
		curScope.pathCondition = pathCond;
		
		// put updated variables in current scope

		Set<String> trueVars = getChangedVars(trueScope, curScope);
		Set<String> falseVars = getChangedVars(falseScope, curScope);
		Set<String> curVars = curScope.renaming.keySet();
		
		// all variables that are changed in both the if and else branch
		Set<String> union = new HashSet<>();
		union.addAll(trueVars);
		union.retainAll(falseVars);
		
		// all variables that are changed in either the if or the else branch
		Set<String> diff = new HashSet<>();
		diff.addAll(trueVars);
		diff.addAll(falseVars);
		diff.removeAll(union);

		System.out.println(ctx.expression().getText());
		System.out.println("Cur vars: " + String.join(", ", curVars));
		System.out.println("Union vars: " + String.join(", ", union));
		System.out.println("Diff vars: " + String.join(", ", diff));
		
		// create new variable and set it equal to the values of the branches depending on the path conditions.
		for(String var : union) {
			Expr trueVar = trueScope.renaming.get(var);
			Expr falseVar = falseScope.renaming.get(var);
			Expr defaultVar = curScope.renaming.get(var);
			
			Expr e = c.mkITE(trueScope.pathCondition, trueVar, c.mkITE(falseScope.pathCondition, falseVar, defaultVar));
			solver.add(c.mkEq(newVar(var), e));
		}
		
		for(String var : diff) {
			Expr e;
			Expr defaultVar = curScope.renaming.get(var);
			BoolExpr cond;
			
			if(trueVars.contains(var)) {
				e = trueScope.renaming.get(var);
				cond = trueScope.pathCondition;
			} else if(falseVars.contains(var)) {
				e = falseScope.renaming.get(var);
				cond = falseScope.pathCondition;
			} else {
				throw new RuntimeException(String.format("Variable \"%s\" not found in previous scopes", var));
			}
			
			solver.add(c.mkEq(newVar(var), c.mkITE(cond, e, defaultVar)));
		}
		
		return null;
	}
	
	// contracts
	
	@Override
	public Void visitContract(ContractContext ctx) {
		switch (ctx.contract_type().getText()) {
		case "assert":
			solver.push();
			
			solver.add(c.mkNot((BoolExpr) expr(ctx.expression())));
			
			if (solver.check() == Status.SATISFIABLE) {
				errors.add(new ProgramError(ctx, solver.getModel(), solver.toString()));
			}
			
			solver.pop();
			break;
		default:
			break;
		}
		
		return null;
	}
	
	// program
	
	@Override
	public Void visitProgram(ProgramContext ctx) {
		// base scope, path condition set to true
		BoolExpr cond = newPathCond();
		solver.add(c.mkEq(c.mkTrue(), cond));
		scopeStack.push(new Scope(cond));
		
		super.visitProgram(ctx);
		
		System.out.println(solver);
		
		return null;
	}
	
	// public interface
	
	public boolean isCorrect() {
		return errors.isEmpty();
	}
	
	public List<ProgramError> getErrors() {
		return Collections.unmodifiableList(errors);
	}
	
	private class Scope {
		BoolExpr pathCondition;
		
		/**
		 * map variable id to SSA valid z3 expression
		 */
		Map<String, Expr> renaming = new HashMap<>();
		
		Scope(BoolExpr pathCondition) {
			this.pathCondition = pathCondition;
		}
	}
}
