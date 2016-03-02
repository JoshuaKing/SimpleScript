package translator;

/**
 * Created by josking on 3/2/16.
 */
public class Var {
    enum VarType {
        Integer,
        String,
        Float
    }

    String name;
    VarType varType;
    Object value;

    public Var(String name, Object value, VarType varType) {
        this.name = name;
        this.varType = varType;
        this.value = value;
    }

    public VarType getType() {
        return varType;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
