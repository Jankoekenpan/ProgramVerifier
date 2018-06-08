package ss.group3.programverifier.ast;

public class While extends Statement {

    private final BooleanExpression condition;
    private final Statement body;


    public While(BooleanExpression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }

    public BooleanExpression getCondition() {
        return condition;
    }

    public Statement getBody() {
        return body;
    }

}
