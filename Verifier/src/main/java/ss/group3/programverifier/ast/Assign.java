package ss.group3.programverifier.ast;

public class Assign extends Statement {

    private final Type type;
    private final String identifier;
    private final Expression expression;

    public Assign(String identifier, Expression expression) {
        this(null, identifier, expression);
    }

    public Assign(Type type, String identifier, Expression expression) {
        this.type = type;
        this.identifier = identifier;
        this.expression = expression;
    }

    public boolean hasType() {
        return type != null;
    }

    public Type getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Expression getExpression() {
        return expression;
    }

}
