package ss.group3.programverifier.ast;

import java.util.Collections;
import java.util.List;

public class Program extends AstNode {

    private final List<Statement> contents;

    public Program(List<Statement> contents) {
        this.contents = Collections.unmodifiableList(contents);
    }

    public List<Statement> getContents() {
        return contents;
    }

    @Override
    public String toString() {
        return "PROGRAM{statements=" + contents + "}";
    }
    
    @Override
    public List<AstNode> getChildren() {
    	return Collections.unmodifiableList(contents);
    }
}
