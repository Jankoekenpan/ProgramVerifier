package ss.group3.programverifier.ast;

public class GreaterThan extends Expression {

    private final Expression first, second;

    public GreaterThan(Expression first, Expression second) {
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
