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
    enum Access {
        Public,
        Private
    }

    Access access;
    boolean isStatic = false;
    String name;
    VarType varType;
    Object value;

    public Var(Access access, boolean isStatic, String name, Object value, VarType varType) {
        this.name = name;
        this.varType = varType;
        this.value = value;
        this.access = access;
        this.isStatic = isStatic;
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

    public Access getAccess() {
        return access;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public VarType getVarType() {
        return varType;
    }
}
