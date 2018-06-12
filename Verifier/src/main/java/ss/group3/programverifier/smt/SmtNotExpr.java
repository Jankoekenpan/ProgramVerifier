package ss.group3.programverifier.smt;

public class SmtNotExpr extends SmtExpr {

    private final SmtExpr boolExpr;

    public SmtNotExpr(SmtExpr boolExpr) {
        this.boolExpr = boolExpr;
    }

    public SmtExpr getBoolExpr() {
        return boolExpr;
    }

    @Override
    public String toString() {
        return "(not " + boolExpr + ")";
    }
}
