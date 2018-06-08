package ss.group3.programverifier.ast;

public class TernaryIf<TExpr extends Expression> extends Expression {

    private final BooleanExpression condition;
    private final TExpr thanExpression, elseExpression;

    public TernaryIf(BooleanExpression condition, TExpr thanExpression, TExpr elseExpression) {
        this.condition = condition;
        this.thanExpression = thanExpression;
        this.elseExpression = elseExpression;
    }

    public TExpr getThanExpression() {
        return thanExpression;
    }

    public TExpr getElseExpression() {
        return elseExpression;
    }

}
