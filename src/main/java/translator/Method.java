package translator;

import java.util.List;

/**
 * Created by Josh on 3/03/2016.
 */
public class Method {
    public Variable.VarType getReturnType() {
        return returnType;
    }

    public List<Variable> getArguments() {
        return arguments;
    }

    Variable.VarType returnType;
    String name;
    List<Variable> arguments;

    public Method(Variable.VarType returnType, String name, List<Variable> arguments) {
        this.arguments = arguments;
        this.returnType = returnType;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
