package ss.group3.programverifier.ast;

public class Contract extends Statement {

    private ContractKind contractType;
    private final BooleanExpression expression;

    public Contract(ContractKind contractType, BooleanExpression expression) {
        this.contractType = contractType;
        this.expression = expression;
    }

    public ContractKind getContractType() {
        return contractType;
    }

    public BooleanExpression getExpression() {
        return expression;
    }
}
