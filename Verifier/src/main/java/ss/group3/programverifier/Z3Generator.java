package ss.group3.programverifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import ss.group3.programverifier.LanguageParser.*;
import ss.group3.programverifier.ast.ContractExpression;

/**
 * Traverses the AST and generates the SMT statements.
 */
public class Z3Generator extends LanguageBaseVisitor<Void> {
	/**
	 * A map from variable id to a counter, which is incremented with every assignment.
	 */
	private HashMap<String, Integer> idCount = new HashMap<>();
	
	/**
	 * Used to keep track of what scope and what path condition holds during traversal.
	 */
	private Stack<Scope> scopeStack = new Stack<>();
	
	/**
	 * A counter that is incremented with every new path condition.
	 */
	private int conditionCount = 0;
	
	/**
	 * Incremented with every function call for unique names.
	 */
	private int callCount = 0;
	
	private Map<String, FunctionDefStatContext> functions;
	
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
		case "boolean":
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
		return curScope().pathCondition;
	}
	
	/**
	 * @return Whether the given variable id is already declared.
	 */
	private boolean isVarDeclared(String id) {
		for (Scope scope : scopeStack) {
			if (scope.variables.containsKey(id)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return The text of the given identifier with either "main" or the id
	 * of the function the identifier is in prefixed. This prevents duplicate
	 * variable names.
	 */
	String getId(TerminalNode ctx) {
		ParseTree c = ctx;
		while (c != null && !(c instanceof FunctionDefStatContext)) {
			c = c.getParent();
		}
		
		String prefix = c == null ? "main" : ((FunctionDefStatContext) c).ID().getText();
		return prefix + "." + ctx.getText();
	}
	
	/**
	 * @return An expression corresponding to the current assignment of the given variable.
	 */
	Expr getVar(String id) {
		Scope scope = curScope();
		if (scope.variables.containsKey(id)) {
			return scope.variables.get(id);
		}
		
		scope.variables.keySet().forEach(v -> {
			System.out.println(v);
		});
		throw new IllegalArgumentException(String.format("Variable \"%s\" not in scope", id));
	}

	Expr getInitialVar(String id) {
		Map<String, Expr> initialVariables = curScope().initialVariables;
		
		if(initialVariables.containsKey(id)) {
			return initialVariables.get(id);
		} else {
			// the given id is not in the initial variables, so either the 
			// expression is being checked in a function call, or the id is 
			// not one of the parameters. In either case, we can just use the 
			// variable as it is in the scope. 
			
			return getVar(id);
		}
	}
	
	Expr getReturnVar() {
		return curScope().returnExpr;
	}

	private Scope curScope() {
		return scopeStack.peek();
	}
	
	/**
	 * Generates and registers a new Z3 const of the given variable.
	 */
	private Expr newVar(String id) {
		int count = idCount.get(id);
		String newId = id + "$" + count;
		
		Expr newConst = makeConst(newId, curScope().types.get(id));
		
		idCount.put(id, count + 1);
		
		curScope().variables.put(id, newConst);
		
		return newConst;
	}
	
	private Expr declareVar(String id, String type) {
		// the fist counter value is 0, the $ symbol is used because it is not in the grammar, guaranteeing no 
		// collisions.
		int count = idCount.getOrDefault(id, 0);
		idCount.put(id, count + 1);
		Expr constExpr = makeConst(id + "$" + count, type);
		
		if (isVarDeclared(id)) {
			// TODO: scopes?
			throw new RuntimeException("Duplicate variable " + id);
		}
		
		curScope().types.put(id, type);
		curScope().variables.put(id, constExpr);
		
		return constExpr;
	}
	
	private void checkExpression(BoolExpr expr, ParserRuleContext ctx, String description) {
		solver.push();
		
		solver.add(c.mkNot(c.mkImplies(curScope().pathCondition, expr)));
		
		if (solver.check() == Status.SATISFIABLE) {
			errors.add(new ProgramError(ctx, description, solver.getModel(), solver.toString()));
		}
		
//		System.out.println("debug " + ctx.getText());
//		System.out.println(solver);
		
		solver.pop();
	}
	
	// statements
	
	@Override
	public Void visitDeclarationStat(DeclarationStatContext ctx) {
		Expr constExpr = declareVar(getId(ctx.ID()), ctx.type().getText());
		
		// assignment is optional
		if(ctx.expression() != null) {
			BoolExpr expr = c.mkEq(constExpr, expr(ctx.expression()));
			solver.add(expr);
		}
		
		return null;
	}
	
	@Override
	public Void visitAssignStat(AssignStatContext ctx) {
		String id = getId(ctx.ID());
		
		// TODO: not sure if it's needed to set it to the old value if the path condition does not hold.
		Expr prevConst = getVar(id);
		Expr newConst = newVar(id);

		BoolExpr eq = c.mkEq(newConst, c.mkITE(currentCond(), expr(ctx.expression()), prevConst));
//		BoolExpr eq = c.mkEq(newConst, expr(ctx.expression()));
		solver.add(eq);
		
		return null;
	}
	
	@Override
	public Void visitReturnStat(ReturnStatContext ctx) {
		scopeStack.push(new Scope(curScope()));
		curScope().returnExpr = expr(ctx.expression());

		FunctionDefStatContext func = curScope().function;
		
		// check ensures contracts
		for (ContractContext ensures : getContracts(func, "ensures")) {
			BoolExpr check = (BoolExpr) expr(ensures.expression());
			checkExpression(check, ctx, "Failed ensures contract " + ensures.getText());
		}
		
		scopeStack.pop();
		
		BoolExpr cond = newPathCond();
		curScope().pathCondition = cond;
		solver.add(c.mkEq(cond, c.mkFalse()));
		
		return null;
	}
	
	/**
	 * @return The set of variable id's that are changed in the scope of the 
	 * given branch, compared to the current scope.
	 */
	private Set<String> getChangedVars(Scope branch, Scope curScope) {
		HashSet<String> result = new HashSet<>();
		
		for (String var : branch.variables.keySet()) {
			if (curScope.variables.containsKey(var)) {
				boolean trueChanged = curScope.variables.get(var) != branch.variables.get(var);
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

		Scope curScope = curScope();
		
		Scope trueScope = new Scope(curScope);
		trueScope.pathCondition = trueCond;
		
		Scope falseScope = new Scope(curScope);
		falseScope.pathCondition = falseCond;
		
		scopeStack.push(trueScope);
		visit(ctx.statement(0));
		scopeStack.pop();
		
		if(ctx.statement().size() > 1) {
			solver.add(c.mkEq(falseCond, c.mkAnd(currentCond(), c.mkNot(trueCond))));
			
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
//		Set<String> curVars = curScope.variables.keySet();
		
		// all variables that are changed in both the if and else branch
		Set<String> union = new HashSet<>();
		union.addAll(trueVars);
		union.retainAll(falseVars);
		
		// all variables that are changed in either the if or the else branch
		Set<String> diff = new HashSet<>();
		diff.addAll(trueVars);
		diff.addAll(falseVars);
		diff.removeAll(union);

//		System.out.println(ctx.expression().getText());
//		System.out.println("Cur vars: " + String.join(", ", curVars));
//		System.out.println("Union vars: " + String.join(", ", union));
//		System.out.println("Diff vars: " + String.join(", ", diff));
		
		// create new variable and set it equal to the values of the branches depending on the path conditions.
		for(String var : union) {
			Expr trueVar = trueScope.variables.get(var);
			Expr falseVar = falseScope.variables.get(var);
			Expr defaultVar = curScope.variables.get(var);
			
			Expr e = c.mkITE(trueCond, trueVar, c.mkITE(falseCond, falseVar, defaultVar));
			solver.add(c.mkEq(newVar(var), e));
		}
		
		for(String var : diff) {
			Expr e;
			Expr defaultVar = curScope.variables.get(var);
			BoolExpr cond;
			
			if(trueVars.contains(var)) {
				e = trueScope.variables.get(var);
				cond = trueCond;
			} else if(falseVars.contains(var)) {
				e = falseScope.variables.get(var);
				cond = falseCond;
			} else {
				throw new RuntimeException(String.format("Variable \"%s\" not found in previous scopes", var));
			}
			
			solver.add(c.mkEq(newVar(var), c.mkITE(cond, e, defaultVar)));
		}
		
		return null;
	}
	
	@Override
	public Void visitContractStat(ContractStatContext ctx) {
		ContractContext con = ctx.contract();
		String type = con.contract_type().getText();
		switch (type) {
		case "assert":
			BoolExpr expr = (BoolExpr) expr(con.expression());
			
			checkExpression(expr, ctx, "Failed assertion " + con.expression().getText());
			break;
		default:
			throw new UnsupportedOperationException("Contract type " + type + " not supported in statements.");
		}
		
		return null;
	}

	private List<ContractContext> getContracts(WhileStatContext ctx, String type) {
	    return ctx.contract().stream()
                .filter(c -> c.contract_type().getText().equals(type))
                .collect(Collectors.toList());
    }
	
	private List<ContractContext> getContracts(FunctionDefStatContext ctx, String type) {
		return ctx.contract().stream()
				.filter((c) -> c.contract_type().getText().equals(type))
				.collect(Collectors.toList());
	}
	
	@Override
	public Void visitFunctionDefStat(FunctionDefStatContext ctx) {
		BoolExpr pathCond = newPathCond();
		solver.add(c.mkEq(pathCond, c.mkTrue()));
		
		Scope scope = new Scope(pathCond);
		scopeStack.push(scope);
		scope.function = ctx;
		
		// declare parameters in current scope
		for (ParameterContext p : ctx.parameter()) {
			Expr expr = declareVar(getId(p.ID()), p.type().getText());
			scope.variables.put(getId(p.ID()), expr);
		}
		
		// add requires contracts
		for (ContractContext c : getContracts(ctx, "requires")) {
			solver.add((BoolExpr) expr(c.expression()));
		}
		
		scope.initialVariables.putAll(scope.variables);
		
		visit(ctx.statement());
		
		scopeStack.pop();
		
		return null;
	}

	@Override
    public Void visitWhileStat(WhileStatContext ctx) {
        //invariants should hold from the beginning
        List<ContractContext> invariants = getContracts(ctx, "invariant");
        for (ContractContext contractContext : invariants) {
            ExpressionContext expr = contractContext.expression();
            BoolExpr z3Expr = (BoolExpr) expr(expr);

            checkExpression(z3Expr, ctx, "Couldn't establish the loop invariant " + expr.getText() + " before entering the loop.");
        }

        // now we assert the loop invariants, and the loop condition.
        // we create a new path condition (we are in the loop now)
        ExpressionContext whileCondition = ctx.expression();
        BoolExpr z3wileCondition = (BoolExpr) expr(whileCondition);
        solver.add(z3wileCondition);

        Scope oldScope = curScope();
        Scope whileBodyScope = new Scope(z3wileCondition);
        whileBodyScope.initialVariables.putAll(oldScope.variables);
        scopeStack.push(whileBodyScope);

        for (String oldVar : oldScope.variables.keySet()) {
            //inside the while body, every variabele has a new identifier
            Expr newVar = newVar(oldVar);
        }

        List<ContractContext> decreases = getContracts(ctx, "decreases");
        List<ArithExpr> decreasesExpressionsInitially = decreases.stream()
                .map(contractContext -> contractContext.expression())
                .map(expressionContext -> (ArithExpr) expr(expressionContext))
                .collect(Collectors.toList());

        // The initial variables in the while body are the variables after renaming the variables to
        // their loopbody-specific name. These will be used to check the decreases contract
        whileBodyScope.initialVariables.putAll(whileBodyScope.variables);

        // assert the loop condition and every invariant
        BoolExpr newWhileCondition = (BoolExpr) expr(whileCondition);
        solver.add(newWhileCondition);

        // assert every contract on the loop (don't check just yet)
        for (ContractContext contractContext : getContracts(ctx, "invariant")) {
            ExpressionContext expr = contractContext.expression();
            BoolExpr z3Expr = (BoolExpr) expr(expr);
            solver.add(z3Expr);
        }

        //now visit the body of the while loop
        StatementContext body = ctx.statement();
        visit(body); //will insert new variables in the scope

        //check the loop invariants with the new smt variables
        for (ContractContext contractContext : invariants) {
            ExpressionContext expr = contractContext.expression();
            BoolExpr z3Expr = (BoolExpr) expr(expr);

            checkExpression(z3Expr, ctx, "Couldn't establish the loop invariant " + expr.getText() + " after a loop body iteration.");
        }
        //check the decreases.
        List<ArithExpr> decreasesExpressionsAfterwards = decreases.stream()
                .map(contractContext -> contractContext.expression())
                .map(expressionContext -> (ArithExpr) expr(expressionContext))
                .collect(Collectors.toList());
        for (int i = 0; i < decreasesExpressionsAfterwards.size(); i++) {
            //check that the new expression is greater than the old one
            ArithExpr newExpr = decreasesExpressionsAfterwards.get(i);
            ArithExpr oldExpr = decreasesExpressionsInitially.get(i);

            BoolExpr newGreaterThanOld = c.mkGt(newExpr, oldExpr);
            checkExpression(newGreaterThanOld, ctx, "Couldn't verify the 'decreases' contract " + decreases.get(i).getText() + ".");
        }

        scopeStack.pop();

        for (String inWhileVar : whileBodyScope.variables.keySet()) {
            //after the while body, every variabele has a new identifier
            Expr afterWhileVar = newVar(inWhileVar);
        }
        //assert ((invariant) && (not condition))
        BoolExpr condition = (BoolExpr) expr(ctx.expression());
        for (ContractContext contractContext : invariants) {
            ExpressionContext expr = contractContext.expression();
            BoolExpr invariantExpr = (BoolExpr) expr(expr);
            BoolExpr terminationExpr = c.mkAnd(invariantExpr, c.mkNot(condition));
            solver.add(terminationExpr);
        }


	    return null;
	    //TODO what if our while body contains an early return?
    }
	
	public Expr genFunctionCallExpr(FunctionCallExprContext ctx) {
		String funcId = ctx.ID().getText();
		FunctionDefStatContext func = functions.get(funcId);
		
		// first, check if the requires contracts hold.
		
		// we make temporary variables here to fill in with the arguments
		// so we push a scope and pop it later so the rest of the program
		// isn't affected.
		scopeStack.push(new Scope(curScope()));
		
		// create argument variables
		List<ParameterContext> parameters = func.parameter();
		for (int i = 0; i < parameters.size() || i < ctx.expression().size(); i++) {
			ParameterContext p = parameters.get(i);
			ExpressionContext e = ctx.expression(i);
			
			String id = getId(p.ID());
			Expr argVar = makeConst("$p" + callCount + "$" + id, p.type().getText());
			solver.add(c.mkEq(argVar, expr(e)));
			curScope().variables.put(id, argVar);
		}
		
		// check requires
		for (ContractContext c : getContracts(func, "requires")) {
			String description = "Failed requires contract " + c.expression().getText();
			checkExpression((BoolExpr) expr(c.expression()), ctx, description);
		}
		
		// check decreases contracts
		if (func == curScope().function) {
			// only check recursive calls
			
			for (ContractContext con : getContracts(func, "decreases")) {
				if (con.expression() instanceof IdExprContext) {
					String id = getId(((IdExprContext) con.expression()).ID());
					
					ArithExpr oldValue = (ArithExpr) curScope().initialVariables.get(id);
					ArithExpr newValue = (ArithExpr) getVar(id);
					
					// check (newValue >= 0) && (newValue < oldValue)
					BoolExpr check = c.mkAnd(c.mkGe(newValue, c.mkInt(0)), c.mkLt(newValue, oldValue));
					checkExpression(check, ctx, "Failed decreases contract on variable " + id);
				} else {
					// this should probably be handled by the parser, but handling 
					// it like this was easier in the short term
					throw new RuntimeException("Cannot assert decreases contract on expressions");
				}
			}
		}
		
		// create result variable
		String resultId = "$result-" + funcId + callCount;
		Expr result = declareVar(resultId, func.return_type().getText());
		curScope().returnExpr = result;
		
		// fill in ensures
		for (ContractContext c : getContracts(func, "ensures")) {
			solver.add((BoolExpr) expr(c.expression()));
		}
		
		scopeStack.pop();
		
		callCount++;
		
		return result;
	}
	
	// program
	
	@Override
	public Void visitProgram(ProgramContext ctx) {
		// find all declared functions
		FunctionFinder finder = new FunctionFinder();
		finder.visit(ctx);
		functions = finder.getFunctions();
		
		// base scope, path condition set to true
		BoolExpr cond = newPathCond();
		solver.add(c.mkEq(c.mkTrue(), cond));
		scopeStack.push(new Scope(cond));
		
		super.visitProgram(ctx);
		
		System.out.println("-- start generated SMT code --");
		System.out.println(solver);
		System.out.println("-- end generated SMT code --");
		
		return null;
	}
	
	// public interface
	
	public boolean isCorrect() {
		return errors.isEmpty();
	}
	
	public List<ProgramError> getErrors() {
		return Collections.unmodifiableList(errors);
	}
	
	/**
	 * Class representing a scope of the program. A scope is added to the 
	 * scope stack with every block statement, if statement, function 
	 * definition etc.
	 */
	private class Scope {
		BoolExpr pathCondition;
		
		/**
		 * All variables in the current scope. Maps variable id to SSA valid 
		 * z3 expression.
		 */
		Map<String, Expr> variables = new HashMap<>();
		
		/**
		 * Variables declared at the start of a function. Used for checking 
		 * 'decreases' contracts.
		 */
		Map<String, Expr> initialVariables = new HashMap<>();
		
		/**
		 * map variable id to its type.
		 */
		private HashMap<String, String> types = new HashMap<>();
		
		private Expr returnExpr;
		
		private FunctionDefStatContext function;
		
		Scope(BoolExpr pathCondition) {
			this.pathCondition = pathCondition;
		}
		
		Scope(Scope lower) {
			this.pathCondition = lower.pathCondition;
			this.returnExpr = lower.returnExpr;
			variables.putAll(lower.variables);
			initialVariables.putAll(lower.initialVariables);
			types.putAll(lower.types);
			this.function = lower.function;
		}
	}
}
