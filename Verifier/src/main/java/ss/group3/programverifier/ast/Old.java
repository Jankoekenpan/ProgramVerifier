package ss.group3.programverifier.ast;

public class Old extends ContractExpression {

    private final String identifier;

    public Old(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
