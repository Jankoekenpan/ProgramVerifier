package ss.group3.programverifier.ast;

public class If extends Statement {

    private final BooleanExpression condition;
    private final Statement thanBranch, elseBranch;

    public If(BooleanExpression condition, Statement thanBranch) {
        this(condition, thanBranch, null);
    }

    public If(BooleanExpression condition, Statement thanBranch, Statement elseBranch) {
        this.condition = condition;
        this.thanBranch = thanBranch;
        this.elseBranch = elseBranch;
    }

    public boolean hasElseBranch() {
        return elseBranch != null;
    }

    public BooleanExpression getCondition() {
        return condition;
    }

    public Statement getThanBranch() {
        return thanBranch;
    }

    public Statement getElseBranch() {
        return elseBranch;
    }
}
