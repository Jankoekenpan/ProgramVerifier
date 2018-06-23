package ss.group3.programverifier.ast;

import java.util.Arrays;
import java.util.List;

public class GreaterThanOrEqual extends Expression {

    private final Expression first, second;

    public GreaterThanOrEqual(Expression first, Expression second) {
        this.first = first;
        this.second = second;
    }

    public Expression getFirst() {
        return first;
    }

    public Expression getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "GREATER_THAN_OR_EQUAL{first=" + first + ",second=" + second + "}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Arrays.asList(first, second);
    }
}
