package ss.group3.programverifier.smt;

public class SmtPush extends SmtStatement {

    public static final SmtPush INSTANCE = new SmtPush();

    private SmtPush() {}

    @Override
    public String toString() {
        return "(push)";
    }
}
