package ss.group3.programverifier.ast;

public class Return extends Statement {

    private final Expression expression;

    public Return(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
