package ss.group3.programverifier.ast;

import java.util.Arrays;
import java.util.List;

public class Assign extends Statement {

    private String identifier;
    private final Expression expression;

    public Assign(String identifier, Expression expression) {
        this.identifier = identifier;
        this.expression = expression;
    }

    public String getIdentifier() {
        return identifier;
    }
    
    public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "ASSIGN{identifier=" + identifier + ",expression=" + expression + "}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Arrays.asList(expression);
    }
}
