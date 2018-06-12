package ss.group3.programverifier.smt;

public class SmtAndExpr extends SmtExpr {

    public SmtExpr first, second;

    public SmtAndExpr(SmtExpr first, SmtExpr second) {
        this.first = first;
        this.second = second;
    }

    public SmtExpr getFirst() {
        return first;
    }

    public SmtExpr getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "(and " + first + " " + second + ")";
    }
}