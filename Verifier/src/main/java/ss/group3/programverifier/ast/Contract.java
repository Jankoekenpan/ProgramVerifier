package ss.group3.programverifier.ast;

import java.util.Arrays;
import java.util.List;

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

    @Override
    public String toString() {
        return "CONTRACT{contractType="+contractType+",expression="+expression+"}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Arrays.asList(expression);
    }
}
