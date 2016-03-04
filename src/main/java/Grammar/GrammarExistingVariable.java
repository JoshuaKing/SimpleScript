package Grammar;

import classes.Expression;
import classes.Variable;
import compiler.VariableTable;

/**
 * Created by josking on 3/4/16.
 */
public class GrammarExistingVariable extends GrammarRule<Expression> {
    private final Variable.VarType type;

    public GrammarExistingVariable(Variable.VarType type) {
        this.type = type;
    }

    @Override
    public Expression parseGrammar() throws GrammarException {
        String name = required(GrammarName.class);

        Variable variable = VariableTable.getInstance().get(name);
        if (notNull(variable)) {
            System.out.println(name + " is currently " + variable.getValue());
        } else {
            System.err.println(name + " does not exist. ");
            return null;
        }

        return new Expression(Expression.Type.Expression, variable.getType());
    }
}
