package ss.group3.programverifier.smt;

import java.math.BigInteger;

public class SmtIntLiteralExpr extends SmtIntExpr {

    private final BigInteger value;

    public SmtIntLiteralExpr(BigInteger value) {
        this.value = value;
    }

    public BigInteger getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
