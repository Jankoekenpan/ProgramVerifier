package ss.group3.programverifier.ast;

public class Not extends Expression {

    private final Expression negatedExpression;

    public Not(Expression negatedExpression) {
        this.negatedExpression = negatedExpression;
    }

    public Expression getNegatedExpression() {
        return negatedExpression;
    }
}
