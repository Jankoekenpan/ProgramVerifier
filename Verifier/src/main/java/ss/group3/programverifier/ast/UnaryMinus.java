package ss.group3.programverifier.ast;

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
}
