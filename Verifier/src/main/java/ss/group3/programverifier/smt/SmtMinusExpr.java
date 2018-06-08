package ss.group3.programverifier.smt;

public class SmtMinusExpr extends SmtIntExpr {

    public SmtIntExpr first, second;

    public SmtMinusExpr(SmtIntExpr first, SmtIntExpr second) {
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
        return "(- " + first + " " + second + ")";
    }
}
