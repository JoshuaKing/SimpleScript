package Grammar;

import classes.DebugException;
import classes.Expression;
import classes.Variable;

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
        DebugException error = null;
        try {
            Expression expression = test(new GrammarMethodCall(type));
            if (notNull(expression)) return expression;
        } catch (DebugException e) {
            error = e;
            System.out.println("NEW ERROR: " + error.getError().getMessage());
        }
        try {
            Expression expression = test(new GrammarConstant(type), new GrammarExistingVariable(type));
            if (!notNull(expression)) return null;
            optional(new GrammarSoloOperator(type));
            return expression;
        } catch (DebugException e) {
            error = e.adjust(error);
            System.out.println("NEW ERROR: " + error.getError().getMessage());
        }
        except(error);
        return null;
    }

    private boolean isComparison() throws GrammarException {
        if (notNull(optional(new GrammarDualOperator(type)))) return false;
        required(GrammarComparison.class);
        return true;
    }
}
