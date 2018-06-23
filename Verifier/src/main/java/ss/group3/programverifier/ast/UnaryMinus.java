package ss.group3.programverifier.ast;

import java.util.Arrays;
import java.util.List;

public class UnaryMinus extends Expression {

    private Expression expression;

    public UnaryMinus(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "UNARY_MINUS{expression=" + expression + "}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Arrays.asList(expression);
    }
}
