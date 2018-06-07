package ss.group3.programverifier;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

public class Main {

    public static void main(String[] args) {
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
    }

}
