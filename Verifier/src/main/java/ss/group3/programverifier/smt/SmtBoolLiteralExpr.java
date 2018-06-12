package ss.group3.programverifier.smt;

public class SmtBoolLiteralExpr extends SmtExpr {

    private boolean value;

    public SmtBoolLiteralExpr(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
