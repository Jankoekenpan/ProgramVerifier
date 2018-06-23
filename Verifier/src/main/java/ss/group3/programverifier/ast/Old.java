package ss.group3.programverifier.ast;

import java.util.Collections;
import java.util.List;

public class Old extends ContractExpression {

    private final String identifier;

    public Old(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return "OLD{identifier=" + identifier + "}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Collections.emptyList();
    }
}
