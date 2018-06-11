package ss.group3.programverifier.ast;

public class Implies extends Expression {

    private final Expression first, second;

    public Implies(Expression first, Expression second) {
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
        return "IMPLIES{first=" + first + ",second=" + second + "}";
    }
}
