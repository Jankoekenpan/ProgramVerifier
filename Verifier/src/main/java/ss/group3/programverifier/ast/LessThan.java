package ss.group3.programverifier.ast;

import java.util.Arrays;
import java.util.List;

public class LessThan extends Expression {

    private final Expression first, second;

    public LessThan(Expression first, Expression second) {
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
        return "LESS_THAN{first=" + first + ",second=" + second + "}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Arrays.asList(first, second);
    }
}
