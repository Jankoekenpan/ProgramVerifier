package ss.group3.programverifier.ast;

public class Or extends Expression {

    private final Expression first, second;

    public Or(Expression first, Expression second) {
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
        return "OR{first=" + first + ",second=" + second + "}";
    }
}
