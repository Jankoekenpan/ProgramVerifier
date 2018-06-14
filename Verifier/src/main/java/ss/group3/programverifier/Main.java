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

}
