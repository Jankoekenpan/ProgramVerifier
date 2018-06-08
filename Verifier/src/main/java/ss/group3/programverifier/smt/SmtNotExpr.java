package ss.group3.programverifier.smt;

public class SmtNotExpr extends SmtBoolExpr {

    private final SmtBoolExpr boolExpr;

    public SmtNotExpr(SmtBoolExpr boolExpr) {
        this.boolExpr = boolExpr;
    }

    public SmtBoolExpr getBoolExpr() {
        return boolExpr;
    }

    @Override
    public String toString() {
        return "(not " + boolExpr + ")";
    }
}
