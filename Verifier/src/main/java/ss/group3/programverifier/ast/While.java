package ss.group3.programverifier.ast;

import java.util.Arrays;
import java.util.List;

public class While extends ContractableStatement {

    private final Expression condition;
    private final Statement body;

    public While(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }

    public While(Expression condition, Statement body, Iterable<Contract> contracts) {
        this(condition, body);
        addContracts(contracts);
    }


    public Expression getCondition() {
        return condition;
    }

    public Statement getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "WHILE{condition=" + condition + ",body=" + body + "}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Arrays.asList(condition, body);
    }

}
