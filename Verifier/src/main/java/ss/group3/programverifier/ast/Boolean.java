package ss.group3.programverifier.ast;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Boolean extends Expression {

    private final boolean value;

    public Boolean(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "BOOLEAN{value="+value+"}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Collections.emptyList();
    }
}
