package ss.group3.programverifier.ast;

public class Identifier extends Expression {

    private final String value;

    public Identifier(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "IDENTIFIER{value=" + value + "}";
    }
}
