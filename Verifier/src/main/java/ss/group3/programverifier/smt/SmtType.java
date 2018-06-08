package ss.group3.programverifier.smt;

public enum SmtType {

    INT("Int"),
    BOOL("Bool");
    //TODO BitVector? I would prefer our language uses unbounded ints though.

    private final String name;

    SmtType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
