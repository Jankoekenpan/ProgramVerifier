package ss.group3.programverifier.ast;

import java.util.Arrays;
import java.util.List;

public class TernaryIf extends Expression {

    private final Expression condition;
    private final Expression thenExpression, elseExpression;

    public TernaryIf(Expression condition, Expression thenExpression, Expression elseExpression) {
        this.condition = condition;
        this.thenExpression = thenExpression;
        this.elseExpression = elseExpression;
    }

    public Expression getThenExpression() {
        return thenExpression;
    }

    public Expression getElseExpression() {
        return elseExpression;
    }

    public Expression getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "TERNARY_IF{condition=" + condition + ",thanExpression=" + thenExpression + ",elseExpression=" + elseExpression + "}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Arrays.asList(condition, thenExpression, elseExpression);
    }

}
