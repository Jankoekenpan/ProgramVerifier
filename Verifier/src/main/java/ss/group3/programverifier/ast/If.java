package ss.group3.programverifier.ast;

import java.util.Arrays;
import java.util.List;

public class If extends Statement {

    private final Expression condition;
    private final Statement thenBranch, elseBranch;

    public If(Expression condition, Statement thanBranch) {
        this(condition, thanBranch, null);
    }

    public If(Expression condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public boolean hasElseBranch() {
        return elseBranch != null;
    }

    public Expression getCondition() {
        return condition;
    }

    public Statement getThenBranch() {
        return thenBranch;
    }

    public Statement getElseBranch() {
        return elseBranch;
    }
    @Override
    public String toString() {
        return "IF{condition=" + condition + ",thanBranch=" + thenBranch + ",elseBranch="+elseBranch+"}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Arrays.asList(condition, thenBranch, elseBranch);
    }

}
