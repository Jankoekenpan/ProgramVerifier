package ss.group3.programverifier;

import java.util.HashMap;
import java.util.Map;

import ss.group3.programverifier.LanguageParser.FunctionDefStatContext;

public class FunctionFinder extends LanguageBaseVisitor<Map<String, FunctionDefStatContext>> {
	private Map<String, FunctionDefStatContext> functions = new HashMap<>();
	
	@Override
	public Map<String, FunctionDefStatContext> visitFunctionDefStat(FunctionDefStatContext ctx) {
		String id = ctx.ID().getText();
		
		if(functions.containsKey(id)) {
			throw new RuntimeException("Duplicate function declaration \"" + id + "\"");
		}
		
		functions.put(id, ctx);
		
		visitChildren(ctx);
		
		return functions;
	}
	
	public Map<String, FunctionDefStatContext> getFunctions() {
		return functions;
	}
}
