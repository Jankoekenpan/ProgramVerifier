package ss.group3.programverifier.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Divide extends Expression {

    private final Expression first, second;

    public Divide(Expression first, Expression second) {
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
        return "DIVIDE{first=" + first + ",second=" + second + "}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Arrays.asList(first, second);
    }
}
