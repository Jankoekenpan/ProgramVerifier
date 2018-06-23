package ss.group3.programverifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ss.group3.programverifier.ast.Assign;
import ss.group3.programverifier.ast.AstNode;
import ss.group3.programverifier.ast.Expression;
import ss.group3.programverifier.ast.Identifier;
import ss.group3.programverifier.ast.If;

public class SSATranslator {
	private HashMap<String, Integer> idCount = new HashMap<>();
	private HashMap<String, String> renaming = new HashMap<>();
	
	private List<Expression> currentConditions = new ArrayList<>();
	private boolean currentlyThen; //if were visiting a then branch now, in a else branch otherwise
	
	private void toSSA(AstNode node) {
		if (node instanceof Assign) {
			Assign assign = (Assign) node;
			
			toSSA(assign.getExpression());
			
			String identifier = assign.getIdentifier();
			int count = idCount.getOrDefault(identifier, 0);
			
			String newId = identifier + "$" + count;
			
			idCount.put(identifier, count + 1);
			renaming.put(identifier, newId);
			
			assign.setIdentifier(newId);
		} else if (node instanceof Identifier) {
			Identifier identifier = (Identifier) node;
			String old = identifier.getValue();
			identifier.setValue(renaming.getOrDefault(old, old));
		} else if (node instanceof If) {
			If ifStat = (If) node;
			
			toSSA(ifStat.getCondition());
			
			currentConditions.add(ifStat.getCondition());
			currentlyThen = true;
			
			toSSA(ifStat.getThenBranch());
			
			currentlyThen = false;
			
			toSSA(ifStat.getElseBranch());
			
			currentConditions.remove(currentConditions.size() - 1);
		} else {
			for (AstNode child : node.getChildren()) {
				toSSA(child);
			}
		}
	}
}
