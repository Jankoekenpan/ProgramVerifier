package ss.group3.programverifier;

import org.antlr.v4.runtime.tree.ParseTree;

import com.microsoft.z3.Model;

public class ProgramError {
	private ParseTree source;
	private Model model;
	
	public ProgramError(ParseTree source, Model model) {
		this.source = source;
		this.model = model;
	}
	
	public ParseTree getSource() {
		return source;
	}
	
	public Model getModel() {
		return model;
	}
}
