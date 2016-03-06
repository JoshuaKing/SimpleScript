package Grammar;

import classes.Expression;
import classes.Variable;
import compiler.VariableTable;

/**
 * Created by Josh on 4/03/2016.
 */
public class GrammarStatement extends GrammarRule<Expression> {
    private Variable.VarType type;

    public GrammarStatement(Variable.VarType type) {
        this.type = type;
    }

    @Override
    // MethodCall | NewInstance | { Constant, Variable } [SoloOp]
    public Expression parseGrammar() throws GrammarException {
        Expression expression = optional(new GrammarConstant(type));
        if (notNull(expression)) return optional(new GrammarSoloOperator(type));

        int repeat = tokens.getIndex();
        String name = required(GrammarName.class);
        reset(repeat);

        if (notNull(VariableTable.getInstance().get(name))) {
            return required(new GrammarExistingVariable(type));
        } else {
            return required(new GrammarMethodCall(type));
        }
    }

    private boolean isComparison() throws GrammarException {
        if (notNull(optional(new GrammarDualOperator(type)))) return false;
        required(GrammarComparison.class);
        return true;
    }
}
