package ss.group3.programverifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class SymbolTable {

    private final Stack<Scope> scopes = new Stack<>();

    public void openScope() {
        scopes.push(new Scope());
    }

    public void closeScope() {
        scopes.pop();
    }

    public boolean add(String identifier, String type) {
        return scopes.peek().add(identifier, type);
    }

    public static class Scope {
        private Map<String, String> identifiersAndTypes = new HashMap<>();

        public boolean add(String identifier, String type) {
            return identifiersAndTypes.putIfAbsent(identifier, type) == null;
        }
    }
}
