package ss.group3.programverifier.ast;

public class Times extends IntExpression {

    private final IntExpression first;
    private final IntExpression second;

    public Times(IntExpression first, IntExpression second) {
        this.first = first;
        this.second = second;
    }

    public IntExpression getFirst() {
        return first;
    }

    public IntExpression getSecond() {
        return second;
    }
}
