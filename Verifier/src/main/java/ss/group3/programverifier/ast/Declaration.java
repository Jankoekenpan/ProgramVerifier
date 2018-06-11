package ss.group3.programverifier.ast;

public class Declaration extends Statement {

    private final Type type;
    private final String identifier;
    private final Expression expression;

    public Declaration(Type type, String identifier) {
        this(type, identifier, null);
    }

    public Declaration(Type type, String identifier, Expression expression) {
        this.type = type;
        this.identifier = identifier;
        this.expression = expression;
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

    public boolean hasExpression() {
        return expression != null;
    }

    @Override
    public String toString() {
        return "DECLARATION{type="+type+",identifier="+identifier+",expression="+expression+"}";
    }
}
