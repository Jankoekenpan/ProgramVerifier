package ss.group3.programverifier.ast;

public class Not extends BooleanExpression {

    private final BooleanExpression negatedExpression;

    public Not(BooleanExpression negatedExpression) {
        this.negatedExpression = negatedExpression;
    }

    public BooleanExpression getNegatedExpression() {
        return negatedExpression;
    }
}
