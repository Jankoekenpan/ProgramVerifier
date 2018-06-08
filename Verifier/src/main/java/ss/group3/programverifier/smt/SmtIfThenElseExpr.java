package ss.group3.programverifier.smt;

public class SmtIfThenElseExpr<T extends SmtExpr> extends SmtExpr {

    private final SmtBoolExpr condition;
    private final T thanBranch, elseBranch;

    public SmtIfThenElseExpr(SmtBoolExpr condition, T thanBranch, T elseBranch) {
        this.condition = condition;
        this.thanBranch = thanBranch;
        this.elseBranch = elseBranch;
    }

    public SmtBoolExpr getCondition() {
        return condition;
    }

    public T getThanBranch() {
        return thanBranch;
    }

    public T getElseBranch() {
        return elseBranch;
    }

    @Override
    public String toString() {
        return "(ite " + condition + " " + thanBranch + " " + elseBranch + ")";
    }

}
