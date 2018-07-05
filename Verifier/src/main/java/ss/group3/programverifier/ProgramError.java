package ss.group3.programverifier;

import org.antlr.v4.runtime.ParserRuleContext;

import com.microsoft.z3.Model;

public class ProgramError {
	private ParserRuleContext source;
	private Model model;
	private String smt;
	private String description;
	
	public ProgramError(ParserRuleContext source, String description, Model model, String smt) {
		this.source = source;
		this.description = description;
		this.model = model;
		this.smt = smt;
	}
	
	public ParserRuleContext getSource() {
		return source;
	}
	
	public Model getModel() {
		return model;
	}
	
	public String getSmt() {
		return smt;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		int line = source.start.getLine();
		builder.append("Error on line " + line + ", expression '" + source.getText() + "'");
		builder.append("\n");
		builder.append(description);
		builder.append("\n\nSMT file up to assertion:\n");
		builder.append(smt);
		builder.append("\nSMT model of failure:\n");
		builder.append(model);
		builder.append("\n");
		
		return builder.toString();
	}
}
