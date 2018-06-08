package ss.group3.programverifier.smt;

public class SmtAssert extends SmtStatement {

    private final SmtBoolExpr boolExpr;

    public SmtAssert(SmtBoolExpr boolExpr) {
        this.boolExpr = boolExpr;
    }

    public SmtBoolExpr getBoolExpr() {
        return boolExpr;
    }

    @Override
    public String toString() {
        return "(assert " + boolExpr + ")";
    }
}
