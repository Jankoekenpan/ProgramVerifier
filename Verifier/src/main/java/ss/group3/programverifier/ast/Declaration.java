package ss.group3.programverifier.ast;

public class Declaration extends Statement {

    private final Type type;
    private final String identifier;

    public Declaration(Type type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public Type getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }
}
