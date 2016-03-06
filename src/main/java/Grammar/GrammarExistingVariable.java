package Grammar;

import classes.Expression;
import classes.Variable;
import compiler.VariableTable;

/**
 * Created by josking on 3/4/16.
 */
public class GrammarExistingVariable extends GrammarRule<Expression> {
    private final Variable.VarType type;
    private String name;

    public GrammarExistingVariable(Variable.VarType type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Expression parseGrammar() throws GrammarException {
        Variable variable = VariableTable.getInstance().get(name);
        if (notNull(variable)) {
            System.out.println(name + " is currently " + variable.getValue());
        } else {
            System.out.println("variable " + name + " does not exist. ");
            return null;
        }

        if (notNull(type) && !type.equals(variable.getType())) except("Expected variable '" + name + "' to be type '"
                + type.name() + "' but was '" + variable.getType().name() + "'", true);
        return new Expression(Expression.Type.Expression, variable.getType());
    }
}
