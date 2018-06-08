package ss.group3.programverifier.ast;

public class GreaterThanOrEqual extends Expression {

    private final Expression first, second;

    public GreaterThanOrEqual(Expression first, Expression second) {
        this.first = first;
        this.second = second;
    }

    public Expression getFirst() {
        return first;
    }

    public Expression getSecond() {
        return second;
    }
}
