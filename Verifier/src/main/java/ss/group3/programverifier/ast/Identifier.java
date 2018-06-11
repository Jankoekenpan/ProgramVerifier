package ss.group3.programverifier.ast;

public class Identifier extends Expression {

    private final String name;

    public Identifier(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "IDENTIFIER{name=" + name + "}";
    }
}
