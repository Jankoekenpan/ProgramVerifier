package ss.group3.programverifier.ast;

public class UnaryMinus extends IntExpression {

    private IntExpression expression;

    public UnaryMinus(IntExpression expression) {
        this.expression = expression;
    }

    public IntExpression getExpression() {
        return expression;
    }
}
