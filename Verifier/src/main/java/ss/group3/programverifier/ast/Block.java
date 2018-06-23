package ss.group3.programverifier.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Block extends Statement {

    private final List<Statement> statements;

    public Block(List<Statement> statements) {
        this.statements = Collections.unmodifiableList(statements);
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        return "BLOCK{statements="+statements+"}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Collections.unmodifiableList(statements);
    }
}
