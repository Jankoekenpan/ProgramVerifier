package ss.group3.programverifier.ast;

public class Implies extends BooleanExpression {

    private final BooleanExpression first;
    private final BooleanExpression second;

    public Implies(BooleanExpression first, BooleanExpression second) {
        this.first = first;
        this.second = second;
    }

    public BooleanExpression getFirst() {
        return first;
    }

    public BooleanExpression getSecond() {
        return second;
    }
}
