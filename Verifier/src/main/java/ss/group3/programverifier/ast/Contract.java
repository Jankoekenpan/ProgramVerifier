package ss.group3.programverifier.ast;

public class Contract extends Statement {

    private ContractKind contractType;
    private final Expression expression;

    public Contract(ContractKind contractType, Expression expression) {
        this.contractType = contractType;
        this.expression = expression;
    }

    public ContractKind getContractType() {
        return contractType;
    }

    public Expression getExpression() {
        return expression;
    }
}
