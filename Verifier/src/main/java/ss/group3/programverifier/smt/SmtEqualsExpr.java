package ss.group3.programverifier.smt;

public class SmtEqualsExpr<T extends SmtExpr> extends SmtBoolExpr {

    private final T first, second;

    public SmtEqualsExpr(T first, T second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public T getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "(= " + first + " " + second + ")";
    }
}
