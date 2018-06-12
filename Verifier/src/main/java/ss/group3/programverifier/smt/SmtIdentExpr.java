package ss.group3.programverifier.smt;

public class SmtIdentExpr extends SmtExpr {

    private final String identifier;

    public SmtIdentExpr(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
