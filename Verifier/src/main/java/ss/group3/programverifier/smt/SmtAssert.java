package ss.group3.programverifier.smt;

public class SmtAssert extends SmtStatement {

    private final SmtExpr boolExpr;

    public SmtAssert(SmtExpr boolExpr) {
        this.boolExpr = boolExpr;
    }

    public SmtExpr getBoolExpr() {
        return boolExpr;
    }

    @Override
    public String toString() {
        return "(assert " + boolExpr + ")";
    }
}
