package ss.group3.programverifier.smt;

public class SmtCheckSat extends SmtStatement {

    public static final SmtCheckSat INSTANCE = new SmtCheckSat();

    private SmtCheckSat() {}

    @Override
    public String toString() {
        return "(check-sat)";
    }
}
