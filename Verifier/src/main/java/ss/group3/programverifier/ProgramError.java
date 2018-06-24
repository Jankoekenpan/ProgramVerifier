package ss.group3.programverifier;

import org.antlr.v4.runtime.tree.ParseTree;

import com.microsoft.z3.Model;

public class ProgramError {
	private ParseTree source;
	private Model model;
	private String smt;
	
	public ProgramError(ParseTree source, Model model, String smt) {
		this.source = source;
		this.model = model;
		this.smt = smt;
	}
	
	public ParseTree getSource() {
		return source;
	}
	
	public Model getModel() {
		return model;
	}
	
	public String getSmt() {
		return smt;
	}
}
