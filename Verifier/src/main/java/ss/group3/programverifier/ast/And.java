package ss.group3.programverifier.ast;

public class And extends Expression {
    private final Expression first, second;

    public And(Expression first, Expression second) {
        this.first = first;
        this.second = second;
    }

    public Expression getFirst() {
        return first;
    }

    public Expression getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "AND{first=" + first + ",second=" + second + "}";
    }
}
