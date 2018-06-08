package ss.group3.programverifier.smt;

public class SmtTimesExpr extends SmtIntExpr {

    public SmtIntExpr first, second;

    public SmtTimesExpr(SmtIntExpr first, SmtIntExpr second) {
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
        return "(* " + first + " " + second + ")";
    }
}
