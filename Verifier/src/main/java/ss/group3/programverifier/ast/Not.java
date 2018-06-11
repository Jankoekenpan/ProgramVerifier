package ss.group3.programverifier.ast;

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
}
