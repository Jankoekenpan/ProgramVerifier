package ss.group3.programverifier.ast;

public class Minus extends Expression {

    private final Expression first, second;

    public Minus(Expression first, Expression second) {
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
        return "MINUS{first=" + first + ",second=" + second + "}";
    }

}
