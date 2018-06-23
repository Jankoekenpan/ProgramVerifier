package ss.group3.programverifier.ast;

import java.util.Arrays;
import java.util.List;

public class Not extends Expression {

    private final Expression expression;

    public Not(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "NOT{expression=" + expression + "}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Arrays.asList(expression);
    }
}
