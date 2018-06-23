package ss.group3.programverifier.ast;

import java.util.Collections;
import java.util.List;

public class Result extends ContractExpression {

    @Override
    public String toString() {
        return "RESULT";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Collections.emptyList();
    }

}
