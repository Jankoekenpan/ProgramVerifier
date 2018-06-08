package ss.group3.programverifier.smt;

public class SmtImpliesExpr extends SmtBoolExpr {

    private final SmtBoolExpr first, second;

    public SmtImpliesExpr(SmtBoolExpr first, SmtBoolExpr second) {
        this.first = first;
        this.second = second;
    }

    public SmtBoolExpr getFirst() {
        return first;
    }

    public SmtBoolExpr getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "(>= " + first + " " + second + ")";
    }
}
