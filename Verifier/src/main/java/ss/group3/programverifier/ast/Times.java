package ss.group3.programverifier.ast;

import java.util.Arrays;
import java.util.List;

public class Times extends Expression {

    private final Expression first, second;

    public Times(Expression first, Expression second) {
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
        return "TIMES{first=" + first + ",second=" + second + "}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Arrays.asList(first, second);
    }
}
