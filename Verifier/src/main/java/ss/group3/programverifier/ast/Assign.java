package ss.group3.programverifier.ast;

public class Assign extends Statement {

    private final String identifier;
    private final Expression expression;

    public Assign(String identifier, Expression expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "ASSIGN{identifier=" + identifier + ",expression=" + expression + "}";
    }
}
