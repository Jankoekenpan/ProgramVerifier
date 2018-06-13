package ss.group3.programverifier;

import com.microsoft.z3.*;

import java.io.IOException;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.TokenStream;

public class Main {

    public static void main(String[] args) throws IOException {
        Context context = new Context();

        //create the solver instance
        Solver solver = context.mkSolver();

        //create expressions using context.mk<Expression>
        BoolExpr boolExpr = context.mkBool(true);

        //add the assumption to the solver
        solver.add(boolExpr);

        //do the check
        Status status = solver.check(); //satisfiable, unknown or unsatisfiable
        System.out.println("status = " + status);

        System.out.println("--- max.hello translation ---");
        maxTranslationAttempt1();
        maxTranslationAttempt2();
        System.out.println("--- max.hello translation ---");

        //useful methods:
        //solver.getModel(), solver.push(), solver.pop(), solver.reset()
        //to read smt files: solver.fromFile(String fileName)
        //to read smt strings: solver.fromString(smtProgram)s

        CharStream input = new ANTLRInputStream(System.in); //ANTLRInputStream("hello");
		LanguageLexer lexer = new LanguageLexer(input);
		TokenStream stream = new CommonTokenStream(lexer);
		LanguageParser parser = new LanguageParser(stream);

		//parse tree
		LanguageParser.ProgramContext programTree = parser.program();
    }

    private static void maxTranslationAttempt2() {
        Context context = new Context();
        Solver solver = context.mkSolver();

        BoolExpr c_0 = context.mkBoolConst("c_0");
        IntExpr x_0 = context.mkIntConst("x_0");
        IntExpr y_0 = context.mkIntConst("y_0");
        IntExpr result_0 = context.mkIntConst("result_0");

        BoolExpr c_1 = context.mkGt(x_0, y_0);
        IntExpr result_1 = (IntExpr) context.mkITE(c_1, x_0, result_0);

        BoolExpr c_2 = context.mkNot(c_1);
        IntExpr result_2 = (IntExpr) context.mkITE(c_2, y_0, result_0);

        IntExpr result_3 = (IntExpr) context.mkITE(c_1, result_1, context.mkITE(c_2, result_2, result_0));


        solver.add(c_0, c_1, c_2);

        solver.push();
        BoolExpr contract1 = context.mkNot(context.mkImplies(context.mkGt(x_0, y_0), context.mkEq(result_3, x_0)));
        solver.add(contract1);
        Status status = solver.check();
        System.out.println("status = " + status);
        solver.pop();

        solver.push();
        BoolExpr contract2 = context.mkNot(context.mkImplies(context.mkLe(x_0, y_0), context.mkEq(result_3, y_0)));
        solver.add(contract2);
        status = solver.check();
        System.out.println("status = " + status);

        solver.pop();

        context.close();
    }

    private static void maxTranslationAttempt1() {
        Context context = new Context();
        Solver solver = context.mkSolver();

        FuncDecl c_0 = context.mkFuncDecl("c_0", new Sort[0], context.getBoolSort());
        FuncDecl x_0 = context.mkFuncDecl("x_0", new Sort[0], context.getIntSort());
        FuncDecl y_0 = context.mkFuncDecl("y_0", new Sort[0], context.getIntSort());

        FuncDecl result_0 = context.mkFuncDecl("result_0", new Sort[0], context.getIntSort());

        FuncDecl c_1 = context.mkFuncDecl("c_1", new Sort[0], context.getBoolSort());
        BoolExpr c_1_assertion = context.mkEq(c_1.apply(),
                context.mkGt((IntExpr) x_0.apply(), (IntExpr) y_0.apply()));

        FuncDecl result_1 = context.mkFuncDecl("result_1", new Sort[0], context.getIntSort());
        Expr result_1_assertion = context.mkEq(result_1.apply(),
                context.mkITE(c_1_assertion, x_0.apply(), result_0.apply()));

        FuncDecl c_2 = context.mkFuncDecl("c_2", new Sort[0], context.getBoolSort());
        BoolExpr c_2_assertion = context.mkEq(c_2.apply(),
                context.mkNot(c_1_assertion));

        FuncDecl result_2 = context.mkFuncDecl("result_2", new Sort[0], context.getIntSort());
        Expr result_2_assertion = context.mkEq(result_2.apply(),
                context.mkITE(c_2_assertion, y_0.apply(), result_0.apply()));

        FuncDecl result_3 = context.mkFuncDecl("result_3", new Sort[0], context.getIntSort());
        Expr result_3_assertion = context.mkEq(result_3.apply(),
                context.mkITE(c_1_assertion, result_1.apply(),
                        context.mkITE(c_2_assertion, result_2.apply(), result_0.apply())));

        solver.add((BoolExpr) c_0.apply());
        solver.add((BoolExpr) c_1.apply());
        solver.add((BoolExpr) c_2.apply());

        solver.push();
        BoolExpr contract1 = context.mkNot(
                context.mkImplies(context.mkGt((IntExpr) x_0.apply(), (IntExpr) y_0.apply()),
                context.mkEq(result_3.apply(), x_0.apply())));
        solver.add(contract1);
        Status status = solver.check();
        System.out.println("status = " + status);
        solver.pop();

        solver.push();
        BoolExpr contract2 = context.mkNot(
                context.mkImplies(context.mkLe((IntExpr) x_0.apply(), (IntExpr) y_0.apply()),
                        context.mkEq(result_3.apply(), y_0.apply())));
        solver.add(contract2);
        status = solver.check();
        System.out.println("status = " + status);
        solver.pop();


        context.close();
    }

}
