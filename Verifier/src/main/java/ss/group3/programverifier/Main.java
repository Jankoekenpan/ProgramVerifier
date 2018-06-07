package ss.group3.programverifier;

import java.io.IOException;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import ss.group3.programverifier.LanguageParser.StatementContext;

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

        //useful methods:
        //solver.getModel(), solver.push(), solver.pop(), solver.reset()
        //to read smt files: solver.fromFile(String fileName)
        //to read smt strings: solver.fromString(smtProgram)s
        
        CharStream input = new ANTLRInputStream(System.in);
		LanguageLexer lexer = new LanguageLexer(input);
		CommonTokenStream stream = new CommonTokenStream(lexer);
		LanguageParser parser = new LanguageParser(stream);
		StatementContext statement = parser.statement();
    }

}
