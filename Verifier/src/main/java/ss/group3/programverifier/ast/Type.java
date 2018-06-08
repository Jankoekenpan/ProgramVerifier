package ss.group3.programverifier.ast;

import java.util.HashMap;
import java.util.Map;

public enum Type {

    INT("int"),
    BOOLEAN("boolean"),
    VOID("void");

    private static final Map<String, Type> byName = new HashMap<>();
    static {
        for (Type type : Type.values()) {
            byName.put(type.name, type);
        }
    }

    private final String name;

    Type(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Type getByName(String name) {
        Type type = byName.get(name);
        if (type == null) {
            throw new IllegalArgumentException(name + " is not a type.");
        } else {
            return type;
        }
    }
}
