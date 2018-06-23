package ss.group3.programverifier.ast;

import java.util.Arrays;
import java.util.List;

public class Return extends Statement {

    private final Expression expression;

    public Return() {
        this(null);
    }

    public Return(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public boolean hasExpression() {
        return expression != null;
    }

    @Override
    public String toString() {
        return "RETURN{expression=" + expression + "}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Arrays.asList(expression);
    }
}
