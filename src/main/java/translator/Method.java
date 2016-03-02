package translator;

import java.util.List;

/**
 * Created by Josh on 3/03/2016.
 */
public class Method {
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
