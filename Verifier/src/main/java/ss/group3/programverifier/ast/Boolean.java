package ss.group3.programverifier.ast;

public class Boolean extends BooleanExpression {

    private final boolean value;

    public Boolean(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
