package ss.group3.programverifier.ast;

public class LessThan extends Expression {

    private final Expression first, second;

    public LessThan(Expression first, Expression second) {
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
