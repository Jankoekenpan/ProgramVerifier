package ss.group3.programverifier.smt;

public class SmtPop extends SmtStatement {

    public static final SmtPop INSTANCE = new SmtPop();

    private SmtPop() {}

    @Override
    public String toString() {
        return "(pop)";
    }
}
