package ss.group3.programverifier.ast;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Number extends Expression {

    private BigInteger value;

    public Number(BigInteger value) {
        this.value = value;
    }

    public BigInteger getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "NUMBER{value=" + value + "}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Collections.emptyList();
    }
}
