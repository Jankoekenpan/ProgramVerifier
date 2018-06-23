package ss.group3.programverifier.ast;

import java.util.Collections;
import java.util.List;

public class Identifier extends Expression {

    private String value;

    public Identifier(String value) {
        this.value = value;
    }
    
    public void setValue(String value) {
		this.value = value;
	}
    
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "IDENTIFIER{value=" + value + "}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Collections.emptyList();
    }
}
