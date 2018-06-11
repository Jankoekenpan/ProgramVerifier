package ss.group3.programverifier.ast;

public class Plus extends Expression {

    private final Expression first, second;

    public Plus(Expression first, Expression second) {
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
        return "PLUS{first=" + first + ",second=" + second + "}";
    }
}
