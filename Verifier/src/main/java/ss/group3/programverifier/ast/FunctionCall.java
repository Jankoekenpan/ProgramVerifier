package ss.group3.programverifier.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FunctionCall extends Expression {

    private final String functionIdentifier;
    private final List<Expression> arguments;

    public FunctionCall(String functionIdentifier, List<Expression> arguments) {
        this.functionIdentifier = functionIdentifier;
        this.arguments = Collections.unmodifiableList(arguments);
    }

    public FunctionCall(String functionIdentifier, Expression... arguments) {
        this(functionIdentifier, Arrays.asList(arguments));
    }

    public String getFunctionIdentifier() {
        return functionIdentifier;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "FUNCTION_CALL{functionIdentifier=" + functionIdentifier + ",arguments=" + arguments + "}";
    }
}
