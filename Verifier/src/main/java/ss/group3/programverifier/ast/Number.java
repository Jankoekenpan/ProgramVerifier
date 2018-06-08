package ss.group3.programverifier.ast;

import java.math.BigInteger;

public class Number extends IntExpression {

    private BigInteger value;

    public Number(BigInteger value) {
        this.value = value;
    }

    public BigInteger getValue() {
        return value;
    }
}
