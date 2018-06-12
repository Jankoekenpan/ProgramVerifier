package ss.group3.programverifier.smt;

public class SmtIfThenElseExpr extends SmtExpr {

    private final SmtExpr condition;
    private final SmtExpr thanBranch, elseBranch;

    public SmtIfThenElseExpr(SmtExpr condition, SmtExpr thanBranch, SmtExpr elseBranch) {
        this.condition = condition;
        this.thanBranch = thanBranch;
        this.elseBranch = elseBranch;
    }

    public SmtExpr getCondition() {
        return condition;
    }

    public SmtExpr getThanBranch() {
        return thanBranch;
    }

    public SmtExpr getElseBranch() {
        return elseBranch;
    }

    @Override
    public String toString() {
        return "(ite " + condition + " " + thanBranch + " " + elseBranch + ")";
    }

}
