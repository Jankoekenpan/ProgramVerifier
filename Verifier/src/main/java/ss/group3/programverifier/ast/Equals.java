package ss.group3.programverifier.ast;

public class Equals extends Expression {

    private final Expression first, second;

    public Equals(Expression first, Expression second) {
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
        return "EQUALS{first=" + first + ",second=" + second + "}";
    }
}
