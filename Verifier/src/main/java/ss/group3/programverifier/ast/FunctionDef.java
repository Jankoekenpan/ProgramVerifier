package ss.group3.programverifier.ast;

import ss.group3.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FunctionDef extends Statement {

    private final String identifier;
    private final Type returnType;
    private final Statement body;

    private final List<Type> parameterTypes;
    private final List<String> parameterIdentifiers;
    private final List<Pair<Type, String>> parameterPairs;

    public FunctionDef(String identifier, Type returnType, Statement body) {
        this.identifier = identifier;
        this.returnType = returnType;
        this.body = body;
        this.parameterTypes = Collections.emptyList();
        this.parameterIdentifiers = Collections.emptyList();
        this.parameterPairs = Collections.emptyList();
    }

    public FunctionDef(String identifier, Type returnType, Statement body, List<Pair<Type, String>> parameterPairs) {
        this.identifier = identifier;
        this.returnType = returnType;
        this.body = body;
        this.parameterPairs = Collections.unmodifiableList(parameterPairs);

        List<Type> types = new ArrayList<>(parameterPairs.size());
        List<String> identifiers = new ArrayList<>(parameterPairs.size());
        for (Pair<Type, String> pair : parameterPairs) {
            types.add(pair.getFirst());
            identifiers.add(pair.getSecond());
        }
        this.parameterTypes = Collections.unmodifiableList(types);
        this.parameterIdentifiers = Collections.unmodifiableList(identifiers);
    }

    public FunctionDef(String identifier, Type returnType, Statement body, List<Type> parameterTypes, List<String> parameterIdentifiers) {
        assert parameterTypes.size() == parameterIdentifiers.size() : "parameter types and parameter identifires must have equal length!";

        this.identifier = identifier;
        this.returnType = returnType;
        this.body = body;
        this.parameterTypes = Collections.unmodifiableList(parameterTypes);
        this.parameterIdentifiers = Collections.unmodifiableList(parameterIdentifiers);

        List<Pair<Type, String>> pairs = new ArrayList<>(parameterTypes.size());
        Iterator<Type> typeIterator = parameterTypes.iterator();
        Iterator<String> identifierIterator = parameterIdentifiers.iterator();
        while (typeIterator.hasNext()) {
            pairs.add(new Pair<>(typeIterator.next(), identifierIterator.next()));
        }
        this.parameterPairs = Collections.unmodifiableList(pairs);
    }

    public String getIdentifier() {
        return identifier;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<String> getParameterIdentifiers() {
        return parameterIdentifiers;
    }

    public List<Type> getParameterTypes() {
        return parameterTypes;
    }

    public List<Pair<Type, String>> getParameterPairs() {
        return parameterPairs;
    }

    public Statement getBody() {
        return body;
    }
}
