package ss.group3.programverifier.ast;

import java.util.HashMap;
import java.util.Map;

public enum ContractKind {

    REQUIRES("requires"),
    ENSURES("ensures"),
    INVARIANT("invariant");

    private static final Map<String, ContractKind> byName = new HashMap<>();
    static {
        for (ContractKind kind : ContractKind.values()) {
            byName.put(kind.name, kind);
        }
    }

    private final String name;

    ContractKind(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ContractKind getByName(String name) {
        ContractKind kind = byName.get(name);
        if (kind == null) {
            throw new IllegalArgumentException(name + " is not a contract kind.");
        } else {
            return kind;
        }
    }
}
