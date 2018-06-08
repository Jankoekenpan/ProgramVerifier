package ss.group3.programverifier.ast;

public class NotEquals<TExpr extends Expression> extends BooleanExpression {

    private final TExpr first;
    private final TExpr second;

    public NotEquals(TExpr first, TExpr second) {
        this.first = first;
        this.second = second;
    }

    public TExpr getFirst() {
        return first;
    }

    public TExpr getSecond() {
        return second;
    }
}
