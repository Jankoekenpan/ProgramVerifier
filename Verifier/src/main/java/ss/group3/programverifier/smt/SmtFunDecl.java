package ss.group3.programverifier.smt;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SmtFunDecl extends SmtStatement {

    private final String name;
    private final List<SmtType> parameterTypes;
    private final SmtType returnType;

    public SmtFunDecl(String name, SmtType returnType) {
        this(name, Collections.emptyList(), returnType);
    }

    public SmtFunDecl(String name, List<SmtType> parameterTypes, SmtType returnType) {
        this.name = name;
        this.parameterTypes = Collections.unmodifiableList(parameterTypes);
        this.returnType = returnType;
    }

    public String getName() {
        return name;
    }

    public List<SmtType> getParameterTypes() {
        return parameterTypes;
    }

    public SmtType getReturnType() {
        return returnType;
    }

    @Override
    public String toString() {
        return "(declare-fun (" + parameterTypes.stream().map(SmtType::toString).collect(Collectors.joining(" ")) + ") " + returnType + ")";
    }
}
