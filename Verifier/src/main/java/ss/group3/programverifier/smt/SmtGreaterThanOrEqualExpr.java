package ss.group3.programverifier.smt;

public class SmtGreaterThanOrEqualExpr extends SmtExpr {

    private final SmtExpr first, second;

    public SmtGreaterThanOrEqualExpr(SmtExpr first, SmtExpr second) {
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
        return "(>= " + first + " " + second + ")";
    }

}
