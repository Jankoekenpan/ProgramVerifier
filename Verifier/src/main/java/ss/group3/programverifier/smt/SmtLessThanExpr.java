package ss.group3.programverifier.smt;

public class SmtLessThanExpr extends SmtBoolExpr {

    private final SmtIntExpr first, second;

    public SmtLessThanExpr(SmtIntExpr first, SmtIntExpr second) {
        this.first = first;
        this.second = second;
    }

    public SmtIntExpr getFirst() {
        return first;
    }

    public SmtIntExpr getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "(< " + first + " " + second + ")";
    }
}
