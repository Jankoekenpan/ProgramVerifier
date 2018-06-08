package ss.group3.programverifier.ast;

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

}
