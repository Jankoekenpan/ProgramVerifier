package ss.group3.programverifier.ast;

public class TernaryIf extends Expression {

    private final Expression condition;
    private final Expression thanExpression, elseExpression;

    public TernaryIf(Expression condition, Expression thanExpression, Expression elseExpression) {
        this.condition = condition;
        this.thanExpression = thanExpression;
        this.elseExpression = elseExpression;
    }

    public Expression getThanExpression() {
        return thanExpression;
    }

    public Expression getElseExpression() {
        return elseExpression;
    }

    public Expression getCondition() {
        return condition;
    }

    @Override
    public String toString() {
        return "TERNARY_IF{condition=" + condition + ",thanExpression=" + thanExpression + ",elseExpression=" + elseExpression + "}";
    }

}
